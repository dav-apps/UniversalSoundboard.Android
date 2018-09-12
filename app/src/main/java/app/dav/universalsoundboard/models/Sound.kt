package app.dav.universalsoundboard.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.media.Image
import android.util.Log
import java.util.*

class Sound(val uuid: UUID){
    private val nameData = MutableLiveData<String>()
    val name: LiveData<String>
        get() = nameData
    private val categoryUuidData = MutableLiveData<UUID>()
    val categoryUuid: LiveData<UUID>
        get() = categoryUuidData
    private val favouriteData = MutableLiveData<Boolean>()
    val favourite: LiveData<Boolean>
        get() = favouriteData
    private val imageData = MutableLiveData<Image>()
    val image: LiveData<Image>
        get() = imageData

    constructor(uuid: UUID, name: String, category: UUID?, favourite: Boolean, image: Image?) : this(uuid){
        nameData.value = name
        if(category != null) categoryUuidData.value = category
        favouriteData.value = favourite
        if(image != null) imageData.value = image
    }

    fun setNameLiveData(name: String){
        nameData.value = name
    }

    fun setCategory(uuid: UUID){
        Log.d("setCategory", "UUID: ${uuid}")
        categoryUuidData.value = uuid
    }

    fun setFavourite(favourite: Boolean){
        favouriteData.value = favourite
    }
}