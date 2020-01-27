package io.horizontalsystems.lightningkit.demo.home

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ErrorDialog : DialogFragment() {
    interface Listener {
        fun onLogoutClick(dialog: DialogFragment)
    }
    private lateinit var listener: Listener

    init {
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setMessage(arguments?.getString(ERROR_MESSAGE_KEY))
                .setPositiveButton("Logout") { dialog: DialogInterface, which: Int ->
                    listener.onLogoutClick(this)
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement NoticeDialogListener")
        }
    }

    fun setMessage(message: String) {
        val bundle = Bundle().apply {
            putString(ERROR_MESSAGE_KEY, message)
        }
        this.arguments = bundle
    }

    companion object {
        const val ERROR_MESSAGE_KEY = "ERROR_MESSAGE"
    }
}