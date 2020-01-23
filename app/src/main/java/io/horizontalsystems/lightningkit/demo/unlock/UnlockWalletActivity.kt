package io.horizontalsystems.lightningkit.demo.unlock

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.demo.R

class UnlockWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock_wallet)
        title = "Unlock Wallet"

        val passwordInput = findViewById<TextInputEditText>(R.id.password)
        val unlockButton = findViewById<Button>(R.id.unlock)

        val presenter = ViewModelProvider(this, UnlockWalletModule.Factory()).get(UnlockWalletPresenter::class.java)
        presenter.error.observe(this, Observer {
            passwordInput.error = it.message
        })
        presenter.closeEvent.observe(this, Observer {
            finish()
        })

        unlockButton.setOnClickListener {
            presenter.unlock(passwordInput.text.toString())
        }
    }
}
