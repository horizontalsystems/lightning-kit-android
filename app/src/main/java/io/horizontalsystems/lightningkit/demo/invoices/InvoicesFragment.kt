package io.horizontalsystems.lightningkit.demo.invoices

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.addinvoice.AddInvoiceActivity

class InvoicesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_invoices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = ViewModelProvider(this, InvoicesModule.Factory()).get(InvoicesPresenter::class.java)
        presenter.onLoad()

        val invoicesAdapter = InvoicesAdapter()

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            val intent = Intent(context, AddInvoiceActivity::class.java)
            startActivity(intent)
        }

        val rvPayments = view.findViewById<RecyclerView>(R.id.invoices)
        rvPayments.adapter = invoicesAdapter

        presenter.invoicesUpdate.observe(viewLifecycleOwner, Observer {
            invoicesAdapter.update(it)
        })

        presenter.invoiceUpdate.observe(viewLifecycleOwner, Observer {
            invoicesAdapter.update(it)
        })
    }
}
