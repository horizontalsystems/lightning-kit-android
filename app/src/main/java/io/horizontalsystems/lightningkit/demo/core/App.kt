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

    fun isWalletSetup(): Boolean {
        return storage.getRemoteLndCredentials() != null || storage.getLocalLndPassword() != null
    }

    fun initLightningKit() {
        when {
            storage.getRemoteLndCredentials() != null -> initLightningKitRemote()
            storage.getLocalLndPassword() != null -> initLightningKitLocal()
            else -> throw Exception("No wallet is setup")
        }
    }

    private fun initLightningKitRemote() {
        val remoteLndCredentials = checkNotNull(storage.getRemoteLndCredentials())

        lightningKit = LightningKit.remote(remoteLndCredentials)
    }

    private fun initLightningKitLocal() {
        val password = checkNotNull(storage.getLocalLndPassword())

        lightningKit = LightningKit.local(filesDir.absolutePath, password)
    }

}
