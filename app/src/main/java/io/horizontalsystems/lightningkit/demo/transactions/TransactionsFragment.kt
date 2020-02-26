package io.horizontalsystems.lightningkit.demo.transactions

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

class TransactionsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = ViewModelProvider(this, TransactionsModule.Factory()).get(TransactionsPresenter::class.java)
        presenter.onLoad()

        val transactionsAdapter = TransactionsAdapter()

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
//            val intent = Intent(context, PayActivity::class.java)
//            startActivity(intent)
        }

        val rvPayments = view.findViewById<RecyclerView>(R.id.items)
        rvPayments.adapter = transactionsAdapter

        presenter.transactions.observe(viewLifecycleOwner, Observer {
            transactionsAdapter.update(it)
        })
    }
}
