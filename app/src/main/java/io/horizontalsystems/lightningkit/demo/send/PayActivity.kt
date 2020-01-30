package io.horizontalsystems.lightningkit.demo.send

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.demo.R

class PayActivity : AppCompatActivity(), ConfirmDialog.Listener {
    val presenter by lazy { ViewModelProvider(this, PayModule.Factory()).get(PayPresenter::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        title = "Pay Invoice"

        val sendButton = findViewById<Button>(R.id.send)
        val invoiceTextInputEditText = findViewById<TextInputEditText>(R.id.invoice)

        invoiceTextInputEditText.addTextChangedListener {
            presenter.invoice = it.toString()
        }

        sendButton.setOnClickListener {
            presenter.send()
        }

        presenter.payReq.observe(this, Observer { payReq ->
            val payReqText = listOf(
                "Satoshis" to payReq.numSatoshis,
                "Payment Hash" to payReq.paymentHash,
                "Description" to payReq.description
            ).map {
                "${it.first}: ${it.second}"
            }.joinToString("\n\n")

            val confirmDialog = ConfirmDialog()
            confirmDialog.setMessage(payReqText)
            confirmDialog.show(supportFragmentManager, "confirm")
        })

        presenter.clearInvoice.observe(this, Observer {
            invoiceTextInputEditText.text = null
        })

        presenter.showPaidLiveEvent.observe(this, Observer {
            Toast.makeText(this, "Successfully paid", Toast.LENGTH_LONG).show()

            finish()
        })

        presenter.paymentError.observe(this, Observer {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        })

        presenter.invoiceError.observe(this, Observer {
            invoiceTextInputEditText.error = it.message
        })
    }

    override fun onConfirm(dialog: DialogFragment) {
        presenter.confirmSend()
    }
}
