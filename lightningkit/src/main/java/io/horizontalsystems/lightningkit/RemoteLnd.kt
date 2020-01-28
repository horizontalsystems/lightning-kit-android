package io.horizontalsystems.lightningkit

import android.util.Base64
import com.github.lightningnetwork.lnd.lnrpc.*
import io.grpc.okhttp.OkHttpChannelBuilder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class RemoteLnd(host: String, port: Int, cert: String, macaroon: String) : ILndNode {

    private val statusSubject = PublishSubject.create<ILndNode.Status>()
    override val statusObservable = statusSubject

    var status: ILndNode.Status = ILndNode.Status.CONNECTING
        set(value) {
            if (field != value) {
                field = value
                statusSubject.onNext(value)
            }
        }

    private val sslCertStr = Base64.decode(cert, Base64.DEFAULT)
    private val sslFactory = CustomSSLSocketFactory.create(sslCertStr)

    private val macaroonCallCredential = MacaroonCallCredential(macaroon)
    private val channel = OkHttpChannelBuilder
        .forAddress(host, port)
        .sslSocketFactory(sslFactory)
        .build()

    private val asyncStub = LightningGrpc.newStub(channel).withCallCredentials(macaroonCallCredential)
    private var walletUnlocker = WalletUnlocker(channel, macaroonCallCredential)
    private val disposables = CompositeDisposable()

    fun scheduleStatusUpdates() {
        disposables.add(Observable.interval(1, TimeUnit.SECONDS)
            .flatMap {
                fetchStatus().toObservable()
            }
            .subscribe {
                status = it
            })
    }

    override fun getInfo(): Single<GetInfoResponse> {
        val request = GetInfoRequest.newBuilder().build()

        return Single.create<GetInfoResponse> { asyncStub.getInfo(request, StreamObserverToSingle(it)) }
    }

    override fun listChannels(): Single<ListChannelsResponse> {
        val request = ListChannelsRequest.newBuilder().build()

        return Single.create<ListChannelsResponse> { asyncStub.listChannels(request, StreamObserverToSingle(it)) }
    }

    override fun payInvoice(invoice: String): Single<SendResponse> {
        val requestBuilder = SendRequest.newBuilder()
        requestBuilder.setPaymentRequest(invoice)

        val request = requestBuilder.build()
        return Single.create<SendResponse> { asyncStub.sendPaymentSync(request, StreamObserverToSingle(it)) }
    }

    override fun unlockWallet(password: String): Single<Unit> {
        if (walletUnlocker.isUnlocking()) {
            return Single.error(WalletUnlocker.UnlockingException)
        }

        return walletUnlocker.startUnlock(password)
    }

    override fun listPayments(): Single<ListPaymentsResponse> {
        val request = ListPaymentsRequest.newBuilder().build()

        return Single.create<ListPaymentsResponse> { asyncStub.listPayments(request, StreamObserverToSingle(it)) }
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

        return Single.create<ListInvoiceResponse> { asyncStub.listInvoices(request, StreamObserverToSingle(it)) }
    }

    fun validateAsync(): Single<Unit> {
        return fetchStatus()
            .flatMap {
                if (it is ILndNode.Status.ERROR) {
                    Single.error(it.throwable)
                } else {
                    Single.just(Unit)
                }
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
                } else if (message.contains("unavailable") && walletUnlocker.isUnlocking()) {
                    ILndNode.Status.UNLOCKING
                } else {
                    ILndNode.Status.ERROR(throwable)
                }

                Single.just(status)
            }
    }
}
