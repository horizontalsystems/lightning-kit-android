package io.horizontalsystems.lightningkit.demo.remoteconnection

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.MainActivity
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.databinding.ActivityRemoteConnectionBinding

class RemoteConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presenter = ViewModelProvider(this, RemoteConnectionModule.Factory()).get(RemoteConnectionPresenter::class.java)

        val binding = DataBindingUtil.setContentView<ActivityRemoteConnectionBinding>(this,
            R.layout.activity_remote_connection
        )
        binding.lifecycleOwner = this
        binding.viewModel = presenter

        presenter.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        presenter.navigateToHome.observe(this, Observer {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        })
    }
}
