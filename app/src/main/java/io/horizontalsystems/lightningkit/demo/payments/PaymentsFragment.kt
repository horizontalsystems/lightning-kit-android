package io.horizontalsystems.lightningkit.demo.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.horizontalsystems.lightningkit.demo.R

class PaymentsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = ViewModelProvider(this, PaymentsModule.Factory()).get(PaymentsPresenter::class.java)
        presenter.onLoad()

        val paymentsAdapter = PaymentsAdapter()

        val rvPayments = view.findViewById<RecyclerView>(R.id.payments)
        rvPayments.adapter = paymentsAdapter

        presenter.payments.observe(viewLifecycleOwner, Observer {
            paymentsAdapter.update(it)
        })
    }
}
