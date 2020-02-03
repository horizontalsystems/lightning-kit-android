package io.horizontalsystems.lightningkit.demo.channels

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.horizontalsystems.lightningkit.demo.R
import io.horizontalsystems.lightningkit.demo.openchannel.OpenChannelActivity

class ChannelsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presenter = ViewModelProvider(this, ChannelsModule.Factory()).get(ChannelsPresenter::class.java)
        presenter.onLoad()

        val channelsAdapter = ChannelsAdapter {
            val channelInfoStr = listOf(
                "ChannelPoint" to it.channelPoint,
                "Capacity" to it.capacity,
                "Local Balance" to it.localBalance,
                "Remote Balance" to it.remoteBalance
            ).map {
                "${it.first}: ${it.second}"
            }.joinToString("\n\n")

            val dialog = AlertDialog.Builder(requireActivity())
                .setTitle("Close Channel")
                .setMessage(channelInfoStr)
                .setPositiveButton("Confirm") { dialog: DialogInterface, which: Int ->
                    presenter.closeChannel(it.channelPoint)
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            val intent = Intent(context, OpenChannelActivity::class.java)
            startActivity(intent)
        }

        val rvChannels = view.findViewById<RecyclerView>(R.id.channels)
        rvChannels.adapter = channelsAdapter

        presenter.channels.observe(viewLifecycleOwner, Observer {
            channelsAdapter.updateChannels(it)
        })

        presenter.closedChannels.observe(viewLifecycleOwner, Observer {
            channelsAdapter.updateClosedChannels(it)
        })

        presenter.pendingChannels.observe(viewLifecycleOwner, Observer {
            channelsAdapter.updatePendingChannels(it)
        })

        presenter.channelCloseStatusUpdate.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireActivity(), "Successfully Closed", Toast.LENGTH_LONG).show()
        })

        presenter.channelCloseFailure.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireActivity(), "Error Closing Channel ${it.localizedMessage}", Toast.LENGTH_LONG).show()
        })
    }
}
