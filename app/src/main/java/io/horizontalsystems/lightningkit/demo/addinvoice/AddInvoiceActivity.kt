package io.horizontalsystems.lightningkit.demo.addinvoice

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.demo.R

class AddInvoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_invoice)

        title = "Add Invoice"

        val presenter = ViewModelProvider(this, AddInvoiceModule.Factory()).get(AddInvoicePresenter::class.java)

        val amountTextInputEditText = findViewById<TextInputEditText>(R.id.amount)
        val memoTextInputEditText = findViewById<TextInputEditText>(R.id.memo)
        val generateButton = findViewById<Button>(R.id.generate)
        val invoiceTextView = findViewById<TextView>(R.id.invoice)

        generateButton.setOnClickListener {
            presenter.addInvoice(amountTextInputEditText.text.toString().toLong(), memoTextInputEditText.text.toString())
        }

        presenter.showAddedLiveEvent.observe(this, Observer {
            invoiceTextView.text = it
        })

        presenter.addInvoiceFailure.observe(this, Observer {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        })
    }
}

