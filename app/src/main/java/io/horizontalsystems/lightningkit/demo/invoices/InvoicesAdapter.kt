package io.horizontalsystems.lightningkit.demo.invoices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Invoice

class InvoicesAdapter : RecyclerView.Adapter<InvoiceViewHolder>() {
    private var items: List<Invoice> = listOf()

    fun update(items: List<Invoice>) {
        this.items = items

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(InvoiceViewHolder.resId, parent, false)
        return InvoiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
