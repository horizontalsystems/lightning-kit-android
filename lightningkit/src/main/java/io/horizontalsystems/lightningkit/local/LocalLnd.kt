package io.horizontalsystems.lightningkit.local

import com.github.lightningnetwork.lnd.lnrpc.*
import com.google.protobuf.ByteString
import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.hexToByteArray
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import lndmobile.Callback
import lndmobile.Lndmobile
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class LocalLnd(filesDir: String) : ILndNode {
    private val disposables = CompositeDisposable()
    private val lndDir = "$filesDir/lnd"

    init {
        start()
            .doOnError {
                status = ILndNode.Status.ERROR(it)
            }
            .subscribe()
            .let {
                disposables.add(it)
            }
    }

    private fun start(): Single<Unit> {
        val args = "--bitcoin.active --bitcoin.node=neutrino --bitcoin.mainnet --routing.assumechanvalid --no-macaroons --lnddir=$lndDir"

        val rpcReady = object : Callback {
            override fun onResponse(p0: ByteArray?) {
                scheduleStatusUpdates()
            }

            override fun onError(p0: java.lang.Exception) {
                status = ILndNode.Status.ERROR(p0)
            }
        }

        return Single.create<Unit> { emitter ->
            Lndmobile.start(args, CallbackToSingle<Unit>(emitter) { Unit }, rpcReady)
        }
    }

    private fun scheduleStatusUpdates() {
        Observable.interval(1, TimeUnit.SECONDS)
            .flatMap {
                fetchStatus().toObservable()
            }
            .subscribe {
                status = it
            }.let {
                disposables.add(it)
            }
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

    override var status: ILndNode.Status = ILndNode.Status.CONNECTING
        set(value) {
            if (field != value) {
                field = value
                statusSubject.onNext(value)
            }
        }

    private val statusSubject = BehaviorSubject.createDefault(status)
    override val statusObservable = statusSubject

    override fun listClosedChannels(): Single<ClosedChannelsResponse> {
        val request = ClosedChannelsRequest.newBuilder().build()

        return Single.create<ClosedChannelsResponse> {
            Lndmobile.closedChannels(request.toByteArray(), CallbackToSingle(it) { ClosedChannelsResponse.parseFrom(it) })
        }
    }

    override fun getOnChainAddress(): Single<NewAddressResponse> {
        val request = NewAddressRequest.newBuilder()
            .setType(AddressType.UNUSED_WITNESS_PUBKEY_HASH)
            .build()

        return Single.create<NewAddressResponse> {
            Lndmobile.newAddress(request.toByteArray(), CallbackToSingle(it) { NewAddressResponse.parseFrom(it) })
        }
    }

    override fun getInfo(): Single<GetInfoResponse> {
        val request = GetInfoRequest.newBuilder().build()

        return Single.create<GetInfoResponse> {
            Lndmobile.getInfo(request.toByteArray(), CallbackToSingle(it) { GetInfoResponse.parseFrom(it) })
        }
    }

    override fun getWalletBalance(): Single<WalletBalanceResponse> {
        val request = WalletBalanceRequest.newBuilder().build()

        return Single.create<WalletBalanceResponse> {
            Lndmobile.walletBalance(request.toByteArray(), CallbackToSingle(it) { WalletBalanceResponse.parseFrom(it) })
        }
    }

    override fun getChannelBalance(): Single<ChannelBalanceResponse> {
        val request = ChannelBalanceRequest.newBuilder().build()

        return Single.create<ChannelBalanceResponse> {
            Lndmobile.channelBalance(request.toByteArray(), CallbackToSingle(it) { ChannelBalanceResponse.parseFrom(it) })
        }
    }

    override fun listChannels(): Single<ListChannelsResponse> {
        val request = ListChannelsRequest.newBuilder().build()

        return Single.create<ListChannelsResponse> {
            Lndmobile.listChannels(request.toByteArray(), CallbackToSingle(it) { ListChannelsResponse.parseFrom(it) })
        }
    }

    override fun listPendingChannels(): Single<PendingChannelsResponse> {
        val request = PendingChannelsRequest.newBuilder().build()

        return Single.create<PendingChannelsResponse> {
            Lndmobile.pendingChannels(request.toByteArray(), CallbackToSingle(it) { PendingChannelsResponse.parseFrom(it) })
        }
    }

    override fun closeChannel(channelPoint: String, forceClose: Boolean): Observable<CloseStatusUpdate> {
        val channelPointParts = channelPoint.split(':')

        val channelPoint = ChannelPoint.newBuilder()
            .setFundingTxidStr(channelPointParts.first())
            .setOutputIndex(channelPointParts.last().toInt())

        val request = CloseChannelRequest.newBuilder()
            .setChannelPoint(channelPoint)
            .setSatPerByte(2) // todo: extract as param
            .setForce(forceClose)
            .build()

        return Observable.create<CloseStatusUpdate> {
            Lndmobile.closeChannel(request.toByteArray(), RecvStreamToObservable(it) { CloseStatusUpdate.parseFrom(it) })
        }

    }

    override fun listPayments(): Single<ListPaymentsResponse> {
        val request = ListPaymentsRequest.newBuilder().build()

        return Single.create<ListPaymentsResponse> {
            Lndmobile.listPayments(request.toByteArray(), CallbackToSingle(it) { ListPaymentsResponse.parseFrom(it) })
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
            Lndmobile.listInvoices(request.toByteArray(), CallbackToSingle(it) { ListInvoiceResponse.parseFrom(it) })
        }
    }

    override fun getTransactions(): Single<TransactionDetails> {
        val request = GetTransactionsRequest
            .newBuilder()
            .build()

        return Single.create<TransactionDetails> {
            Lndmobile.getTransactions(request.toByteArray(), CallbackToSingle(it) { TransactionDetails.parseFrom(it) })
        }
    }

    override fun invoicesObservable(): Observable<Invoice> {
        val request = InvoiceSubscription
            .newBuilder()
            .build()

        return Observable.create<Invoice> {
            Lndmobile.subscribeInvoices(request.toByteArray(), RecvStreamToObservable(it) { Invoice.parseFrom(it) })
        }
    }

    override fun channelsObservable(): Observable<ChannelEventUpdate> {
        val subscription = ChannelEventSubscription
            .newBuilder()
            .build()

        return Observable.create<ChannelEventUpdate> {
            Lndmobile.subscribeChannelEvents(subscription.toByteArray(), RecvStreamToObservable(it) { ChannelEventUpdate.parseFrom(it) })
        }
    }

    override fun transactionsObservable(): Observable<Transaction> {
        val request = GetTransactionsRequest
            .newBuilder()
            .build()

        return Observable.create<Transaction> {
            Lndmobile.subscribeTransactions(request.toByteArray(), RecvStreamToObservable(it) { Transaction.parseFrom(it) })
        }
    }

    override fun payInvoice(invoice: String): Single<SendResponse> {
        val request = SendRequest
            .newBuilder()
            .setPaymentRequest(invoice)
            .build()

        return Single.create<SendResponse> {
            Lndmobile.sendPaymentSync(request.toByteArray(), CallbackToSingle(it) { SendResponse.parseFrom(it) })
        }
    }

    override fun addInvoice(amount: Long, memo: String): Single<AddInvoiceResponse> {
        val invoice = Invoice
            .newBuilder()
            .setValue(amount)
            .setMemo(memo)
            .build()

        return Single.create<AddInvoiceResponse> {
            Lndmobile.addInvoice(invoice.toByteArray(), CallbackToSingle(it) { AddInvoiceResponse.parseFrom(it) })
        }
    }

    override fun unlockWallet(password: String): Single<Unit> {
        val request = UnlockWalletRequest.newBuilder()
            .setWalletPassword(ByteString.copyFromUtf8(password))
            .build()

        return Single.create<Unit> {
            Lndmobile.unlockWallet(request.toByteArray(), CallbackToSingle(it) { Unit })
        }
    }

    override fun unlockWalletBlocking(password: String) {
        unlockWallet(password)
            .subscribe()
            .let {
                disposables.add(it)
            }
    }

    override fun decodePayReq(req: String): Single<PayReq> {
        val request = PayReqString
            .newBuilder()
            .setPayReq(req)
            .build()

        return Single.create<PayReq> {
            Lndmobile.decodePayReq(request.toByteArray(), CallbackToSingle(it) { PayReq.parseFrom(it) })
        }
    }

    override fun openChannel(nodePubKey: String, amount: Long): Observable<OpenStatusUpdate> {
        val request = OpenChannelRequest.newBuilder()
            .setNodePubkey(ByteString.copyFrom(nodePubKey.hexToByteArray()))
            .setSatPerByte(2) // todo: extract as param
            .setLocalFundingAmount(amount)
            .build()

        return Observable.create<OpenStatusUpdate> {
            Lndmobile.openChannel(request.toByteArray(), RecvStreamToObservable(it) { OpenStatusUpdate.parseFrom(it) })
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
            Lndmobile.connectPeer(request.toByteArray(), CallbackToSingle(it) { ConnectPeerResponse.parseFrom(it) })
        }
    }

    override fun initWallet(mnemonicList: List<String>, password: String): Single<InitWalletResponse> {
        val request = InitWalletRequest
            .newBuilder()
            .setWalletPassword(ByteString.copyFromUtf8(password))
            .addAllCipherSeedMnemonic(mnemonicList)
            .build()

        return Single.create<InitWalletResponse> { emitter ->
            Lndmobile.initWallet(request.toByteArray(), CallbackToSingle(emitter) { InitWalletResponse.getDefaultInstance() })
        }
    }

    override fun genSeed(): Single<GenSeedResponse> {
        val request = GenSeedRequest.newBuilder().build()

        return Single.create<GenSeedResponse> { emitter ->
            Lndmobile.genSeed(request.toByteArray(), CallbackToSingle(emitter) { GenSeedResponse.parseFrom(it) })
        }
    }

    override fun logout(): Single<Unit> {
        return Single.create<Unit> { emitter ->
            Lndmobile.stopDaemon(StopRequest.newBuilder().build().toByteArray(), CallbackToSingle(emitter) { Unit })
        }.doOnSuccess {
            disposables.clear()
            deleteRecursive(File(lndDir))
        }
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { child ->
                deleteRecursive(child)
            }
        }

        fileOrDirectory.delete()
    }
}
