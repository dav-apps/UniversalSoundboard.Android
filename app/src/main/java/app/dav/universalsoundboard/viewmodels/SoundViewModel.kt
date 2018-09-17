package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async

class SoundViewModel : ViewModel(){

    //private var onSoundClickListener: SoundListAdapter.OnItemClickListener = this
    //private var onSoundLongClickListener: SoundListAdapter.OnItemLongClickListener = this
    var soundListAdapter: SoundListAdapter? = null
    /*
    override fun onItemClicked(sound: Sound) {
        MediaPlayer.create(context, 1)
        GlobalScope.launch {
            val mediaPlayer = MediaPlayer.create(this, 1)
        }
    }

    override fun onItemLongClicked(sound: Sound) {

    }
    */

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

    fun onItemLongClicked(sound: Sound){

    }
}