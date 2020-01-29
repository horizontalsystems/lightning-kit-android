package io.horizontalsystems.lightningkit

import io.grpc.stub.StreamObserver
import io.reactivex.ObservableEmitter

class StreamObserverToObserver<T>(private val emitter: ObservableEmitter<T>) : StreamObserver<T> {

    override fun onNext(value: T) {
        emitter.onNext(value)
    }

    override fun onError(t: Throwable) {
        emitter.onError(t)
    }

    override fun onCompleted() {
        emitter.onComplete()
    }
}
