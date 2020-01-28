package io.horizontalsystems.lightningkit.demo.invoices

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Invoice
import io.horizontalsystems.lightningkit.demo.R

class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val resId = R.layout.view_holder_invoice
    }

    private val description = itemView.findViewById<TextView>(R.id.description)

    fun bind(item: Invoice) {
        description.text = item.run {
            mapOf(
                "Amount" to value,
                "Status" to state.name
            )
        }.map {
            "${it.key}: ${it.value}"
        }.joinToString("\n")
    }
}
