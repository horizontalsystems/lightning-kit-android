package io.horizontalsystems.lightningkit.local

import io.reactivex.SingleEmitter
import lndmobile.Callback

class CallbackToSingle<T>(private val emitter: SingleEmitter<T>, private val parseFrom: (p0: ByteArray?) -> T) : Callback {
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
