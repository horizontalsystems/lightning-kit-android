package io.horizontalsystems.lightningkit.demo.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Transaction
import io.horizontalsystems.lightningkit.demo.payments.PaymentViewHolder

class TransactionsAdapter : RecyclerView.Adapter<TransactionViewHolder>() {
    private var items: List<Transaction> = listOf()

    fun update(items: List<Transaction>) {
        this.items = items

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(PaymentViewHolder.resId, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
