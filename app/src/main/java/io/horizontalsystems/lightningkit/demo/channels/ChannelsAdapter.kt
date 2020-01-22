package io.horizontalsystems.lightningkit.demo.channels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Channel

class ChannelsAdapter : RecyclerView.Adapter<ChannelViewHolder>() {
    private var channels: List<Channel> = listOf()

    fun updateChannels(channels: List<Channel>) {
        this.channels = channels

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(ChannelViewHolder.resId, parent, false)
        return ChannelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(channels[position])
    }
}
