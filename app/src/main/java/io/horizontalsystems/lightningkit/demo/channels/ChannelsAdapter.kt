package io.horizontalsystems.lightningkit.demo.channels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Channel
import com.github.lightningnetwork.lnd.lnrpc.ChannelCloseSummary
import io.horizontalsystems.lightningkit.demo.channels.viewholders.*


class ChannelsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_CHANNEL = 1
        const val VIEW_TYPE_CLOSED_CHANNEL_SUMMARY = 2
    }

    private class ChannelViewItem(val title: String, val channel: Channel? = null, val closedChannel: ChannelCloseSummary? = null)

    private var channelViewItems: List<ChannelViewItem> = listOf()

    private fun updateItems(channels: List<Channel>, closedChannels: List<ChannelCloseSummary>) {
        var channelViewItems: MutableList<ChannelViewItem> = mutableListOf()

        channelViewItems.add(ChannelViewItem("Channels"))
        channels.forEach { channelViewItems.add(ChannelViewItem("", it)) }

        channelViewItems.add(ChannelViewItem("ClosedChannels"))
        closedChannels.forEach { channelViewItems.add(ChannelViewItem("", null, it)) }

        this.channelViewItems = channelViewItems
        notifyDataSetChanged()
    }

    fun updateChannels(channels: List<Channel>) {
        updateItems(channels, channelViewItems.mapNotNull { it.closedChannel })
    }

    fun updateClosedChannels(closedChannels: List<ChannelCloseSummary>) {
        updateItems(channelViewItems.mapNotNull { it.channel }, closedChannels)
    }

    override fun getItemCount(): Int {
        return channelViewItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            channelViewItems[position].channel != null -> {
                VIEW_TYPE_CHANNEL
            }
            channelViewItems[position].closedChannel != null -> {
                VIEW_TYPE_CLOSED_CHANNEL_SUMMARY
            }
            else -> {
                0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CHANNEL -> {
                val itemView = LayoutInflater.from(parent.context).inflate(ChannelViewHolder.resId, parent, false)
                ChannelViewHolder(itemView)
            }
            VIEW_TYPE_CLOSED_CHANNEL_SUMMARY -> {
                val itemView = LayoutInflater.from(parent.context).inflate(ClosedChannelViewHolder.resId, parent, false)
                ClosedChannelViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(ChannelSectionTitleViewHolder.resId, parent, false)
                ChannelSectionTitleViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChannelViewHolder -> channelViewItems[position].channel?.let { holder.bind(it) }
            is ClosedChannelViewHolder -> channelViewItems[position].closedChannel?.let { holder.bind(it) }
            else -> (holder as ChannelSectionTitleViewHolder).bind(channelViewItems[position].title)
        }
    }
}
