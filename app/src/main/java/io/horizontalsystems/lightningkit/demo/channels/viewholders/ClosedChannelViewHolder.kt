package io.horizontalsystems.lightningkit.demo.channels.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.ChannelCloseSummary
import io.horizontalsystems.lightningkit.demo.R

class ClosedChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val resId = R.layout.view_holder_closedchannel
    }

    private val id = itemView.findViewById<TextView>(R.id.idValue)
    private val capacity = itemView.findViewById<TextView>(R.id.capacityValue)
    private val settledBalance = itemView.findViewById<TextView>(R.id.settledBalanceValue)

    fun bind(closedChannel: ChannelCloseSummary) {
        id.text = closedChannel.chanId.toString()
        capacity.text = closedChannel.capacity.toString()
        settledBalance.text = closedChannel.settledBalance.toString()
    }
}
