package io.horizontalsystems.lightningkit.demo.channels.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.PendingChannelsResponse
import io.horizontalsystems.lightningkit.demo.R

class PendingChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val resId = R.layout.view_holder_pendingchannel
    }

    private val type = itemView.findViewById<TextView>(R.id.typeValue)
    private val channelPoint = itemView.findViewById<TextView>(R.id.channelPoint)
    private val localBalance = itemView.findViewById<TextView>(R.id.localBalanceValue)
    private val remoteBalance = itemView.findViewById<TextView>(R.id.remoteBalanceValue)

    fun bind(channel: PendingChannelsResponse.PendingChannel, type: String) {
        this.type.text = type
        channelPoint.text = channel.channelPoint.toString()
        localBalance.text = channel.localBalance.toString()
        remoteBalance.text = channel.remoteBalance.toString()
    }
}
