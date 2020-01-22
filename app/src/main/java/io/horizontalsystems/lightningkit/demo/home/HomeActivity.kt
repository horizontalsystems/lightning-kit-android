package io.horizontalsystems.lightningkit.demo.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.send.SendActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val channelsAdapter = ChannelsAdapter()

        val rvChannels = findViewById<RecyclerView>(R.id.channels)
        rvChannels.adapter = channelsAdapter

        val sendButton = findViewById<Button>(R.id.button)
        sendButton.setOnClickListener(View.OnClickListener() {
            val intent = Intent(this, SendActivity::class.java)
            startActivity(intent)
            finish()
        })

        val presenter = ViewModelProvider(this, HomeModule.Factory()).get(HomePresenter::class.java)
        presenter.onLoad()

        presenter.channels.observe(this, Observer {
            channelsAdapter.updateChannels(it)
        })
    }
}

