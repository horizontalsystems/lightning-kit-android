package io.horizontalsystems.lightningkit.demo.restorewallet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.home.HomeActivity
import kotlinx.android.synthetic.main.activity_restore_wallet.*

class RestoreWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_wallet)

        val presenter = ViewModelProvider(this, RestoreWalletModule.Factory()).get(RestoreWalletPresenter::class.java)

        presenter.mnemonicError.observe(this, Observer {
            mnemonicInput.editText?.error = it
        })

        presenter.goToMainLiveEvent.observe(this, Observer {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        })

        restore.setOnClickListener {
            presenter.restore()
        }

        mnemonicInput.editText?.addTextChangedListener {
            it?.let {
                presenter.mnemonic = it.toString()
            }
        }
    }
}
