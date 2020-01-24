package io.horizontalsystems.lightningkit

import android.util.Base64
import com.github.lightningnetwork.lnd.lnrpc.*
import com.google.protobuf.ByteString
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
    private val asyncWalletStub = WalletUnlockerGrpc.newStub(channel).withCallCredentials(macaroonCallCredential)
    private val disposables = CompositeDisposable()

    fun scheduleStatusUpdates() {
        disposables.add(Observable.interval(1, TimeUnit.SECONDS)
            .flatMap {
                getInfo()
                    .map {
                        if (it.syncedToGraph) {
                            ILndNode.Status.RUNNING
                        } else {
                            ILndNode.Status.SYNCING
                        }
                    }
                    .onErrorResumeNext { throwable: Throwable ->
                        val message = throwable.message ?: ""

                        val status = if (message.toLowerCase(Locale.ENGLISH).contains("unimplemented")) {
                            ILndNode.Status.LOCKED
                        } else {
                            ILndNode.Status.ERROR
                        }

                        Single.just(status)
                    }
                    .toObservable()
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

    override fun unlockWallet(password: String): Single<UnlockWalletResponse> {
        val request = UnlockWalletRequest
            .newBuilder()
            .setWalletPassword(ByteString.copyFromUtf8(password))
            .build()

        return Single.create<UnlockWalletResponse> { asyncWalletStub.unlockWallet(request, StreamObserverToSingle(it)) }
    }

    override fun listPayments(): Single<ListPaymentsResponse> {
        val request = ListPaymentsRequest.newBuilder().build()

        return Single.create<ListPaymentsResponse> { asyncStub.listPayments(request, StreamObserverToSingle(it)) }
    }

    fun validateAsync(): Single<Unit> {
        return getInfo()
            .map { Unit }
            .onErrorResumeNext { throwable: Throwable ->
                val message = throwable.message ?: ""

                if (message.toLowerCase(Locale.ENGLISH).contains("unimplemented")) {
                    Single.just(Unit)
                } else {
                    Single.error(throwable)
                }
            }

    }
}
