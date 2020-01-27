package io.horizontalsystems.lightningkit.demo.unlock

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.WalletUnlocker
import io.horizontalsystems.lightningkit.demo.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class UnlockWalletActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock_wallet)
        title = "Unlock Wallet"

        val passwordInput = findViewById<TextInputEditText>(R.id.password)
        val unlockButton = findViewById<Button>(R.id.unlock)

        val presenter = ViewModelProvider(this, UnlockWalletModule.Factory()).get(UnlockWalletPresenter::class.java)
        presenter.error.observe(this, Observer {
            passwordInput.error = it.message
            if (it != WalletUnlocker.UnlockingException) {
                stopLoadingAnimation()
            }
        })
        presenter.closeEvent.observe(this, Observer {
            stopLoadingAnimation()
            finish()
        })

        unlockButton.setOnClickListener {
            presenter.unlock(passwordInput.text.toString())

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            animateLoadingText()
        }
    }

    private fun animateLoadingText() {
        if (disposables.size() > 0) {
            return
        }

        val loadingText = findViewById<TextView>(R.id.loadingText)
        var numberOfDots = 0

        disposables.add(
            Observable.interval(300, TimeUnit.MILLISECONDS)
                .subscribe {
                    this.runOnUiThread {
                        numberOfDots = (numberOfDots + 1) % 6

                        var dots = ""
                        repeat(numberOfDots) { dots += "." }

                        loadingText.text = dots
                    }
                })

    }

    private fun stopLoadingAnimation() {
        findViewById<TextView>(R.id.loadingText).text = ""
        disposables.clear()
    }

}
