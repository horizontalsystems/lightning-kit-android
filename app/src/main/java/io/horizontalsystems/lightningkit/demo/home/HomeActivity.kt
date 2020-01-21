package io.horizontalsystems.lightningkit.demo.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.horizontalsystems.lightningkit.demo.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val channelsAdapter = ChannelsAdapter()

        val rvChannels = findViewById<RecyclerView>(R.id.channels)
        rvChannels.adapter = channelsAdapter

        val presenter = ViewModelProvider(this, HomeModule.Factory()).get(HomePresenter::class.java)
        presenter.onLoad()

        presenter.channels.observe(this, Observer {
            channelsAdapter.updateChannels(it)
        })
    }
}

