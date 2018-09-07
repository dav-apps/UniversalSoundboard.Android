package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import app.dav.davandroidlibrary.data.TableObject
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.Sound
import java.util.*
import kotlin.collections.ArrayList

object FileManager{
    const val appId = 1                 // Dev: 8, Prod: 1
    const val soundFileTableId = 6      // Dev: 11, Prod: 6
    const val imageFileTableId = 7      // Dev: 15, Prod: 7
    const val categoryTableId = 8       // Dev: 16, Prod: 8
    const val soundTableId = 5          // Dev: 17, Prod: 5
    const val playingSoundTableId = 9   // Dev: 18, Prod: 9

    const val soundTableNamePropertyName = "name"
    const val soundTableFavouritePropertyName = "favourite"
    const val soundTableSoundUuidPropertyName = "sound_uuid"
    const val soundTableImageUuidPropertyName = "image_uuid"
    const val soundTableCategoryUuidPropertyName = "category_uuid"

    const val categoryTableNamePropertyName = "name"
    const val categoryTableIconPropertyName = "icon"

    const val playingSoundTableSoundIdsPropertyName = "sound_ids"
    const val playingSoundTableCurrentPropertyName = "current"
    const val playingSoundTableRepetitionsPropertyName = "repetitions"
    const val playingSoundTableRandomlyPropertyName = "randomly"
    const val playingSoundTableVolumePropertyName = "volume"

    val itemViewHolder: ItemViewHolder = ItemViewHolder(title = "All Sounds")

    fun addSound(uuid: UUID?, name: String, categoryUuid: UUID?/*, audioFile: File*/){
        // Generate a new uuid if necessary
        val newUuid: UUID = if(uuid == null) UUID.randomUUID() else uuid

        // Create a uuid for the sound file
        val soundFileUuid = UUID.randomUUID()

        val categoryUuidString = if(categoryUuid == null) "" else categoryUuid.toString()

        // Copy the sound file
        // TODO

        DatabaseOperations.createSound(newUuid, name, soundFileUuid.toString(), categoryUuidString)
    }

    fun getAllSounds() : LiveData<ArrayList<Sound>>{
        val tableObjects = DatabaseOperations.getAllSounds()

        return Transformations.map(tableObjects) {
            val sounds = ArrayList<Sound>()

            for(obj in it){
                val sound = convertTableObjectToSound(obj)
                if(sound != null) sounds.add(sound)
            }

            sounds
        }
    }

    fun getSoundsOfCategory(categoryUuid: UUID) : LiveData<ArrayList<Sound>>{
        val tableObjects = DatabaseOperations.getAllSounds()

        return Transformations.map(tableObjects){
            val sounds = ArrayList<Sound>()

            for(obj in it){
                val sound = convertTableObjectToSound(obj)
                if(sound != null) sounds.add(sound)
            }

            sounds
        }
    }

    fun getAllCategories() : LiveData<ArrayList<Category>>{
        val tableObjects = DatabaseOperations.getAllCategories()

        return Transformations.map(tableObjects) {
            val categories = ArrayList<Category>()

            for(obj in it){
                val category = convertTableObjectToCategory(obj)
                if(category != null) categories.add(category)
            }

            categories
        }
    }

    private fun convertTableObjectToSound(tableObject: TableObject) : Sound?{
        if(tableObject.tableId != FileManager.soundTableId) return null

        // Get name
        val name = tableObject.getPropertyValue(soundTableNamePropertyName) ?: ""

        // Get favourite
        var favourite = false
        val favouriteString = tableObject.getPropertyValue(soundTableFavouritePropertyName)
        if(favouriteString != null) favourite = favouriteString.toBoolean()

        val sound = Sound(tableObject.uuid, name, null, favourite, null)

        tableObject.properties.observeForever {
            if(it != null){
                for(p in it){
                    when(p.name){
                        soundTableFavouritePropertyName -> {
                            sound.favourite = p.value.toBoolean()
                        }
                        soundTableNamePropertyName -> {
                            sound.name = p.value
                        }
                    }
                }
            }
        }

        return sound
    }

    private fun convertTableObjectToCategory(tableObject: TableObject) : Category? {
        if(tableObject.tableId != FileManager.categoryTableId) return null

        // Get name
        val name = tableObject.getPropertyValue(categoryTableNamePropertyName) ?: ""

        // Get icon
        val icon = tableObject.getPropertyValue(categoryTableIconPropertyName) ?: ""

        val category = Category(tableObject.uuid, name, icon)

        tableObject.properties.observeForever {
            if(it != null){
                for(p in it){
                    when(p.name){
                        categoryTableNamePropertyName -> {
                            category.name = p.value
                        }
                        categoryTableIconPropertyName -> {
                            category.icon = p.value
                        }
                    }
                }
            }
        }

        return category
    }
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