package io.horizontalsystems.lightningkit.demo.core

import android.app.Application
import android.preference.PreferenceManager
import io.horizontalsystems.lightningkit.LightningKit

class App : Application() {
    companion object {
        lateinit var instance: App
            private set

        lateinit var storage: Storage

        lateinit var lightningKit: LightningKit

    }
    override fun onCreate() {
        super.onCreate()

        storage = Storage(PreferenceManager.getDefaultSharedPreferences(this))

        instance = this
    }

    fun initLightningKit() {
        val connectionParams = storage.getConnectionParams()
        checkNotNull(connectionParams)

        lightningKit = LightningKit.remote(connectionParams.host, connectionParams.port, connectionParams.certificate, connectionParams.macaroon)
    }

    fun isWalletSetup(): Boolean {
        return storage.getConnectionParams() != null
    }

}
