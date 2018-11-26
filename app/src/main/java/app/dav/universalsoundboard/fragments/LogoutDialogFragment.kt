package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager

class LogoutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog()
    }

    private fun createDialog() : AlertDialog{
        return AlertDialog.Builder(activity)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout) { dialog, which ->
                    // Log out the user
                    val user = FileManager.itemViewHolder.user.value ?: return@setPositiveButton
                    user.logout()
                    FileManager.itemViewHolder.setUser(user)
                }
                .setNegativeButton(R.string.dialog_negative_button, null)
                .show()
    }
}