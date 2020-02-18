package io.horizontalsystems.lightningkit

import com.google.common.io.BaseEncoding
import io.horizontalsystems.lightningkit.remote.RemoteLndCredentials
import java.net.URI

object LndConnect {
    fun parse(lndConnectString: String) : RemoteLndCredentials {
        val uri = URI(lndConnectString)

        check(uri.scheme == "lndconnect") {
            "Parsing is supported only for lndconnect:// urls"
        }

        check(uri.port != -1) {
            "Invalid port"
        }

        var certificate: String? = null
        var macaroon: String? = null

        uri.query.split("&").forEach {
            val (key, value) =  it.split("=")

            when(key) {
                "cert" -> {
                    val certBytes = BaseEncoding.base64Url().decode(value)
                    certificate = BaseEncoding.base64().encode(certBytes)
                }
                "macaroon" -> {
                    val macaroonBytes = BaseEncoding.base64Url().decode(value)
                    macaroon = BaseEncoding.base16().encode(macaroonBytes)
                }
            }
        }

        return RemoteLndCredentials(uri.host, uri.port, checkNotNull(certificate), checkNotNull(macaroon))
    }
}
