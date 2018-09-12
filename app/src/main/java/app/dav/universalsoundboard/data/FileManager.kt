package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import app.dav.davandroidlibrary.Dav
import app.dav.davandroidlibrary.data.TableObject
import app.dav.universalsoundboard.R
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
/*
    suspend fun getAllNonObservableSounds(){
        Dav.Database.getAllNonObservableTableObjects().await()
        //Log.d("GetAllSounds", .toString())
    }
    */

    fun getSoundsOfCategory(categoryUuid: UUID) : LiveData<ArrayList<Sound>>{
        return Transformations.map(getAllSounds()){
            val sounds = ArrayList<Sound>()

            for(sound in it){
                Log.d("getSoundsOfCategory", "Sound: ${sound.categoryUuid.value}")
                //if(sound.categoryUuid == categoryUuid) sounds.add(sound)

                sound.categoryUuid.observeForever(android.arch.lifecycle.Observer {
                    Log.d("getSoundsOfC", "Category of sound changed! ${it}")
                    if(it != null){
                        if(it == categoryUuid){
                            // Add the sound to the list
                            sounds.add(sound)
                        }
                    }
                })
            }

            Log.d("getSoundsOfCategory", "Count: ${sounds.count()}")
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
/*
    fun getCategory(uuid: UUID) : Category?{
        val category = Dav.Database.getTableObject(uuid)
        Log.d("getCategory", "Category: $category")
        return convertTableObjectToCategory(category)
    }
    */

    suspend fun addCategory(uuid: UUID?, name: String, icon: String) : Category?{
        // Generate a new uuid if necessary
        val newUuid: UUID = if(uuid == null) UUID.randomUUID() else uuid

        // Check if an object with the uuid already exists
        if(DatabaseOperations.getObject(newUuid) != null) return null

        DatabaseOperations.createCategory(newUuid, name, icon)
        return Category(newUuid, name, icon)
    }

    private fun convertTableObjectToSound(tableObject: TableObject) : Sound?{
        if(tableObject.tableId != FileManager.soundTableId) return null
/*
        // Get name
        val name = tableObject.getPropertyValue(soundTableNamePropertyName) ?: ""

        // Get favourite
        var favourite = false
        val favouriteString = tableObject.getPropertyValue(soundTableFavouritePropertyName)
        if(favouriteString != null) favourite = favouriteString.toBoolean()
        */

        val sound = Sound(tableObject.uuid, "", null, false, null)

        tableObject.properties.observeForever {
            if(it != null){
                for(p in it){
                    when(p.name){
                        soundTableFavouritePropertyName -> {
                            sound.setFavourite(p.value.toBoolean())
                        }
                        soundTableNamePropertyName -> {
                            //sound.name = p.value
                            sound.setNameLiveData(p.value)
                        }
                        soundTableCategoryUuidPropertyName -> {
                            Log.d("Sound TableObject", "Uuid: ${p.value}")
                            val uuid = UUID.fromString(p.value)
                            sound.setCategory(uuid)
                        }
                    }
                }
            }
        }

        return sound
    }

    private fun convertTableObjectToCategory(tableObject: TableObject) : Category? {
        if(tableObject.tableId != FileManager.categoryTableId) return null
        /*
        // Get name
        val name = tableObject.getPropertyValue(categoryTableNamePropertyName) ?: ""

        // Get icon
        val icon = tableObject.getPropertyValue(categoryTableIconPropertyName) ?: ""
        */

        val category = Category(tableObject.uuid, "", Category.Icons.HOME)

        tableObject.properties.observeForever {
            if(it != null){
                for(p in it){
                    when(p.name){
                        categoryTableNamePropertyName -> {
                            //category.name = p.value
                            category.setNameLiveData(p.value)
                        }
                        categoryTableIconPropertyName -> {
                            category.setIconLiveData(p.value)
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
        currentCategory.value = Category.allSoundsCategory.uuid

        currentCategory.observeForever(android.arch.lifecycle.Observer {
            Log.d("ItemViewHolder", "Changed Category! $it")

            if(it == Category.allSoundsCategory.uuid){
                soundsData.addSource(FileManager.getAllSounds(), soundsData::setValue)
            }else if(it != null){
                soundsData.addSource(FileManager.getSoundsOfCategory(it), soundsData::setValue)
            }
        })
    }

    val currentCategory = MutableLiveData<UUID>()
    var soundsData = MediatorLiveData<ArrayList<Sound>>()
    private val titleData = MutableLiveData<String>()
    val title: LiveData<String>
        get() =  titleData

    fun setTitle(value: String){
        titleData.value = value
    }

    fun getSounds() : LiveData<ArrayList<Sound>>{
        return soundsData
    }
}