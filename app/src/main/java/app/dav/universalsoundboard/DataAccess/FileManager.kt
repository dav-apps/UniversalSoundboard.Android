package app.dav.universalsoundboard.DataAccess

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import app.dav.universalsoundboard.Fragments.SoundListRecyclerViewAdapter
import app.dav.universalsoundboard.Models.Sound
import java.util.*

object FileManager{
    val itemViewHolder: ItemViewHolder = ItemViewHolder(title = "All Sounds")
}

class ItemViewHolder(){
    constructor(title: String) : this() {
        titleData.value = title
    }

    private val titleData = MutableLiveData<String>()
    val title: LiveData<String>
        get() =  titleData
    val sounds = ArrayList<Sound>()
    var soundListRecyclerViewAdapter: SoundListRecyclerViewAdapter? = null

    fun setTitle(value: String){
        titleData.value = value
    }
}