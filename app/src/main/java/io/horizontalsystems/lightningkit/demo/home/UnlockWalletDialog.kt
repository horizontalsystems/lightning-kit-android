package io.horizontalsystems.lightningkit.demo.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import io.horizontalsystems.lightningkit.demo.R

class UnlockWalletDialog : DialogFragment() {
    interface Listener {
        fun onLogoutClick(dialog: DialogFragment)
        fun onUnlockClick(dialog: DialogFragment, password: String)
    }
    private lateinit var listener: Listener

    init {
        isCancelable = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as Listener
    }

    private lateinit var passwordInput: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        val view = activity.layoutInflater.inflate(R.layout.dialog_unlock_wallet, null)
        passwordInput = view.findViewById(R.id.password)
        val unlockButton = view.findViewById<Button>(R.id.unlock)
        val logoutButton = view.findViewById<Button>(R.id.logout)

        unlockButton.setOnClickListener {
            listener.onUnlockClick(this, passwordInput.text.toString())
        }

        logoutButton.setOnClickListener {
            listener.onLogoutClick(this)
        }

        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()

        return dialog
    }

    fun setPasswordError(message: String?) {
        passwordInput.error = message
    }
}