package io.horizontalsystems.lightningkit.demo.transactions

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.lightningnetwork.lnd.lnrpc.Transaction
import io.horizontalsystems.lightningkit.demo.R
import java.text.DateFormat
import java.util.*

class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val resId = R.layout.view_holder_item
        val dateFormatter = DateFormat.getDateTimeInstance(
            DateFormat.DEFAULT,
            DateFormat.DEFAULT,
            Locale.US
        )
    }

    private val description = itemView.findViewById<TextView>(R.id.description)

    fun bind(item: Transaction) {
        description.text = item.run {
            mapOf(
                "Amount" to amount,
                "Date" to dateFormatter.format(Date(timeStamp * 1000)),
                "TxHash" to txHash
            )
        }.map {
            "${it.key}: ${it.value}"
        }.joinToString("\n")
    }
}
