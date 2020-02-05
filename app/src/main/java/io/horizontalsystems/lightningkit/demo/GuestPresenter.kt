package io.horizontalsystems.lightningkit.demo

import android.view.View
import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.demo.core.SingleLiveEvent

class GuestPresenter : ViewModel() {
    val openRemoteConnectionLiveEvent = SingleLiveEvent<Void>()
    val openCreateLiveEvent = SingleLiveEvent<Void>()
    val openRestoreLiveEvent = SingleLiveEvent<Void>()

    fun create(view: View) {
        openCreateLiveEvent.call()
    }

    fun restore(view: View) {
        openRestoreLiveEvent.call()
    }

    fun remote(view: View) {
        openRemoteConnectionLiveEvent.call()
    }
}
