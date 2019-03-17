package app.dav.universalsoundboard.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.dav.davandroidlibrary.models.DavUser
import app.dav.universalsoundboard.MainActivity
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.PlayingSound
import app.dav.universalsoundboard.models.Sound
import java.util.*
import kotlin.collections.ArrayList

class ItemViewHolder(){
    constructor(title: String) : this() {
        titleData.value = title
        showCategoryIconsData.value = false
        showPlayAllIconData.value = false
        soundsData.value = ArrayList<Sound>()
        categoriesData.value = ArrayList<Category>()
        playingSoundsData.value = ArrayList<PlayingSound>()
        userData.value = null
        isProgressBarVisibleData.value = true

        // Settings values
        showSoundTabsData.value = FileManager.showSoundTabsDefault
        showPlayingSoundsData.value = FileManager.showPlayingSoundsDefault
        showCategoriesOfSoundsData.value = FileManager.showCategoriesOfSoundsDefault
    }

    private var isLoadingSounds = false
    private var loadSoundsAgain = false

    var currentCategory: Category = Category.allSoundsCategory
    var mainActivity: MainActivity? = null
    private val titleData = MutableLiveData<String>()                           // titleData holds the current title
    val title: LiveData<String>
        get() =  titleData
    private val showCategoryIconsData = MutableLiveData<Boolean>()              // when showCategoryIconsData is true, the icons for categories are visible
    val showCategoryIcons: LiveData<Boolean>
        get() = showCategoryIconsData
    private val showPlayAllIconData = MutableLiveData<Boolean>()                // when showPlayAllIconData is true, the Play All button is visible
    val showPlayAllIcon: LiveData<Boolean>
        get() = showPlayAllIconData
    val allSounds = ArrayList<Sound>()                                          // allSounds holds all sounds
    var allSoundsChanged = true                                                 // when allSoundsChanged is true, the sounds will be reloaded from the database
    private val soundsData = MutableLiveData<ArrayList<Sound>>()                // soundsData holds the sounds that are currently displayed in the list
    val sounds: LiveData<ArrayList<Sound>>
        get() = soundsData
    private val favouriteSoundsData = MutableLiveData<ArrayList<Sound>>()           // favouriteSoundsData holds the the favourite sounds that are currently displayed in the favourite sounds list
    val favouriteSounds: LiveData<ArrayList<Sound>>
        get() = favouriteSoundsData
    private val categoriesData = MutableLiveData<ArrayList<Category>>()         // categoriesData holds all categories that are currently displayed in the list
    val categories: LiveData<ArrayList<Category>>
        get() = categoriesData
    private val playingSoundsData = MutableLiveData<ArrayList<PlayingSound>>()  // playingSoundsData holds all playingSounds that are currently displayed
    val playingSounds: LiveData<ArrayList<PlayingSound>>
        get() = playingSoundsData
    val notSavedPlayingSounds = ArrayList<PlayingSound>()                       // notSavedPlayingSounds holds the playingSounds that are not saved in the database but displayed in the list
    private val userData = MutableLiveData<DavUser>()                           // userData holds the DavUser object
    val user: LiveData<DavUser>
        get() = userData
    private val isProgressBarVisibleData = MutableLiveData<Boolean>()                // If true, the progress bar is visible
    val isProgressBarVisible: LiveData<Boolean>
        get() = isProgressBarVisibleData

    // Settings values
    private val showSoundTabsData = MutableLiveData<Boolean>()                  // if true shows the Sounds and Favourites tabs on the SoundFragment
    val showSoundTabs: LiveData<Boolean>
        get() = showSoundTabsData
    private val savePlayingSoundsData = MutableLiveData<Boolean>()              // if true, the playing sounds are being saved in the database
    val savePlayingSounds: LiveData<Boolean>
        get() = savePlayingSoundsData
    private val showPlayingSoundsData = MutableLiveData<Boolean>()              // if true, the playing sounds list is visible
    val showPlayingSounds: LiveData<Boolean>
        get() = showPlayingSoundsData
    private val showCategoriesOfSoundsData = MutableLiveData<Boolean>()         // If true, the sound items show the icons of it's categories
    val showCategoriesOfSounds: LiveData<Boolean>
        get() = showCategoriesOfSoundsData

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

    fun setIsProgressBarVisible(value: Boolean){
        isProgressBarVisibleData.value = value
    }

    fun setShowSoundTabs(value: Boolean){
        showSoundTabsData.value = value
    }

    fun setSavePlayingSounds(value: Boolean){
        savePlayingSoundsData.value = value
    }

    fun setShowPlayingSounds(value: Boolean){
        showPlayingSoundsData.value = value
    }

    fun setShowCategoriesOfSounds(value: Boolean){
        showCategoriesOfSoundsData.value = value
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

        isProgressBarVisibleData.value = true

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

        // Get the favourite sounds
        val soundsList = soundsData.value
        favouriteSoundsData.value = soundsList?.filter { it.favourite } as ArrayList<Sound>

        isLoadingSounds = false
        if(loadSoundsAgain){
            loadSoundsAgain = false
            loadSounds()
        }

        setShowPlayAllIcon(sounds.value?.size ?: 0 > 0)
        isProgressBarVisibleData.value = false
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