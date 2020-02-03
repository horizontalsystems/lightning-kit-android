package io.horizontalsystems.lightningkit.demo.channels.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.R.layout.view_holder_channel_section_title

class ChannelSectionTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val resId = view_holder_channel_section_title
    }

    private val title = itemView.findViewById<TextView>(R.id.title)

    fun bind(title: String) {
        this.title.text = title
    }
}
