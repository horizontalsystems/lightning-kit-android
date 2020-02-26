package io.horizontalsystems.lightningkit.demo.payments

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Payment
import io.horizontalsystems.lightningkit.demo.R

class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val resId = R.layout.view_holder_item
    }

    private val description = itemView.findViewById<TextView>(R.id.description)

    fun bind(item: Payment) {
        description.text = item.run {
            mapOf(
                "Amount" to valueSat,
                "Status" to status.name
            )
        }.map {
            "${it.key}: ${it.value}"
        }.joinToString("\n")
    }
}
