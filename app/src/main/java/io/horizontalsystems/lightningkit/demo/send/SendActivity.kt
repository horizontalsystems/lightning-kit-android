package io.horizontalsystems.lightningkit.demo.send

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.MainActivity
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.databinding.ActivitySendBinding

class SendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presenter = ViewModelProvider(this, SendModule.Factory()).get(SendPresenter::class.java)

        val binding = DataBindingUtil.setContentView<ActivitySendBinding>(this,
            R.layout.activity_send
        )
        binding.lifecycleOwner = this
        binding.viewModel = presenter

        presenter.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        presenter.navigateToHome.observe(this, Observer {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}
