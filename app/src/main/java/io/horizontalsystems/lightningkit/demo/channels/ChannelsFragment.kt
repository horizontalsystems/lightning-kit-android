package io.horizontalsystems.lightningkit.demo.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.horizontalsystems.lightningkit.demo.R

class ChannelsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = ViewModelProvider(this, ChannelsModule.Factory()).get(ChannelsPresenter::class.java)
        presenter.onLoad()

        val channelsAdapter = ChannelsAdapter()

        val rvChannels = view.findViewById<RecyclerView>(R.id.channels2)
        rvChannels.adapter = channelsAdapter

        presenter.channels.observe(viewLifecycleOwner, Observer {
            channelsAdapter.updateChannels(it)
        })
    }
}
