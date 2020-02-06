package io.horizontalsystems.lightningkit.remote

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

class CustomSSLSocketFactory {
    companion object {
        fun create(certificate: ByteArray?): SSLSocketFactory {
            var caInput: InputStream? = null
            return try { // Generate the CA Certificate from the supplied byte array
                caInput = ByteArrayInputStream(certificate)
                val ca = CertificateFactory.getInstance("X.509").generateCertificate(caInput)
                // Load the key store using the CA
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null, null)
                keyStore.setCertificateEntry("ca", ca)
                // Initialize the TrustManager with this CA
                val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                tmf.init(keyStore)
                // Create an SSL context that uses the created trust manager
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, tmf.trustManagers, SecureRandom())
                sslContext.socketFactory
            } catch (ex: Exception) {
                throw RuntimeException(ex)
            } finally {
                if (caInput != null) {
                    try {
                        caInput.close()
                    } catch (ignored: IOException) {
                    }
                }
            }
        }
    }
}
