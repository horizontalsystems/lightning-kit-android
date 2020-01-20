package io.horizontalsystems.lightningkit.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import io.horizontalsystems.lightningkit.demo.databinding.ActivityRemoteConnectionBinding

class RemoteConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityRemoteConnectionBinding>(this, R.layout.activity_remote_connection)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModelProviders.of(this).get(RemoteConnectionViewModel::class.java)

    }
}
