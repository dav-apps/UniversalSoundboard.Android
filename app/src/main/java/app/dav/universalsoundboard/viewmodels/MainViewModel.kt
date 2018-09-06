package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.fragments.SoundListRecyclerViewAdapter
import app.dav.universalsoundboard.models.Sound
import java.util.*

class MainViewModel : ViewModel(), SoundListRecyclerViewAdapter.OnItemClickListener, SoundListRecyclerViewAdapter.OnItemLongClickListener{
    private var clickListener: SoundListRecyclerViewAdapter.OnItemClickListener = this
    private var longClickListener: SoundListRecyclerViewAdapter.OnItemLongClickListener = this
    val soundListRecyclerViewAdapter = SoundListRecyclerViewAdapter(clickListener, longClickListener)
    var currentCategory: UUID? = null

    override fun onItemClicked(sound: Sound) {

    }

    override fun onItemLongClicked(sound: Sound) {

    }

    fun getSounds() : LiveData<ArrayList<Sound>> {
        return FileManager.getAllSounds()
    }
}