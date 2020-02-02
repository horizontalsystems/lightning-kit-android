
package io.horizontalsystems.lightningkit.demo.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.R

class BalanceFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = ViewModelProvider(this, BalanceModule.Factory()).get(BalancePresenter::class.java)
        presenter.onLoad()

        val totalBalanceTextView = view.findViewById<TextView>(R.id.totalBalance)
        val confirmedBalanceTextView = view.findViewById<TextView>(R.id.confirmedBalance)
        val unconfirmedBalanceTextView = view.findViewById<TextView>(R.id.unconfirmedBalance)
        val onChainAddress = view.findViewById<TextView>(R.id.onChainAddress)
        val channelBalanceTextView = view.findViewById<TextView>(R.id.channelBalance)
        val pendingOpenChannelBalanceTextView = view.findViewById<TextView>(R.id.pendingOpenChannelBalance)

        presenter.totalBalance.observe(viewLifecycleOwner, Observer { totalBalanceTextView.text = it })
        presenter.confirmedBalance.observe(viewLifecycleOwner, Observer { confirmedBalanceTextView.text = it })
        presenter.unconfirmedBalance.observe(viewLifecycleOwner, Observer { unconfirmedBalanceTextView.text = it })
        presenter.onChainAddress.observe(viewLifecycleOwner, Observer { onChainAddress.text = it })
        presenter.channelBalance.observe(viewLifecycleOwner, Observer { channelBalanceTextView.text = it })
        presenter.pendingOpenChannelBalance.observe(viewLifecycleOwner, Observer { pendingOpenChannelBalanceTextView.text = it })
    }

}
