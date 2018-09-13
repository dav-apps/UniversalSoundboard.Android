package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.ViewModel
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.models.Sound

class SoundViewModel :
        ViewModel(),
        SoundListAdapter.OnItemClickListener,
        SoundListAdapter.OnItemLongClickListener{

    private var onSoundClickListener: SoundListAdapter.OnItemClickListener = this
    private var onSoundLongClickListener: SoundListAdapter.OnItemLongClickListener = this
    val soundListAdapter = SoundListAdapter(onSoundClickListener, onSoundLongClickListener)

    override fun onItemClicked(sound: Sound) {

    }

    override fun onItemLongClicked(sound: Sound) {

    }
}