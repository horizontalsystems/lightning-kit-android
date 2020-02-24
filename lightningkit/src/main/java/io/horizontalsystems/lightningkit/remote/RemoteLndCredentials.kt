package io.horizontalsystems.lightningkit.remote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RemoteLndCredentials(
        val host: String,
        val port: Int,
        val certificate: String,
        val macaroon: String
) : Parcelable
