package io.horizontalsystems.lightningkit

import android.util.Log
import com.github.lightningnetwork.lnd.lnrpc.*
import com.google.protobuf.ByteString
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import lndmobile.Callback
import lndmobile.Lndmobile
import lndmobile.RecvStream
import java.util.*
import java.util.concurrent.TimeUnit

class LocalLnd(private val filesDir: String) : ILndNode {
    private val disposables = CompositeDisposable()

    fun startAndUnlock(password: String) {
        start()
            .flatMap {
                unlockWallet(password)
            }
            .subscribe({
                scheduleStatusUpdates()
            }, {
                status = ILndNode.Status.ERROR(it)
            })
            .let {
                disposables.add(it)
            }
    }

    fun start(): Single<Unit> {
        val args = "--bitcoin.active --bitcoin.node=neutrino --bitcoin.mainnet --routing.assumechanvalid --no-macaroons --lnddir=$filesDir"

        val rpcReady = XCallback({
        }, {
            status = ILndNode.Status.ERROR(it)
        })

        return Single.create<Unit> { emitter ->
            Lndmobile.start(args, XCallback({
                emitter.onSuccess(Unit)
            }, {
                emitter.onError(it)
            }), rpcReady)
        }

    }

    fun scheduleStatusUpdates() {
        disposables.add(Observable.interval(1, TimeUnit.SECONDS)
            .flatMap {
                fetchStatus().toObservable()
            }
            .subscribe {
                status = it
            })
    }

    private fun fetchStatus(): Single<ILndNode.Status> {
        return getInfo()
            .map {
                if (it.syncedToGraph) {
                    ILndNode.Status.RUNNING
                } else {
                    ILndNode.Status.SYNCING
                }
            }
            .onErrorResumeNext { throwable: Throwable ->
                val message = throwable.message?.toLowerCase(Locale.ENGLISH) ?: ""

                val status = if (message.contains("unimplemented")) {
                    ILndNode.Status.LOCKED
//                } else if (message.contains("unavailable") && walletUnlocker.isUnlocking()) {
//                    ILndNode.Status.UNLOCKING
                } else {
                    ILndNode.Status.ERROR(throwable)
                }

                Single.just(status)
            }
    }


    private val statusSubject = PublishSubject.create<ILndNode.Status>()
    override val statusObservable = statusSubject

    var status: ILndNode.Status = ILndNode.Status.CONNECTING
        set(value) {
            Log.e("AAA", "Setting status $value")
            if (field != value) {
                field = value
                statusSubject.onNext(value)
            }
        }

    override fun listClosedChannels(): Single<ClosedChannelsResponse> {
        val request = ClosedChannelsRequest.newBuilder().build()

        return Single.create<ClosedChannelsResponse> {
            Lndmobile.closedChannels(request.toByteArray(), Xxx(it) { ClosedChannelsResponse.parseFrom(it) })
        }
    }

    override fun getOnChainAddress(): Single<NewAddressResponse> {
        val request = NewAddressRequest.newBuilder().build()

        return Single.create<NewAddressResponse> {
            Lndmobile.newAddress(request.toByteArray(), Xxx(it) { NewAddressResponse.parseFrom(it) })
        }
    }

    override fun getInfo(): Single<GetInfoResponse> {
        val request = GetInfoRequest.newBuilder().build()

        return Single.create<GetInfoResponse> {
            Lndmobile.getInfo(request.toByteArray(), Xxx(it) { GetInfoResponse.parseFrom(it) })
        }
    }

    override fun getWalletBalance(): Single<WalletBalanceResponse> {
        val request = WalletBalanceRequest.newBuilder().build()

        return Single.create<WalletBalanceResponse> {
            Lndmobile.walletBalance(request.toByteArray(), Xxx(it) { WalletBalanceResponse.parseFrom(it) })
        }
    }

    override fun getChannelBalance(): Single<ChannelBalanceResponse> {
        val request = ChannelBalanceRequest.newBuilder().build()

        return Single.create<ChannelBalanceResponse> {
            Lndmobile.channelBalance(request.toByteArray(), Xxx(it) { ChannelBalanceResponse.parseFrom(it) })
        }
    }

    override fun listChannels(): Single<ListChannelsResponse> {
        val request = ListChannelsRequest.newBuilder().build()

        return Single.create<ListChannelsResponse> {
            Lndmobile.listChannels(request.toByteArray(), Xxx(it) { ListChannelsResponse.parseFrom(it) })
        }
    }

    override fun listPendingChannels(): Single<PendingChannelsResponse> {
        val request = PendingChannelsRequest.newBuilder().build()

        return Single.create<PendingChannelsResponse> {
            Lndmobile.pendingChannels(request.toByteArray(), Xxx(it) { PendingChannelsResponse.parseFrom(it) })
        }
    }

    override fun closeChannel(channelPoint: String, forceClose: Boolean): Single<CloseStatusUpdate> {
        val channelPointParts = channelPoint.split(':')

        val channelPoint = ChannelPoint.newBuilder()
            .setFundingTxidStr(channelPointParts.first())
            .setOutputIndex(channelPointParts.last().toInt())

        val request = CloseChannelRequest.newBuilder()
            .setChannelPoint(channelPoint)
            .setSatPerByte(2) // todo: extract as param
            .setForce(forceClose)
            .build()

        return Single.create<CloseStatusUpdate> { emitter ->
            Lndmobile.closeChannel(request.toByteArray(), object : RecvStream {
                override fun onResponse(p0: ByteArray?) {
                    emitter.onSuccess(CloseStatusUpdate.parseFrom(p0))
                }

                override fun onError(p0: java.lang.Exception) {
                    emitter.onError(p0)
                }
            })
        }

    }


    override fun listPayments(): Single<ListPaymentsResponse> {
        val request = ListPaymentsRequest.newBuilder().build()

        return Single.create<ListPaymentsResponse> {
            Lndmobile.listPayments(request.toByteArray(), Xxx(it) { ListPaymentsResponse.parseFrom(it) })
        }
    }

    override fun listInvoices(
        pendingOnly: Boolean,
        offset: Long,
        limit: Long,
        reversed: Boolean
    ): Single<ListInvoiceResponse> {
        val request = ListInvoiceRequest
            .newBuilder()
            .setPendingOnly(pendingOnly)
            .setIndexOffset(offset)
            .setNumMaxInvoices(limit)
            .setReversed(reversed)
            .build()

        return Single.create<ListInvoiceResponse> {
            Lndmobile.listInvoices(request.toByteArray(), Xxx(it) { ListInvoiceResponse.parseFrom(it) })
        }
    }

    override fun invoicesObservable(): Observable<Invoice> {
        val request = InvoiceSubscription
            .newBuilder()
            .build()

        return Observable.create<Invoice> { emitter ->
            Lndmobile.subscribeInvoices(request.toByteArray(), object : RecvStream {
                override fun onResponse(p0: ByteArray?) {
                    emitter.onNext(Invoice.parseFrom(p0))
                }

                override fun onError(p0: java.lang.Exception) {
                    emitter.onError(p0)
                }
            })
        }
    }

    override fun channelsObservable(): Observable<ChannelEventUpdate> {
        val channelEventSubscription = ChannelEventSubscription
            .newBuilder()
            .build()

        return Observable.create<ChannelEventUpdate> { emitter ->
            Lndmobile.subscribeChannelEvents(channelEventSubscription.toByteArray(), object : RecvStream {
                override fun onResponse(p0: ByteArray?) {
                    Log.e("AAA", "ChannelEventUpdate: ${p0?.toHexString()}")
                    emitter.onNext(ChannelEventUpdate.parseFrom(p0))
                }

                override fun onError(p0: java.lang.Exception) {
                    emitter.onError(p0)
                }
            })
        }
    }

    override fun payInvoice(invoice: String): Single<SendResponse> {
        val request = SendRequest
            .newBuilder()
            .setPaymentRequest(invoice)
            .build()

        return Single.create<SendResponse> {
            Lndmobile.sendPaymentSync(request.toByteArray(), Xxx(it) { SendResponse.parseFrom(it) })
        }
    }

    override fun addInvoice(amount: Long, memo: String): Single<AddInvoiceResponse> {
        val invoice = Invoice
            .newBuilder()
            .setValue(amount)
            .setMemo(memo)
            .build()

        return Single.create<AddInvoiceResponse> {
            Lndmobile.addInvoice(invoice.toByteArray(), Xxx(it) { AddInvoiceResponse.parseFrom(it) })
        }
    }

    override fun unlockWallet(password: String): Single<Unit> {
        val request = UnlockWalletRequest.newBuilder()
            .setWalletPassword(ByteString.copyFromUtf8(password))
            .build()

        return Single.create<Unit> {
            Lndmobile.unlockWallet(request.toByteArray(), Xxx(it) {
                Unit
            })
        }
    }

    override fun decodePayReq(req: String): Single<PayReq> {
        val request = PayReqString
            .newBuilder()
            .setPayReq(req)
            .build()

        return Single.create<PayReq> {
            Lndmobile.decodePayReq(request.toByteArray(), Xxx(it) { PayReq.parseFrom(it) })
        }
    }

    override fun openChannel(nodePubKey: String, amount: Long): Single<OpenStatusUpdate> {
        val openChannelRequest = OpenChannelRequest.newBuilder()
            .setNodePubkey(ByteString.copyFrom(nodePubKey.hexToByteArray()))
            .setSatPerByte(2) // todo: extract as param
            .setLocalFundingAmount(amount)
            .build()

        return Single.create<OpenStatusUpdate> { emitter ->
            Lndmobile.openChannel(openChannelRequest.toByteArray(), object : RecvStream {
                override fun onResponse(p0: ByteArray?) {
                    emitter.onSuccess(OpenStatusUpdate.parseFrom(p0))
                }

                override fun onError(p0: java.lang.Exception) {
                    emitter.onError(p0)
                }
            })
        }

    }

    override fun connect(nodeAddress: String, nodePubKey: String): Single<ConnectPeerResponse> {
        val lightningAddress = LightningAddress
            .newBuilder()
            .setPubkey(nodePubKey)
            .setHost(nodeAddress)
            .build()

        val request = ConnectPeerRequest
            .newBuilder()
            .setAddr(lightningAddress)
            .build()

        return Single.create<ConnectPeerResponse> {
            Lndmobile.connectPeer(request.toByteArray(), Xxx(it) { ConnectPeerResponse.parseFrom(it) })
        }
    }

    fun createWallet(password: String): Single<List<String>> {
        val request = GenSeedRequest.newBuilder().build()

        return genSeed(request)
            .flatMap { genSeedResponse ->
                initWallet(genSeedResponse.cipherSeedMnemonicList, password)
                    .doOnSuccess {
                        scheduleStatusUpdates()
                    }
                    .map {
                        genSeedResponse.cipherSeedMnemonicList
                    }
            }
    }

    fun restoreWallet(mnemonicList: List<String>, password: String): Single<Unit> {
        return initWallet(mnemonicList, password)
            .doOnSuccess {
                scheduleStatusUpdates()
            }
            .map { Unit }
    }

    private fun initWallet(mnemonicList: List<String>, password: String): Single<InitWalletResponse> {
        val pw = ByteString.copyFromUtf8(password)

        return Single
            .create<InitWalletResponse> { emitter ->
                val initWalletRequest = InitWalletRequest
                    .newBuilder()
                    .setWalletPassword(pw)
                    .addAllCipherSeedMnemonic(mnemonicList)
                    .build()

                Lndmobile.initWallet(
                    initWalletRequest.toByteArray(),
                    Xxx(emitter) { InitWalletResponse.getDefaultInstance() })
            }
    }

    private fun genSeed(request: GenSeedRequest): Single<GenSeedResponse> {
        return Single.create<GenSeedResponse> { emitter ->
            Lndmobile.genSeed(request.toByteArray(), Xxx(emitter) { GenSeedResponse.parseFrom(it) })
        }
    }
}


class Xxx<T>(private val emitter: SingleEmitter<T>, private val parseFrom: (p0: ByteArray?) -> T) : Callback {
    override fun onResponse(p0: ByteArray?) {
        try {
            emitter.onSuccess(parseFrom.invoke(p0))
        } catch (e: java.lang.Exception) {
            emitter.onError(e)
        }
    }

    override fun onError(p0: Exception) {
        emitter.onError(p0)
    }

}

class XCallback(
    private val onResponse: (response: ByteArray?) -> Unit,
    private val onError: (error: java.lang.Exception) -> Unit
) : Callback {
    override fun onResponse(p0: ByteArray?) {
        onResponse.invoke(p0)
    }

    override fun onError(p0: java.lang.Exception) {
        onError.invoke(p0)
    }
}
