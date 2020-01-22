package io.horizontalsystems.lightningkit.demo.channels

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Channel
import io.horizontalsystems.lightningkit.demo.R

class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val resId = R.layout.view_holder_channel
    }

    private val id = itemView.findViewById<TextView>(R.id.idValue)
    private val localBalance = itemView.findViewById<TextView>(R.id.localBalanceValue)
    private val remoteBalance = itemView.findViewById<TextView>(R.id.remoteBalanceValue)

    fun bind(channel: Channel) {
        id.text = channel.chanId.toString()
        localBalance.text = channel.localBalance.toString()
        remoteBalance.text = channel.remoteBalance.toString()
    }
}
