package io.horizontalsystems.lightningkit.demo.channels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Channel
import com.github.lightningnetwork.lnd.lnrpc.ChannelCloseSummary
import com.github.lightningnetwork.lnd.lnrpc.PendingChannelsResponse
import io.horizontalsystems.lightningkit.demo.channels.viewholders.*


class ChannelsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_CHANNEL = 1
        const val VIEW_TYPE_CLOSED_CHANNEL_SUMMARY = 2
        const val VIEW_TYPE_PENDING_OPEN_CHANNEL = 3
        const val VIEW_TYPE_PENDING_CLOSING_CHANNEL = 4
        const val VIEW_TYPE_PENDING_FORCE_CLOSING_CHANNEL = 5
        const val VIEW_TYPE_PENDING_WAITING_CLOSE_CHANNEL = 6
    }

    private class ChannelViewItem(
        val title: String,
        val channel: Channel? = null,
        val closedChannel: ChannelCloseSummary? = null,
        val pendingOpenChannel: PendingChannelsResponse.PendingOpenChannel? = null,
        val pendingClosingChannel: PendingChannelsResponse.ClosedChannel? = null,
        val pendingForceClosingChannel: PendingChannelsResponse.ForceClosedChannel? = null,
        val pendingWaitingCloseChannel: PendingChannelsResponse.WaitingCloseChannel? = null
    )

    private var channelViewItems: List<ChannelViewItem> = listOf()

    private fun updateItems(
        channels: List<Channel>,
        closedChannels: List<ChannelCloseSummary>,
        pendingOpenChannels: List<PendingChannelsResponse.PendingOpenChannel>,
        pendingClosingChannels: List<PendingChannelsResponse.ClosedChannel>,
        pendingForceClosingChannels: List<PendingChannelsResponse.ForceClosedChannel>,
        pendingWaitingCloseChannels: List<PendingChannelsResponse.WaitingCloseChannel>
    ) {
        val channelViewItems: MutableList<ChannelViewItem> = mutableListOf()

        channelViewItems.add(ChannelViewItem("Channels"))
        channels.forEach { channelViewItems.add(ChannelViewItem("", it)) }

        channelViewItems.add(ChannelViewItem("Pending Channels"))
        pendingOpenChannels.forEach {
            channelViewItems.add(ChannelViewItem("", null, null, it))
        }
        pendingClosingChannels.forEach {
            channelViewItems.add(ChannelViewItem("", null, null, null, it))
        }
        pendingForceClosingChannels.forEach {
            channelViewItems.add(
                ChannelViewItem("", null, null, null, null, it)
            )
        }
        pendingWaitingCloseChannels.forEach {
            channelViewItems.add(
                ChannelViewItem("", null, null, null, null, null, it)
            )
        }

        channelViewItems.add(ChannelViewItem("Closed Channels"))
        closedChannels.forEach { channelViewItems.add(ChannelViewItem("", null, it)) }

        this.channelViewItems = channelViewItems
        notifyDataSetChanged()
    }

    fun updateChannels(channels: List<Channel>) {
        updateItems(
            channels, channelViewItems.mapNotNull { it.closedChannel },
            channelViewItems.mapNotNull { it.pendingOpenChannel }, channelViewItems.mapNotNull { it.pendingClosingChannel },
            channelViewItems.mapNotNull { it.pendingForceClosingChannel }, channelViewItems.mapNotNull { it.pendingWaitingCloseChannel }
        )
    }

    fun updateClosedChannels(closedChannels: List<ChannelCloseSummary>) {
        updateItems(
            channelViewItems.mapNotNull { it.channel }, closedChannels,
            channelViewItems.mapNotNull { it.pendingOpenChannel }, channelViewItems.mapNotNull { it.pendingClosingChannel },
            channelViewItems.mapNotNull { it.pendingForceClosingChannel }, channelViewItems.mapNotNull { it.pendingWaitingCloseChannel }
        )
    }

    fun updatePendingChannels(pendingChannelsResponse: PendingChannelsResponse) {
        updateItems(
            channelViewItems.mapNotNull { it.channel }, channelViewItems.mapNotNull { it.closedChannel },
            pendingChannelsResponse.pendingOpenChannelsList, pendingChannelsResponse.pendingClosingChannelsList,
            pendingChannelsResponse.pendingForceClosingChannelsList, pendingChannelsResponse.waitingCloseChannelsList
        )
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
            channelViewItems[position].pendingOpenChannel != null -> {
                VIEW_TYPE_PENDING_OPEN_CHANNEL
            }
            channelViewItems[position].pendingClosingChannel != null -> {
                VIEW_TYPE_PENDING_CLOSING_CHANNEL
            }
            channelViewItems[position].pendingForceClosingChannel != null -> {
                VIEW_TYPE_PENDING_FORCE_CLOSING_CHANNEL
            }
            channelViewItems[position].pendingWaitingCloseChannel != null -> {
                VIEW_TYPE_PENDING_WAITING_CLOSE_CHANNEL
            }
            else -> {
                0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CHANNEL -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(ChannelViewHolder.resId, parent, false)
                ChannelViewHolder(itemView)
            }
            VIEW_TYPE_CLOSED_CHANNEL_SUMMARY -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(ClosedChannelViewHolder.resId, parent, false)
                ClosedChannelViewHolder(itemView)
            }
            VIEW_TYPE_PENDING_OPEN_CHANNEL,
            VIEW_TYPE_PENDING_CLOSING_CHANNEL,
            VIEW_TYPE_PENDING_FORCE_CLOSING_CHANNEL,
            VIEW_TYPE_PENDING_WAITING_CLOSE_CHANNEL -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(PendingChannelViewHolder.resId, parent, false)
                PendingChannelViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(ChannelSectionTitleViewHolder.resId, parent, false)
                ChannelSectionTitleViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val channelViewItem = channelViewItems[position]

        channelViewItem.channel?.let {
            (holder as ChannelViewHolder).bind(it)
        } ?: channelViewItem.closedChannel?.let {
            (holder as ClosedChannelViewHolder).bind(it)
        } ?: channelViewItem.pendingOpenChannel?.let {
            (holder as PendingChannelViewHolder).bind(it.channel, "Opening")
        } ?: channelViewItem.pendingClosingChannel?.let {
            (holder as PendingChannelViewHolder).bind(it.channel, "Closing")
        } ?: channelViewItem.pendingForceClosingChannel?.let {
            (holder as PendingChannelViewHolder).bind(it.channel, "Force Closing")
        } ?: channelViewItem.pendingWaitingCloseChannel?.let {
            (holder as PendingChannelViewHolder).bind(it.channel, "Waiting Close")
        } ?: (holder as ChannelSectionTitleViewHolder).bind(channelViewItem.title)
    }
}
