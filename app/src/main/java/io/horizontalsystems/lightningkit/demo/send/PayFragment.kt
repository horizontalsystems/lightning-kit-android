package io.horizontalsystems.lightningkit.demo.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.demo.R

class PayFragment : Fragment(), ConfirmDialog.Listener {
    val presenter by lazy { ViewModelProvider(this, PayModule.Factory()).get(PayPresenter::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_pay, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sendButton = view.findViewById<Button>(R.id.send)
        val invoiceTextInputEditText = view.findViewById<TextInputEditText>(R.id.invoice)

        invoiceTextInputEditText.addTextChangedListener {
            presenter.invoice = it.toString()
        }

        sendButton.setOnClickListener {
            presenter.send()
        }

        presenter.payReq.observe(viewLifecycleOwner, Observer { payReq ->
            val payReqText = listOf(
                "Satoshis" to payReq.numSatoshis,
                "Payment Hash" to payReq.paymentHash,
                "Description" to payReq.description
            ).map {
                "${it.first}: ${it.second}"
            }.joinToString("\n\n")

            val confirmDialog = ConfirmDialog()
            confirmDialog.setMessage(payReqText)
            confirmDialog.show(childFragmentManager, "ErrorDialog")
        })

        presenter.clearInvoice.observe(viewLifecycleOwner, Observer {
            invoiceTextInputEditText.text = null
        })

        presenter.showPaidLiveEvent.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "Successfully paid", Toast.LENGTH_LONG).show()
        })

        presenter.paymentError.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        })
    }

    override fun onConfirm(dialog: DialogFragment) {
        presenter.confirmSend()
    }
}
