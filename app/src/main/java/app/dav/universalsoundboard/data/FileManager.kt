package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
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

    fun addCategory(uuid: UUID?, name: String, icon: String) : Category?{
        // Generate a new uuid if necessary
        val newUuid: UUID = if(uuid == null) UUID.randomUUID() else uuid

        // Check if an object with the uuid already exists
        if(DatabaseOperations.getObject(newUuid) != null) return null

        DatabaseOperations.createCategory(newUuid, name, icon)
        return Category(newUuid, name, icon)
    }

    fun convertStringToCategoryIcon(icon: String) : Int{
        return when(icon){
            Category.Icons.ADD -> R.drawable.ic_add
            Category.Icons.EDIT -> R.drawable.ic_edit
            Category.Icons.CLEAR -> R.drawable.ic_clear
            Category.Icons.CLEAR_2 -> R.drawable.ic_clear
            Category.Icons.SEARCH -> R.drawable.ic_search
            Category.Icons.CHECK -> R.drawable.ic_check
            Category.Icons.CHECK_2 -> R.drawable.ic_check
            Category.Icons.CHECK_CIRCLE_OUTLINE -> R.drawable.ic_check_circle_outline
            Category.Icons.PERSON -> R.drawable.ic_person
            Category.Icons.PEOPLE -> R.drawable.ic_people
            Category.Icons.PORTRAIT -> R.drawable.ic_portrait
            Category.Icons.PERSON_PIN_CIRCLE -> R.drawable.ic_person_pin_circle
            Category.Icons.MOOD -> R.drawable.ic_mood
            Category.Icons.SENTIMENT_SATISFIED -> R.drawable.ic_sentiment_satisfied
            Category.Icons.THUMB_UP -> R.drawable.ic_thumb_up
            Category.Icons.THUMB_DOWN -> R.drawable.ic_thumb_down
            Category.Icons.STAR_BORDER -> R.drawable.ic_star_border
            Category.Icons.STAR -> R.drawable.ic_star
            Category.Icons.PHONE -> R.drawable.ic_phone
            Category.Icons.MAIL_OUTLINE -> R.drawable.ic_mail_outline
            Category.Icons.DRAFTS -> R.drawable.ic_drafts
            Category.Icons.ALTERNATE_EMAIL -> R.drawable.ic_alternate_email
            Category.Icons.PHOTO_CAMERA -> R.drawable.ic_photo_camera
            Category.Icons.VIDEOCAM -> R.drawable.ic_videocam
            Category.Icons.DUO -> R.drawable.ic_duo
            Category.Icons.SLIDESHOW -> R.drawable.ic_slideshow
            Category.Icons.MIC -> R.drawable.ic_mic
            Category.Icons.MUSIC_NOTE -> R.drawable.ic_music_note
            Category.Icons.QUEUE_MUSIC -> R.drawable.ic_queue_music
            Category.Icons.VOLUME_UP -> R.drawable.ic_volume_up
            Category.Icons.VOLUME_OFF -> R.drawable.ic_volume_off
            Category.Icons.PLAY_ARROW -> R.drawable.ic_play_arrow
            Category.Icons.PAUSE -> R.drawable.ic_pause
            Category.Icons.SHUFFLE -> R.drawable.ic_shuffle
            Category.Icons.BOOKMARK -> R.drawable.ic_bookmark
            Category.Icons.BOOKMARK_BORDER -> R.drawable.ic_bookmark_border
            Category.Icons.REFRESH -> R.drawable.ic_refresh
            Category.Icons.SYNC -> R.drawable.ic_sync
            Category.Icons.ROTATE_LEFT -> R.drawable.ic_rotate_left
            Category.Icons.REPEAT -> R.drawable.ic_repeat
            Category.Icons.SHARE -> R.drawable.ic_share
            Category.Icons.CHAT_BUBBLE_OUTLINE -> R.drawable.ic_chat_bubble_outline
            Category.Icons.COMMENT -> R.drawable.ic_comment
            Category.Icons.ANNOUNCEMENT -> R.drawable.ic_announcement
            Category.Icons.OUTLINED_FLAG -> R.drawable.ic_outlined_flag
            Category.Icons.NOTE -> R.drawable.ic_note
            Category.Icons.DESCRIPTION -> R.drawable.ic_description
            Category.Icons.BUG_REPORT -> R.drawable.ic_bug_report
            Category.Icons.REPORT -> R.drawable.ic_report
            Category.Icons.VISIBILITY -> R.drawable.ic_visibility
            Category.Icons.MY_LOCATION -> R.drawable.ic_my_location
            Category.Icons.VERTICAL_ALIGN_BOTTOM -> R.drawable.ic_vertical_align_bottom
            Category.Icons.HELP_OUTLINE -> R.drawable.ic_help_outline
            Category.Icons.PRIORITY_HIGH -> R.drawable.ic_priority_high
            Category.Icons.FILTER_LIST -> R.drawable.ic_filter_list
            Category.Icons.LINK -> R.drawable.ic_link
            Category.Icons.ATTACH_FILE -> R.drawable.ic_attach_file
            Category.Icons.VPN_KEY -> R.drawable.ic_vpn_key
            Category.Icons.SETTINGS -> R.drawable.ic_settings
            Category.Icons.KEYBOARD -> R.drawable.ic_keyboard
            Category.Icons.SMARTPHONE -> R.drawable.ic_smartphone
            Category.Icons.SAVE -> R.drawable.ic_save
            Category.Icons.DELETE-> R.drawable.ic_delete
            Category.Icons.LOCAL_OFFER -> R.drawable.ic_local_offer
            Category.Icons.SCHOOL -> R.drawable.ic_school
            Category.Icons.IMPORT_CONTACTS -> R.drawable.ic_import_contacts
            Category.Icons.SHOP -> R.drawable.ic_shop
            Category.Icons.BUILD -> R.drawable.ic_build
            Category.Icons.RESTAURANT -> R.drawable.ic_restaurant
            Category.Icons.LANGUAGE -> R.drawable.ic_language
            Category.Icons.PUBLIC -> R.drawable.ic_public
            Category.Icons.LOCATION_ON -> R.drawable.ic_location_on
            Category.Icons.NAVIGATION -> R.drawable.ic_navigation
            Category.Icons.HOME -> R.drawable.ic_home
            Category.Icons.NATURE_PEOPLE -> R.drawable.ic_nature_people
            Category.Icons.CLOUD_QUEUE -> R.drawable.ic_cloud_queue
            else -> R.drawable.ic_home
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