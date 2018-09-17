package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.widget.PopupMenu
import android.view.View
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async

class SoundViewModel : ViewModel(){

    var soundListAdapter: SoundListAdapter? = null

    suspend fun onItemClicked(context: Context, sound: Sound){
        val uri: Uri? = GlobalScope.async {
            val file = sound.getAudioFile()
            Uri.fromFile(file)
        }.await()

        if(uri != null){
            val mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer.start()
        }
    }

    fun onItemLongClicked(context: Context, sound: Sound, item: View){
        val menu = PopupMenu(context, item)
        menu.inflate(R.menu.sound_item_context_menu)
        menu.show()

        menu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.sound_item_context_menu_change_image -> changeSoundImage()
                R.id.sound_item_context_menu_rename -> renameSound()
                R.id.sound_item_context_menu_delete -> deleteSound()
            }
            true
        }
    }

    fun changeSoundImage(){

    }

    fun renameSound(){

    }

    fun deleteSound(){

    }
}