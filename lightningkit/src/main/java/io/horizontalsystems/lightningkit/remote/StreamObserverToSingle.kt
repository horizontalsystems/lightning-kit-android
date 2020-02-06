package io.horizontalsystems.lightningkit.remote

import io.grpc.stub.StreamObserver
import io.reactivex.SingleEmitter

class StreamObserverToSingle<T>(private val emitter: SingleEmitter<T>) : StreamObserver<T> {

    override fun onNext(value: T) {
        emitter.onSuccess(value)
    }

    override fun onError(t: Throwable) {
        emitter.onError(t)
    }

    override fun onCompleted() {}
}
