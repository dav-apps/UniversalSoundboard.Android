package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.PlayingSound
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch

class SetRepetitionsDialogFragment : DialogFragment() {
    var playingSound: PlayingSound? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog() ?: return super.onCreateDialog(savedInstanceState)
    }

    private fun createDialog() : AlertDialog?{
        playingSound ?: return null
        return AlertDialog.Builder(activity)
                .setTitle(R.string.playing_sound_item_context_menu_set_repetitions)
                .setItems(R.array.playing_sound_item_set_repetitions_options) { dialog, which ->
                    val p = playingSound ?: return@setItems
                    var repetitions = 0

                    repetitions = when(which){
                        0 -> 1              // 1x
                        1 -> 2              // 2x
                        2 -> 5              // 5x
                        3 -> 10             // 10x
                        4 -> Int.MAX_VALUE  // ∞
                        else -> p.repetitions
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        FileManager.setRepetitionsOfPlayingSound(p.uuid, repetitions)
                    }
                }
                .show()
    }
}