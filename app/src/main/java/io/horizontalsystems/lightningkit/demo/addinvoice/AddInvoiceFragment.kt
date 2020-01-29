package io.horizontalsystems.lightningkit.demo.addinvoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.demo.R

class AddInvoiceFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_addinvoice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presenter = ViewModelProvider(this, AddInvoiceModule.Factory()).get(AddInvoicePresenter::class.java)

        val amountTextInputEditText = view.findViewById<TextInputEditText>(R.id.amount)
        val memoTextInputEditText = view.findViewById<TextInputEditText>(R.id.memo)
        val generateButton = view.findViewById<Button>(R.id.generate)
        val invoiceTextView = view.findViewById<TextView>(R.id.invoice)

        generateButton.setOnClickListener {
            presenter.addInvoice(amountTextInputEditText.text.toString().toLong(), memoTextInputEditText.text.toString())
        }

        presenter.showAddedLiveEvent.observe(viewLifecycleOwner, Observer {
            invoiceTextView.text = it
        })

        presenter.addInvoiceFailure.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        })
    }
}

