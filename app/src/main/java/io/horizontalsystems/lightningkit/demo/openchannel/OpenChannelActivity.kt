package io.horizontalsystems.lightningkit.demo.openchannel

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.lightningkit.demo.R
import kotlinx.android.synthetic.main.activity_open_channel.*

class OpenChannelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_channel)

        title = "Open Channel"

        val presenter = ViewModelProvider(this, OpenChannel.Factory()).get(OpenChannelPresenter::class.java)

        buttonOpen.setOnClickListener {
            val capacity = capacityInput.editText?.text.toString().toLongOrNull() ?: 0L
            val nodePublicKey = publicKeyInput.editText?.text.toString()
            val nodeAddress = hostInput.editText?.text.toString()

            presenter.open(capacity, nodePublicKey, nodeAddress)
        }

        presenter.error.observe(this, Observer {
            val toast = Toast.makeText(this, it.message, Toast.LENGTH_LONG)

            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        })

        presenter.notifyChannelOpened.observe(this, Observer {
            val toast = Toast.makeText(this, "Channel opened successfully", Toast.LENGTH_LONG)

            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()

            finish()
        })
    }
}
