package io.horizontalsystems.lightningkit.demo.send

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmDialog : DialogFragment() {
    interface Listener {
        fun onConfirm(dialog: DialogFragment)
    }

    private lateinit var listener: Listener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        return AlertDialog.Builder(activity)
            .setTitle("Confirm")
            .setMessage(arguments?.getString(MESSAGE_KEY))
            .setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                listener.onConfirm(this)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    fun setMessage(message: String) {
        val bundle = Bundle().apply {
            putString(MESSAGE_KEY, message)
        }
        this.arguments = bundle
    }

    companion object {
        const val MESSAGE_KEY = "MESSAGE"
    }
}