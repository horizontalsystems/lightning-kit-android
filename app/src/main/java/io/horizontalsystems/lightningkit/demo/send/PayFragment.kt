package io.horizontalsystems.lightningkit.demo.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.demo.R

class PayFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_pay, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presenter = ViewModelProvider(this, PayModule.Factory()).get(PayPresenter::class.java)

        val sendButton = view.findViewById<Button>(R.id.send)
        val invoiceTextInputEditText = view.findViewById<TextInputEditText>(R.id.invoice)

        sendButton.setOnClickListener {
            presenter.send(invoiceTextInputEditText.text.toString())
        }
    }
}
