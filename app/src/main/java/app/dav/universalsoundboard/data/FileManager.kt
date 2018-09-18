package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.BitmapFactory
import app.dav.davandroidlibrary.data.TableObject
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.io.File
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

    fun showCategory(category: Category){
        itemViewHolder.currentCategory = category.uuid
        itemViewHolder.setTitle(category.name)
        GlobalScope.launch(Dispatchers.Main) { itemViewHolder.loadSounds() }
    }

    fun addSound(uuid: UUID?, name: String, categoryUuid: UUID?, audioFile: File){
        // Generate a new uuid if necessary
        val newUuid: UUID = if(uuid == null) UUID.randomUUID() else uuid

        // Create a uuid for the sound file
        val soundFileUuid = UUID.randomUUID()

        val categoryUuidString = if(categoryUuid == null) "" else categoryUuid.toString()

        // Copy the sound file
        DatabaseOperations.createSoundFile(soundFileUuid, audioFile)

        DatabaseOperations.createSound(newUuid, name, soundFileUuid.toString(), categoryUuidString)
        itemViewHolder.addSound(Sound(newUuid, name, categoryUuid, false, null))
    }

    suspend fun getAllSounds() : ArrayList<Sound>{
        val tableObjects = DatabaseOperations.getAllSounds()
        val sounds = ArrayList<Sound>()

        for(obj in tableObjects){
            val sound = convertTableObjectToSound(obj)
            if(sound != null) sounds.add(sound)
        }

        return sounds
    }

    suspend fun getSoundsOfCategory(categoryUuid: UUID) : ArrayList<Sound>{
        val sounds = ArrayList<Sound>()

        for(sound in getAllSounds()){
            if(sound.category == categoryUuid){
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
        val category = Category(newUuid, name, icon)
        itemViewHolder.addCategory(category)
        return category
    }

    suspend fun updateImageOfSound(soundUuid: UUID, imageFile: File){
        val soundTableObject = DatabaseOperations.getObject(soundUuid)
        if(soundTableObject == null || soundTableObject.tableId != soundTableId) return

        val imageUuidString = soundTableObject.getPropertyValue(soundTableImageUuidPropertyName)
        var imageUuid = UUID.randomUUID()

        if(imageUuidString != null){
            imageUuid = UUID.fromString(imageUuidString)

            // Update the existing imageFile
            DatabaseOperations.updateImageFile(imageUuid, imageFile)
        }else{
            // Create a new imageFile
            DatabaseOperations.createImageFile(imageUuid, imageFile)
            DatabaseOperations.updateSound(soundUuid, null, null, null, imageUuid.toString(), null)
        }

        // itemViewHolder.allSoundsChanged = true
    }

    private suspend fun convertTableObjectToSound(tableObject: TableObject) : Sound?{
        if(tableObject.tableId != FileManager.soundTableId) return null

        // Get name
        val name = tableObject.getPropertyValue(soundTableNamePropertyName) ?: ""

        // Get favourite
        var favourite = false
        val favouriteString = tableObject.getPropertyValue(soundTableFavouritePropertyName)
        favourite = if(favouriteString != null) favouriteString.toBoolean() else false

        val sound = Sound(tableObject.uuid, name, null, favourite, null)

        // Get category
        val categoryUuidString = tableObject.getPropertyValue(soundTableCategoryUuidPropertyName)
        if(categoryUuidString != null){
            sound.category = UUID.fromString(categoryUuidString)
        }

        // Get image
        val imageUuidString = tableObject.getPropertyValue(soundTableImageUuidPropertyName)
        if(imageUuidString != null){
            val imageFileTableObject = getImageFileTableObject(tableObject.uuid)

            if(imageFileTableObject != null){
                if(imageFileTableObject.isFile && imageFileTableObject.file != null){
                    sound.image = BitmapFactory.decodeFile(imageFileTableObject.file?.path)
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
        val icon = tableObject.getPropertyValue(categoryTableIconPropertyName) ?: Category.Icons.HOME

        return Category(tableObject.uuid, name, icon)
    }

    fun getDavDataPath(filesDir: String) : File{
        val path = filesDir + "/dav"
        val dir = File(path)
        if(!dir.exists()){
            dir.mkdir()
        }
        return dir
    }

    suspend fun getAudioFileOfSound(uuid: UUID) : File?{
        val soundFileTableObject = getSoundFileTableObject(uuid)
        if(soundFileTableObject == null) return null
        return soundFileTableObject.file
    }

    private suspend fun getSoundFileTableObject(soundUuid: UUID) : TableObject?{
        val soundTableObject = DatabaseOperations.getObject(soundUuid)
        if(soundTableObject == null) return null
        val soundFileUuidString = soundTableObject.getPropertyValue(soundTableSoundUuidPropertyName)
        if(soundFileUuidString == null) return null
        val soundFileUuid = UUID.fromString(soundFileUuidString)
        return DatabaseOperations.getObject(soundFileUuid)
    }

    private suspend fun getImageFileTableObject(soundUuid: UUID) : TableObject?{
        val soundTableObject = DatabaseOperations.getObject(soundUuid)
        if(soundTableObject == null) return null
        val imageFileUuidString = soundTableObject.getPropertyValue(soundTableImageUuidPropertyName)
        if(imageFileUuidString == null) return null
        val imageFileUuid = UUID.fromString(imageFileUuidString)
        return DatabaseOperations.getObject(imageFileUuid)
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

    fun addSound(sound: Sound){
        if(currentCategory == Category.allSoundsCategory.uuid
                || currentCategory == sound.category) {
            val sounds = soundsData.value
            if(sounds != null){
                sounds.add(sound)

                // Set the value of soundsData
                soundsData.value = sounds
            }
        }
    }

    fun addCategory(category: Category){
        val categories = categoriesData.value
        if(categories != null){
            categories.add(category)

            // Set the value of categoriesData
            categoriesData.value = categories
        }
    }

    suspend fun loadSounds(){
        soundsData.value = if(currentCategory == Category.allSoundsCategory.uuid){
            // Get all sounds
            FileManager.getAllSounds()
        }else{
            // Get the sounds of the selected category
            FileManager.getSoundsOfCategory(currentCategory)
        }
    }

    suspend fun loadCategories(){
        val categories = FileManager.getAllCategories()
        categories.add(0, Category.allSoundsCategory)

        categoriesData.value = categories
    }
}