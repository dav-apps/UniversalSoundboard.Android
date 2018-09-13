package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
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

    suspend fun getAllSounds() : ArrayList<Sound>{
        val tableObjects = DatabaseOperations.getAllSounds()
        val sounds = ArrayList<Sound>()

        for(obj in tableObjects){
            Log.d("getAllSounds", obj.properties.toString())
            val sound = convertTableObjectToSound(obj)
            if(sound != null) sounds.add(sound)
        }

        return sounds
    }

    suspend fun getSoundsOfCategory(categoryUuid: UUID) : ArrayList<Sound>{
        val sounds = ArrayList<Sound>()

        for(sound in getAllSounds()){
            if(sound.category?.uuid == categoryUuid){
                sounds.add(sound)
            }
        }

        return sounds
    }

    suspend fun getAllCategories() : ArrayList<Category>{
        val categories = ArrayList<Category>()

        for(obj in DatabaseOperations.getAllCategories()){
            val category = convertTableObjectToCategory(obj)
            if(category != null) categories.add(category)
        }

        return categories
    }

    suspend fun getCategory(uuid: UUID) : Category?{
        val category = DatabaseOperations.getObject(uuid)
        return if(category != null) convertTableObjectToCategory(category) else null
    }

    suspend fun addCategory(uuid: UUID?, name: String, icon: String) : Category?{
        // Generate a new uuid if necessary
        val newUuid: UUID = if(uuid == null) UUID.randomUUID() else uuid

        // Check if an object with the uuid already exists
        if(DatabaseOperations.getObject(newUuid) != null) return null

        DatabaseOperations.createCategory(newUuid, name, icon)
        return Category(newUuid, name, icon)
    }

    private suspend fun convertTableObjectToSound(tableObject: TableObject) : Sound?{
        if(tableObject.tableId != FileManager.soundTableId) return null

        // Get name
        val name = tableObject.getPropertyValue(soundTableNamePropertyName) ?: ""

        // Get favourite
        var favourite = false
        val favouriteString = tableObject.getPropertyValue(soundTableFavouritePropertyName)
        favourite = if(favouriteString != null) favouriteString.toBoolean() else false

        val sound = Sound(tableObject.uuid, name, null, false, null)

        // Get category
        val categoryUuidString = tableObject.getPropertyValue(soundTableCategoryUuidPropertyName)
        if(categoryUuidString != null){
            sound.category = getCategory(UUID.fromString(categoryUuidString))
        }

        return sound
    }

    private fun convertTableObjectToCategory(tableObject: TableObject) : Category? {
        if(tableObject.tableId != FileManager.categoryTableId) return null

        // Get name
        val name = tableObject.getPropertyValue(categoryTableNamePropertyName) ?: ""

        // Get icon
        val icon = tableObject.getPropertyValue(categoryTableIconPropertyName) ?: Category.Icons.HOME

        return Category(tableObject.uuid, name, icon)
    }
}

class ItemViewHolder(){
    constructor(title: String) : this() {
        titleData.value = title
        soundsData.value = ArrayList<Sound>()
        categoriesData.value = ArrayList<Category>()
    }

    var currentCategory: UUID = Category.allSoundsCategory.uuid
    private val titleData = MutableLiveData<String>()
    val title: LiveData<String>
        get() =  titleData
    private val soundsData = MutableLiveData<ArrayList<Sound>>()
    val sounds: LiveData<ArrayList<Sound>>
        get() = soundsData
    private val categoriesData = MutableLiveData<ArrayList<Category>>()
    val categories: LiveData<ArrayList<Category>>
        get() = categoriesData

    fun setTitle(value: String){
        titleData.value = value
    }

    suspend fun loadSounds(){
        val tableObjects: ArrayList<Sound> = if(currentCategory == Category.allSoundsCategory.uuid){
            // Get all sounds
            FileManager.getAllSounds()
        }else{
            // Get the sounds of the selected category
            FileManager.getSoundsOfCategory(currentCategory)
        }

        soundsData.value?.clear()
        for(sound in tableObjects){
            soundsData.value?.add(sound)
        }
    }

    suspend fun loadCategories(){
        categoriesData.value?.clear()
        for(category in FileManager.getAllCategories()){
            categoriesData.value?.add(category)
        }
    }
}