package io.horizontalsystems.lightningkit.remote

data class RemoteLndCredentials(val host: String, val port: Int, val certificate: String, val macaroon: String)
