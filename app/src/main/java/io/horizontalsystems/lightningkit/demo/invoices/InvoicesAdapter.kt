package io.horizontalsystems.lightningkit.demo.invoices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Invoice

class InvoicesAdapter : RecyclerView.Adapter<InvoiceViewHolder>() {
    private var items: MutableList<Invoice> = mutableListOf()

    fun update(items: List<Invoice>) {
        this.items = items.toMutableList()

        notifyDataSetChanged()
    }

    fun update(item: Invoice) {
        val itemIndex = items.indexOfFirst {
            it.rHash == item.rHash
        }

        if (itemIndex > 0) {
            items[itemIndex] = item

            notifyItemChanged(itemIndex)
        }
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
