package io.horizontalsystems.lightningkit.remote

import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Status
import java.util.concurrent.Executor

class MacaroonCallCredential(private val macaroon: String) : CallCredentials() {
    override fun thisUsesUnstableApi() {}

    override fun applyRequestMetadata(requestInfo: RequestInfo, executor: Executor, metadataApplier: MetadataApplier) {
        executor.execute {
            try {
                val headers = Metadata()
                val macaroonKey = Metadata.Key.of("macaroon", Metadata.ASCII_STRING_MARSHALLER)
                headers.put(macaroonKey, macaroon)
                metadataApplier.apply(headers)
            } catch (e: Throwable) {
                metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e))
            }
        }
    }
}
