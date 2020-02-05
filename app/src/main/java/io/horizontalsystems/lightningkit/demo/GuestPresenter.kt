package io.horizontalsystems.lightningkit.demo

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class GuestPresenter : ViewModel() {
    val openRemoteConnectionLiveEvent = SingleLiveEvent<Void>()
    val openCreateLiveEvent = SingleLiveEvent<Void>()

    fun create(view: View) {
        openCreateLiveEvent.call()
    }

    fun restore(view: View) {
        Log.e("AAA", "restoring...")
    }

    fun remote(view: View) {
        openRemoteConnectionLiveEvent.call()
    }
}
