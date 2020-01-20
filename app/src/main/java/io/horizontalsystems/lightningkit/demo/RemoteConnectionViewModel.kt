package io.horizontalsystems.lightningkit.demo

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RemoteConnectionViewModel : ViewModel() {

    val host = MutableLiveData<String>()
    val port = MutableLiveData<String>()
    val certificate = MutableLiveData<String>()
    val macaroon = MutableLiveData<String>()

    fun connect(view: View) {
        Log.e("AAA", "Connecting to ${host.value}:${port.value}")
        Log.e("AAA", "Certificate: ${certificate.value}")
        Log.e("AAA", "Macaroon: ${macaroon.value}")
    }

}