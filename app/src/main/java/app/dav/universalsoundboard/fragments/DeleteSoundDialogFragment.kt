package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Sound

class DeleteSoundDialogFragment : DialogFragment() {
    var sound: Sound? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog()
    }

    fun createDialog() : AlertDialog{
        return AlertDialog.Builder(activity)
                .setTitle(getString(R.string.delete_sound_dialog_title, sound?.name))
                .setMessage(R.string.delete_sound_dialog_message)
                .setPositiveButton(R.string.delete_sound_dialog_positive_button_text, DialogInterface.OnClickListener{ dialog, which ->
                    val s = sound
                    if(s != null){
                        FileManager.deleteSound(s.uuid)
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, null)
                .show()
    }
}