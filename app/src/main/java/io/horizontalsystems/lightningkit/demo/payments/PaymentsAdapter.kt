package io.horizontalsystems.lightningkit.demo.payments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Payment

class PaymentsAdapter : RecyclerView.Adapter<PaymentViewHolder>() {
    private var items: List<Payment> = listOf()

    fun update(items: List<Payment>) {
        this.items = items

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(PaymentViewHolder.resId, parent, false)
        return PaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
