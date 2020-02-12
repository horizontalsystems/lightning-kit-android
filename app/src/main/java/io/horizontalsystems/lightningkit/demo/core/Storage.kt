package io.horizontalsystems.lightningkit.demo.core

import android.content.SharedPreferences
import io.horizontalsystems.lightningkit.remote.RemoteLndCredentials

class Storage(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val KEY_REMOTE_LND_CREDENTIALS = "REMOTE_LND_CREDENTIALS"
        private const val KEY_LOCAL_LND_PSWD = "LOCAL_LND_PSWD"
    }

    fun saveRemoteLndCredentials(remoteLndCredentials: RemoteLndCredentials) {
        val serialized = with(remoteLndCredentials) {
            listOf(host, port.toString(), certificate, macaroon).joinToString("|")
        }

        sharedPreferences.edit().putString(KEY_REMOTE_LND_CREDENTIALS, serialized).apply()
    }

    fun getRemoteLndCredentials() : RemoteLndCredentials? {
        return sharedPreferences.getString(KEY_REMOTE_LND_CREDENTIALS, null)?.let {
            val chunks = it.split("|")
            RemoteLndCredentials(chunks[0], chunks[1].toInt(), chunks[2], chunks[3])
        }
    }

    fun saveLocalLndPassword(password: String) {
        sharedPreferences.edit().putString(KEY_LOCAL_LND_PSWD, password).apply()
    }

    fun getLocalLndPassword(): String? {
        return sharedPreferences.getString(KEY_LOCAL_LND_PSWD, null)
    }

    fun clear() {
        sharedPreferences.edit().remove(KEY_REMOTE_LND_CREDENTIALS).apply()
        sharedPreferences.edit().remove(KEY_LOCAL_LND_PSWD).apply()
    }
}
