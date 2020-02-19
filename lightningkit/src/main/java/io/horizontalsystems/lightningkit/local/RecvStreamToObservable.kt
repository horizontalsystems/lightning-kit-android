package io.horizontalsystems.lightningkit.local

import io.reactivex.ObservableEmitter
import lndmobile.RecvStream

class RecvStreamToObservable<T>(
    private val emitter: ObservableEmitter<T>,
    private val parseMethod: (response: ByteArray?) -> T
) : RecvStream {

    override fun onResponse(p0: ByteArray?) {
        emitter.onNext(parseMethod.invoke(p0))
    }

    override fun onError(p0: java.lang.Exception) {
        emitter.onError(p0)
    }
}
