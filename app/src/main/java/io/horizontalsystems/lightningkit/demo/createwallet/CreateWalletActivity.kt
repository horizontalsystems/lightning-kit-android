package io.horizontalsystems.lightningkit.demo.createwallet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.MainActivity
import io.horizontalsystems.lightningkit.demo.R
import kotlinx.android.synthetic.main.activity_create_wallet.*

class CreateWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        val presenter = ViewModelProvider(this, CreateWalletModule.Factory()).get(CreateWalletPresenter::class.java)
        presenter.onLoad()

        presenter.mnemonicList.observe(this, Observer {
            mnemonicList.text = it.mapIndexed { index, s ->
                "${index + 1}: $s"
            }.joinToString("\n")

            next.isEnabled = true
        })

        next.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
