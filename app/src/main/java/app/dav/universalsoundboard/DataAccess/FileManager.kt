package app.dav.universalsoundboard.DataAccess

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

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

    fun setTitle(value: String){
        titleData.value = value
    }
}