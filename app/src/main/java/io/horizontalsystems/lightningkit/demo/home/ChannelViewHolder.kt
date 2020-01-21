package io.horizontalsystems.lightningkit.demo.home

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Channel
import io.horizontalsystems.lightningkit.demo.R

class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val channelPoint = itemView.findViewById<TextView>(R.id.channelPoint)

    fun bind(channel: Channel) {
        channelPoint.text = channel.channelPoint
    }
}