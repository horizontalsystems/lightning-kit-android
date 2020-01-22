package io.horizontalsystems.lightningkit

import com.github.lightningnetwork.lnd.lnrpc.GetInfoResponse
import com.github.lightningnetwork.lnd.lnrpc.ListChannelsResponse
import com.github.lightningnetwork.lnd.lnrpc.ListPaymentsResponse
import io.reactivex.Single

interface ILndNode {
    fun getInfo(): Single<GetInfoResponse>
    fun listChannels(): Single<ListChannelsResponse>
    fun listPayments(): Single<ListPaymentsResponse>
}
