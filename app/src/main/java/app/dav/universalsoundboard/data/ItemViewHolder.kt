package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import app.dav.davandroidlibrary.models.DavUser
import app.dav.universalsoundboard.MainActivity
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.PlayingSound
import app.dav.universalsoundboard.models.Sound
import java.util.*

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