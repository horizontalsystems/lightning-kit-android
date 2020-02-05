package io.horizontalsystems.lightningkit.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.createwallet.CreateWalletActivity
import io.horizontalsystems.lightningkit.demo.databinding.ActivityGuestBinding
import io.horizontalsystems.lightningkit.demo.remoteconnection.RemoteConnectionActivity
import io.horizontalsystems.lightningkit.demo.restorewallet.RestoreWalletActivity

class GuestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)

        val viewModel = ViewModelProvider(this).get(GuestPresenter::class.java)

        val binding = DataBindingUtil.setContentView<ActivityGuestBinding>(this, R.layout.activity_guest)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.openRemoteConnectionLiveEvent.observe(this, Observer {
            val intent = Intent(this, RemoteConnectionActivity::class.java)
            startActivity(intent)
        })

        viewModel.openCreateLiveEvent.observe(this, Observer {
            val intent = Intent(this, CreateWalletActivity::class.java)
            startActivity(intent)
        })

        viewModel.openRestoreLiveEvent.observe(this, Observer {
            val intent = Intent(this, RestoreWalletActivity::class.java)
            startActivity(intent)
        })
    }
}
