package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import app.dav.davandroidlibrary.DavEnvironment
import app.dav.davandroidlibrary.models.DavUser
import app.dav.davandroidlibrary.models.TableObject
import app.dav.universalsoundboard.MainActivity
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.PlayingSound
import app.dav.universalsoundboard.models.Sound
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object FileManager{
    // Keys for shared preferences
    const val PACKAGE_NAME = "app.dav.universalsoundboard"
    const val PLAY_ONE_SOUND_AT_ONCE_KEY = "$PACKAGE_NAME.playOneSoundAtOnce"
    const val SAVE_PLAYING_SOUNDS_KEY = "$PACKAGE_NAME.savePlayingSounds"

    // Default values
    const val playOneSoundAtOnce = false
    const val savePlayingSounds = true

    val environment = DavEnvironment.Production

    // dav Keys
    private const val apiKeyProduction = "gHgHKRbIjdguCM4cv5481hdiF5hZGWZ4x12Ur-7v"
    private const val apiKeyDevelopment = "eUzs3PQZYweXvumcWvagRHjdUroGe5Mo7kN1inHm"
    val apiKey = if(environment == DavEnvironment.Production) apiKeyProduction else apiKeyDevelopment

    private const val loginImplicitUrlProduction = "https://dav-apps.tech/login_implicit"
    private const val loginImplicitUrlDevelopment = "https://30d34bb4.ngrok.io/login_implicit"
    val loginImplicitUrl = if(environment == DavEnvironment.Production) loginImplicitUrlProduction else loginImplicitUrlDevelopment

    private const val appIdProduction = 1                   // Dev: 4, Prod: 1
    private const val appIdDevelopment = 4
    val appId = if(environment == DavEnvironment.Production) appIdProduction else appIdDevelopment

    private const val soundFileTableIdProduction = 6        // Dev: 7, Prod: 6
    private const val soundFileTableIdDevelopment = 7
    val soundFileTableId = if(environment == DavEnvironment.Production) soundFileTableIdProduction else soundFileTableIdDevelopment

    private const val imageFileTableIdProduction = 7        // Dev: 9, Prod: 7
    private const val imageFileTableIdDevelopment = 9
    val imageFileTableId = if(environment == DavEnvironment.Production) imageFileTableIdProduction else imageFileTableIdDevelopment

    private const val categoryTableIdProduction = 8         // Dev: 5, Prod: 8
    private const val categoryTableIdDevelopment = 5
    val categoryTableId = if(environment == DavEnvironment.Production) categoryTableIdProduction else categoryTableIdDevelopment

    private const val soundTableIdProduction = 5            // Dev: 6, Prod: 5
    private const val soundTableIdDevelopment = 6
    val soundTableId = if(environment == DavEnvironment.Production) soundTableIdProduction else soundTableIdDevelopment

    private const val playingSoundTableIdProduction = 9     // Dev: 8, Prod: 9
    private const val playingSoundTableIdDevelopment = 8
    val playingSoundTableId = if(environment == DavEnvironment.Production) playingSoundTableIdProduction else playingSoundTableIdDevelopment

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

    suspend fun showCategory(category: Category){
        itemViewHolder.currentCategory = category
        itemViewHolder.setShowCategoryIcons(category.uuid != Category.allSoundsCategory.uuid)
        itemViewHolder.setTitle(category.name)
        itemViewHolder.loadSounds()
    }

    // Sound functions
    suspend fun addSound(uuid: UUID?, name: String, categoryUuid: UUID?, audioFile: File){
        // Generate a new uuid if necessary
        val newUuid: UUID = if(uuid == null) UUID.randomUUID() else uuid

        // Create a uuid for the sound file
        val soundFileUuid = UUID.randomUUID()

        val categoryUuidString = if(categoryUuid == null) "" else categoryUuid.toString()

        // Copy the sound file
        DatabaseOperations.createSoundFile(soundFileUuid, audioFile)

        DatabaseOperations.createSound(newUuid, name, soundFileUuid.toString(), categoryUuidString)
        itemViewHolder.allSoundsChanged = true
    }

    suspend fun getAllSoundsFromDatabase() : ArrayList<Sound>{
        val tableObjects = DatabaseOperations.getAllSounds()
        val sounds = ArrayList<Sound>()

        for(obj in tableObjects){
            val sound = convertTableObjectToSound(obj) ?: continue
            val soundFileUuid = obj.getPropertyValue(soundTableSoundUuidPropertyName) ?: continue
            DatabaseOperations.getObject(UUID.fromString(soundFileUuid)) ?: continue

            sounds.add(sound)
        }

        return sounds
    }

    suspend fun getSoundsOfCategory(categoryUuid: UUID) : ArrayList<Sound>{
        val sounds = ArrayList<Sound>()

        for(sound in itemViewHolder.allSounds){
            if(sound.category?.uuid == categoryUuid){
                sounds.add(sound)
            }
        }

        return sounds
    }

    suspend fun getSound(uuid: UUID) : Sound?{
        val soundTableObject = DatabaseOperations.getObject(uuid) ?: return null
        return convertTableObjectToSound(soundTableObject)
    }

    suspend fun renameSound(uuid: UUID, newName: String){
        DatabaseOperations.updateSound(uuid, newName, null, null, null, null)
        itemViewHolder.allSoundsChanged = true
    }

    suspend fun setCategoryOfSound(soundUuid: UUID, categoryUuid: UUID){
        DatabaseOperations.updateSound(soundUuid, null, null, null, null, categoryUuid.toString())
        itemViewHolder.allSoundsChanged = true
    }

    suspend fun setSoundAsFavourite(uuid: UUID, favourite: Boolean){
        DatabaseOperations.updateSound(uuid, null, favourite.toString(), null, null, null)
        itemViewHolder.allSoundsChanged = true
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

        itemViewHolder.allSoundsChanged = true
    }

    suspend fun deleteSound(uuid: UUID){
        DatabaseOperations.deleteSound(uuid)
        itemViewHolder.allSoundsChanged = true
    }
    // End Sound functions

    // Category functions
    suspend fun getAllCategories() : ArrayList<Category>{
        val categories = ArrayList<Category>()

        for(obj in DatabaseOperations.getAllCategories()){
            val category = convertTableObjectToCategory(obj) ?: continue
            categories.add(category)
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
        itemViewHolder.loadCategories()
        val category = Category(newUuid, name, icon)
        return category
    }

    suspend fun updateCategory(uuid: UUID, name: String, icon: String){
        DatabaseOperations.updateCategory(uuid, name, icon)
        itemViewHolder.setTitle(name)
        itemViewHolder.loadCategories()
    }

    suspend fun deleteCategory(uuid: UUID){
        DatabaseOperations.deleteCategory(uuid)
        itemViewHolder.loadCategories()
    }
    // End Category functions

    // PlayingSound functions
    suspend fun addPlayingSound(uuid: UUID?, sounds: ArrayList<Sound>, current: Int, repetitions: Int, randomly: Boolean, volume: Double) : PlayingSound?{
        val newUuid: UUID = uuid ?: UUID.randomUUID()

        // Check if an object with the uuid already exists
        if(DatabaseOperations.getObject(newUuid) != null) return null

        var newVolume = volume
        if(newVolume >= 1) newVolume = 1.0
        if(newVolume <= 0) newVolume = 0.0

        val playingSound = PlayingSound(newUuid, current, sounds, repetitions, randomly, newVolume)

        // Check if playing sounds should be saved
        if(getBooleanValue(SAVE_PLAYING_SOUNDS_KEY, savePlayingSounds)){
            DatabaseOperations.createPlayingSound(newUuid, sounds, current, repetitions, randomly, newVolume)
        }else{
            itemViewHolder.notSavedPlayingSounds.add(playingSound)
        }
        itemViewHolder.loadPlayingSounds()
        return playingSound
    }

    suspend fun getPlayingSound(uuid: UUID) : PlayingSound?{
        val playingSound = DatabaseOperations.getObject(uuid)
        return if(playingSound != null){
            convertTableObjectToPlayingSound(playingSound)
        } else {
            return itemViewHolder.notSavedPlayingSounds.find { p -> p.uuid == uuid }
        }
    }

    suspend fun getAllPlayingSounds() : ArrayList<PlayingSound>{
        val playingSounds = ArrayList<PlayingSound>()

        for(obj in DatabaseOperations.getAllPlayingSounds()){
            val playingSound = convertTableObjectToPlayingSound(obj) ?: continue
            playingSounds.add(playingSound)
        }

        return playingSounds
    }

    suspend fun setCurrentOfPlayingSound(uuid: UUID, current: Int){
        DatabaseOperations.updatePlayingSound(uuid, null, current, null, null, null)
        itemViewHolder.updateNotSavedPlayingSound(uuid, null, current, null, null, null)
    }

    suspend fun setRepetitionsOfPlayingSound(uuid: UUID, repetitions: Int){
        DatabaseOperations.updatePlayingSound(uuid, null, null, repetitions, null, null)
        itemViewHolder.updateNotSavedPlayingSound(uuid, null, null, repetitions, null, null)
    }

    suspend fun setVolumeOfPlayingSound(uuid: UUID, volume: Double){
        DatabaseOperations.updatePlayingSound(uuid, null, null, null, null, volume)
        itemViewHolder.updateNotSavedPlayingSound(uuid, null, null, null, null, volume)
    }

    suspend fun deletePlayingSound(uuid: UUID){
        DatabaseOperations.deletePlayingSound(uuid)
        itemViewHolder.removeNotSavedPlayingSound(uuid)
        itemViewHolder.loadPlayingSounds()
    }

    suspend fun deleteAllPlayingSounds(){
        val playingSounds = FileManager.itemViewHolder.playingSounds.value
        if(playingSounds != null){
            val playingSoundUuidsList = java.util.ArrayList<UUID>()
            for(p in playingSounds) playingSoundUuidsList.add(p.uuid)

            for (uuid in playingSoundUuidsList){
                FileManager.deletePlayingSound(uuid)
            }
        }
    }
    // End PlayingSound functions

    private suspend fun convertTableObjectToSound(tableObject: TableObject) : Sound?{
        if(tableObject.tableId != FileManager.soundTableId) return null

        // Get name
        val name = tableObject.getPropertyValue(soundTableNamePropertyName) ?: ""

        // Get favourite
        var favourite = false
        val favouriteString = tableObject.getPropertyValue(soundTableFavouritePropertyName)
        favourite = if(favouriteString != null) favouriteString.toBoolean() else false

        val sound = Sound(tableObject.uuid, name, null, favourite, FileManager.getAudioFileOfSound(tableObject.uuid), null)

        // Get category
        val categoryUuidString = tableObject.getPropertyValue(soundTableCategoryUuidPropertyName)
        if(categoryUuidString != null){
            val categoryUuid = UUID.fromString(categoryUuidString)
            val category = getCategory(categoryUuid)
            if(category != null) sound.category = category
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

    private suspend fun convertTableObjectToPlayingSound(tableObject: TableObject) : PlayingSound?{
        if(tableObject.tableId != FileManager.playingSoundTableId) return null

        // Get the sounds
        val soundIds = tableObject.getPropertyValue(FileManager.playingSoundTableSoundIdsPropertyName)
        val sounds = ArrayList<Sound>()

        if(soundIds != null && !soundIds.isNullOrEmpty()){
            for (uuidString in soundIds.split(',')){
                val uuid = UUID.fromString(uuidString)
                val sound = getSound(uuid)
                if(sound != null) sounds.add(sound)
            }
        }

        if(sounds.count() == 0){
            // Delete the PlayingSound
            deletePlayingSound(tableObject.uuid)
            return null
        }

        // Get current
        val currentString = tableObject.getPropertyValue(FileManager.playingSoundTableCurrentPropertyName)
        val current = currentString?.toIntOrNull() ?: 0

        // Get volume
        val volumeString = tableObject.getPropertyValue(FileManager.playingSoundTableVolumePropertyName)
        val volume = volumeString?.toDoubleOrNull() ?: 1.0

        // Get repetitions
        val repetitionsString = tableObject.getPropertyValue(FileManager.playingSoundTableRepetitionsPropertyName)
        val repetitions = repetitionsString?.toIntOrNull() ?: 1

        // Get randomly
        val randomlyString = tableObject.getPropertyValue(FileManager.playingSoundTableRandomlyPropertyName)
        val randomly = randomlyString?.toBoolean() ?: false

        return PlayingSound(tableObject.uuid, current, sounds, repetitions, randomly, volume)
    }

    fun getDavDataPath(filesDir: String) : File{
        val path = "$filesDir/dav"
        val dir = File(path)
        if(!dir.exists()){
            dir.mkdir()
        }
        return dir
    }

    suspend fun getAudioFileOfSound(uuid: UUID) : File?{
        val soundFileTableObject = getSoundFileTableObject(uuid) ?: return null
        return soundFileTableObject.file
    }

    private suspend fun getSoundFileTableObject(soundUuid: UUID) : TableObject?{
        val soundTableObject = DatabaseOperations.getObject(soundUuid) ?: return null
        val soundFileUuidString = soundTableObject.getPropertyValue(soundTableSoundUuidPropertyName) ?: return null
        val soundFileUuid = UUID.fromString(soundFileUuidString)
        return DatabaseOperations.getObject(soundFileUuid)
    }

    private suspend fun getImageFileTableObject(soundUuid: UUID) : TableObject?{
        val soundTableObject = DatabaseOperations.getObject(soundUuid) ?: return null
        val imageFileUuidString = soundTableObject.getPropertyValue(soundTableImageUuidPropertyName) ?: return null
        val imageFileUuid = UUID.fromString(imageFileUuidString)
        return DatabaseOperations.getObject(imageFileUuid)
    }

    fun setBooleanValue(key: String, value: Boolean){
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBooleanValue(key: String, defaultValue: Boolean) : Boolean{
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return defaultValue
        return prefs.getBoolean(key, defaultValue)
    }

    fun setStringValue(key: String, value: String){
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return
        prefs.edit().putString(key, value).apply()
    }

    fun getStringValue(key: String, defaultValue: String) : String{
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return defaultValue
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    fun setLongValue(key: String, value: Long){
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return
        prefs.edit().putLong(key, value).apply()
    }

    fun getLongValue(key: String, defaultValue: Long) : Long{
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return defaultValue
        return prefs.getLong(key, defaultValue)
    }

    fun setIntValue(key: String, value: Int){
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return
        prefs.edit().putInt(key, value).apply()
    }

    fun getIntValue(key: String, defaultValue: Int) : Int{
        val prefs = itemViewHolder.mainActivity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return defaultValue
        return prefs.getInt(key, defaultValue)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = itemViewHolder.mainActivity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}

class ItemViewHolder(){
    constructor(title: String) : this() {
        titleData.value = title
        showCategoryIconsData.value = false
        soundsData.value = ArrayList<Sound>()
        categoriesData.value = ArrayList<Category>()
        playingSoundsData.value = ArrayList<PlayingSound>()
        userData.value = null
    }

    private var isLoadingSounds = false
    private var loadSoundsAgain = false

    var currentCategory: Category = Category.allSoundsCategory
    var mainActivity: MainActivity? = null
    // titleData holds the current title
    private val titleData = MutableLiveData<String>()
    val title: LiveData<String>
        get() =  titleData
    // when showCategoryIconsData is true, the icons for categories are visible
    private val showCategoryIconsData = MutableLiveData<Boolean>()
    val showCategoryIcons: LiveData<Boolean>
        get() = showCategoryIconsData
    // when showPlayAllIconData is true, the Play All button is visible
    private val showPlayAllIconData = MutableLiveData<Boolean>()
    val showPlayAllIcon: LiveData<Boolean>
        get() = showPlayAllIconData
    // allSounds holds all sounds
    val allSounds = ArrayList<Sound>()
    // when allSoundsChanged is true, the sounds will be reloaded from the database
    var allSoundsChanged = true
    // soundsData holds the sounds that are currently displayed in the list
    private val soundsData = MutableLiveData<ArrayList<Sound>>()
    val sounds: LiveData<ArrayList<Sound>>
        get() = soundsData
    // categoriesData holds all categories that are currently displayed in the list
    private val categoriesData = MutableLiveData<ArrayList<Category>>()
    val categories: LiveData<ArrayList<Category>>
        get() = categoriesData
    // playingSoundsData holds all playingSounds that are currently displayed
    private val playingSoundsData = MutableLiveData<ArrayList<PlayingSound>>()
    val playingSounds: LiveData<ArrayList<PlayingSound>>
        get() = playingSoundsData
    // notSavedPlayingSounds holds the playingSounds that are not saved in the database but displayed in the list
    val notSavedPlayingSounds = ArrayList<PlayingSound>()
    // userData holds the DavUser object
    private val userData = MutableLiveData<DavUser>()
    val user: LiveData<DavUser>
        get() = userData

    fun setTitle(value: String){
        titleData.value = value
    }

    fun setShowCategoryIcons(showCategoryIcons: Boolean){
        showCategoryIconsData.value = showCategoryIcons
    }

    fun setShowPlayAllIcon(showPlayAllIcon: Boolean){
        showPlayAllIconData.value = showPlayAllIcon
    }

    fun setUser(user: DavUser){
        userData.value = user
    }

    fun updateNotSavedPlayingSound(uuid: UUID, sounds: ArrayList<Sound>?, current: Int?, repetitions: Int?, randomly: Boolean?, volume: Double?){
        val playingSound = notSavedPlayingSounds.find { p -> p.uuid == uuid } ?: return

        if(sounds != null) playingSound.sounds = sounds
        if(current != null) playingSound.currentSound = current
        if(repetitions != null) playingSound.repetitions = repetitions
        if(randomly != null) playingSound.randomly = randomly
        if(volume != null) playingSound.volume = volume
    }

    fun removeNotSavedPlayingSound(uuid: UUID){
        val playingSound = notSavedPlayingSounds.find { p -> p.uuid == uuid } ?: return
        notSavedPlayingSounds.remove(playingSound)
    }

    suspend fun loadSounds(){
        if(isLoadingSounds){
            loadSoundsAgain = true
            return
        }else
            isLoadingSounds = true

        if(allSoundsChanged){
            allSoundsChanged = false
            // Get all sounds from the database
            allSounds.clear()
            for (sound in FileManager.getAllSoundsFromDatabase())
                allSounds.add(sound)
        }

        soundsData.value = if(currentCategory.uuid == Category.allSoundsCategory.uuid){
            // Get all sounds
            allSounds
        }else{
            // Get the sounds of the selected category
            FileManager.getSoundsOfCategory(currentCategory.uuid)
        }

        isLoadingSounds = false
        if(loadSoundsAgain){
            loadSoundsAgain = false
            loadSounds()
        }
    }

    suspend fun loadCategories(){
        val categories = FileManager.getAllCategories()
        categories.add(0, Category.allSoundsCategory)

        categoriesData.value = categories
    }

    suspend fun loadPlayingSounds(){
        val currentPlayingSounds = playingSoundsData.value ?: return
        val playingSounds = FileManager.getAllPlayingSounds()
        val playingSoundUuids = ArrayList<UUID>()

        // Add new PlayingSounds
        for(playingSound in playingSounds){
            playingSoundUuids.add(playingSound.uuid)
            if(currentPlayingSounds.find { p -> p.uuid == playingSound.uuid } == null){
                currentPlayingSounds.add(playingSound)
            }
        }

        // Remove old PlayingSounds
        currentPlayingSounds.retainAll { p -> playingSoundUuids.contains(p.uuid) }

        // Add playing sounds that are not saved in the database
        for (playingSound in notSavedPlayingSounds){
            currentPlayingSounds.add(playingSound)
        }

        playingSoundsData.value = currentPlayingSounds
    }
}