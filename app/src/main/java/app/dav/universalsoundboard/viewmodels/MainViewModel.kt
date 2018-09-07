package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.models.Sound
import java.util.*

class MainViewModel : ViewModel(), SoundListAdapter.OnItemClickListener, SoundListAdapter.OnItemLongClickListener{
    private var clickListener: SoundListAdapter.OnItemClickListener = this
    private var longClickListener: SoundListAdapter.OnItemLongClickListener = this
    val soundListRecyclerViewAdapter = SoundListAdapter(clickListener, longClickListener)
    var currentCategory: UUID? = null

    override fun onItemClicked(sound: Sound) {

    }

    override fun onItemLongClicked(sound: Sound) {

    }

    fun getSounds() : LiveData<ArrayList<Sound>> {
        return FileManager.getAllSounds()
    }
}