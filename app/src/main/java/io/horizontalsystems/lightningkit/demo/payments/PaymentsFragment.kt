package io.horizontalsystems.lightningkit.demo.payments

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
import io.horizontalsystems.lightningkit.demo.send.PayActivity

class PaymentsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = ViewModelProvider(this, PaymentsModule.Factory()).get(PaymentsPresenter::class.java)
        presenter.onLoad()

        val paymentsAdapter = PaymentsAdapter()

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            val intent = Intent(context, PayActivity::class.java)
            startActivity(intent)
        }

        val rvPayments = view.findViewById<RecyclerView>(R.id.payments)
        rvPayments.adapter = paymentsAdapter

        presenter.payments.observe(viewLifecycleOwner, Observer {
            paymentsAdapter.update(it)
        })
    }
}
