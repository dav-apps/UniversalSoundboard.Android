package app.dav.universalsoundboard.models

import app.dav.universalsoundboard.R
import java.util.*


class Category(val uuid: UUID, var name: String, var icon: String){

    fun getIconImageResource() : Int{
        return Category.convertStringToIconResourceId(icon)
    }

    companion object {
        fun getIconResourceIds() : Array<Int>{
            return arrayOf(
                    R.drawable.ic_add,
                    R.drawable.ic_edit,
                    R.drawable.ic_clear,
                    R.drawable.ic_search,
                    R.drawable.ic_check,
                    R.drawable.ic_check_circle_outline,
                    R.drawable.ic_person,
                    R.drawable.ic_people,
                    R.drawable.ic_portrait,
                    R.drawable.ic_person_pin_circle,
                    R.drawable.ic_mood,
                    R.drawable.ic_sentiment_satisfied,
                    R.drawable.ic_thumb_up,
                    R.drawable.ic_thumb_down,
                    R.drawable.ic_star_border,
                    R.drawable.ic_star,
                    R.drawable.ic_phone,
                    R.drawable.ic_mail_outline,
                    R.drawable.ic_drafts,
                    R.drawable.ic_alternate_email,
                    R.drawable.ic_photo_camera,
                    R.drawable.ic_videocam,
                    R.drawable.ic_duo,
                    R.drawable.ic_slideshow,
                    R.drawable.ic_mic,
                    R.drawable.ic_music_note,
                    R.drawable.ic_queue_music,
                    R.drawable.ic_volume_up,
                    R.drawable.ic_volume_off,
                    R.drawable.ic_play_arrow,
                    R.drawable.ic_pause,
                    R.drawable.ic_shuffle,
                    R.drawable.ic_bookmark,
                    R.drawable.ic_bookmark_border,
                    R.drawable.ic_refresh,
                    R.drawable.ic_sync,
                    R.drawable.ic_rotate_left,
                    R.drawable.ic_repeat,
                    R.drawable.ic_share,
                    R.drawable.ic_chat_bubble_outline,
                    R.drawable.ic_comment,
                    R.drawable.ic_announcement,
                    R.drawable.ic_outlined_flag,
                    R.drawable.ic_note,
                    R.drawable.ic_description,
                    R.drawable.ic_bug_report,
                    R.drawable.ic_report,
                    R.drawable.ic_visibility,
                    R.drawable.ic_my_location,
                    R.drawable.ic_vertical_align_bottom,
                    R.drawable.ic_help_outline,
                    R.drawable.ic_priority_high,
                    R.drawable.ic_filter_list,
                    R.drawable.ic_link,
                    R.drawable.ic_attach_file,
                    R.drawable.ic_vpn_key,
                    R.drawable.ic_settings,
                    R.drawable.ic_keyboard,
                    R.drawable.ic_smartphone,
                    R.drawable.ic_save,
                    R.drawable.ic_delete,
                    R.drawable.ic_local_offer,
                    R.drawable.ic_school,
                    R.drawable.ic_import_contacts,
                    R.drawable.ic_shop,
                    R.drawable.ic_build,
                    R.drawable.ic_restaurant,
                    R.drawable.ic_language,
                    R.drawable.ic_public,
                    R.drawable.ic_location_on,
                    R.drawable.ic_navigation,
                    R.drawable.ic_home,
                    R.drawable.ic_nature_people,
                    R.drawable.ic_cloud_queue
            )
        }

        fun convertIconResourceIdToString(icon: Int) : String{
            return when(icon){
                R.drawable.ic_add -> Category.Icons.ADD
                R.drawable.ic_edit -> Category.Icons.EDIT
                R.drawable.ic_clear -> Category.Icons.CLEAR
                R.drawable.ic_search -> Category.Icons.SEARCH
                R.drawable.ic_check -> Category.Icons.CHECK
                R.drawable.ic_check_circle_outline -> Category.Icons.CHECK_CIRCLE_OUTLINE
                R.drawable.ic_person -> Category.Icons.PERSON
                R.drawable.ic_people -> Category.Icons.PEOPLE
                R.drawable.ic_portrait -> Category.Icons.PORTRAIT
                R.drawable.ic_person_pin_circle -> Category.Icons.PERSON_PIN_CIRCLE
                R.drawable.ic_mood -> Category.Icons.MOOD
                R.drawable.ic_sentiment_satisfied -> Category.Icons.MOOD
                R.drawable.ic_thumb_up -> Category.Icons.THUMB_UP
                R.drawable.ic_thumb_down -> Category.Icons.THUMB_DOWN
                R.drawable.ic_star_border -> Category.Icons.STAR_BORDER
                R.drawable.ic_star -> Category.Icons.STAR
                R.drawable.ic_phone -> Category.Icons.PHONE
                R.drawable.ic_mail_outline -> Category.Icons.MAIL_OUTLINE
                R.drawable.ic_drafts -> Category.Icons.DRAFTS
                R.drawable.ic_alternate_email -> Category.Icons.ALTERNATE_EMAIL
                R.drawable.ic_photo_camera -> Category.Icons.PHOTO_CAMERA
                R.drawable.ic_videocam -> Category.Icons.VIDEOCAM
                R.drawable.ic_duo -> Category.Icons.DUO
                R.drawable.ic_slideshow -> Category.Icons.SLIDESHOW
                R.drawable.ic_mic -> Category.Icons.MIC
                R.drawable.ic_music_note -> Category.Icons.MUSIC_NOTE
                R.drawable.ic_queue_music -> Category.Icons.QUEUE_MUSIC
                R.drawable.ic_volume_up -> Category.Icons.VOLUME_UP
                R.drawable.ic_volume_off -> Category.Icons.VOLUME_OFF
                R.drawable.ic_play_arrow -> Category.Icons.PLAY_ARROW
                R.drawable.ic_pause -> Category.Icons.PAUSE
                R.drawable.ic_shuffle -> Category.Icons.SHUFFLE
                R.drawable.ic_bookmark -> Category.Icons.BOOKMARK
                R.drawable.ic_bookmark_border -> Category.Icons.BOOKMARK_BORDER
                R.drawable.ic_refresh -> Category.Icons.REFRESH
                R.drawable.ic_sync -> Category.Icons.SYNC
                R.drawable.ic_rotate_left -> Category.Icons.ROTATE_LEFT
                R.drawable.ic_repeat -> Category.Icons.REPEAT
                R.drawable.ic_share -> Category.Icons.SHARE
                R.drawable.ic_chat_bubble_outline -> Category.Icons.CHAT_BUBBLE_OUTLINE
                R.drawable.ic_comment -> Category.Icons.COMMENT
                R.drawable.ic_announcement -> Category.Icons.ANNOUNCEMENT
                R.drawable.ic_outlined_flag -> Category.Icons.OUTLINED_FLAG
                R.drawable.ic_note -> Category.Icons.NOTE
                R.drawable.ic_description -> Category.Icons.DESCRIPTION
                R.drawable.ic_bug_report -> Category.Icons.BUG_REPORT
                R.drawable.ic_report -> Category.Icons.REPORT
                R.drawable.ic_visibility -> Category.Icons.VISIBILITY
                R.drawable.ic_my_location -> Category.Icons.MY_LOCATION
                R.drawable.ic_vertical_align_bottom -> Category.Icons.VERTICAL_ALIGN_BOTTOM
                R.drawable.ic_help_outline -> Category.Icons.HELP_OUTLINE
                R.drawable.ic_priority_high -> Category.Icons.PRIORITY_HIGH
                R.drawable.ic_filter_list -> Category.Icons.FILTER_LIST
                R.drawable.ic_link -> Category.Icons.LINK
                R.drawable.ic_attach_file -> Category.Icons.ATTACH_FILE
                R.drawable.ic_vpn_key -> Category.Icons.VPN_KEY
                R.drawable.ic_settings -> Category.Icons.SETTINGS
                R.drawable.ic_keyboard -> Category.Icons.KEYBOARD
                R.drawable.ic_smartphone -> Category.Icons.SMARTPHONE
                R.drawable.ic_save -> Category.Icons.SAVE
                R.drawable.ic_delete -> Category.Icons.DELETE
                R.drawable.ic_local_offer -> Category.Icons.LOCAL_OFFER
                R.drawable.ic_school -> Category.Icons.SCHOOL
                R.drawable.ic_import_contacts -> Category.Icons.IMPORT_CONTACTS
                R.drawable.ic_shop -> Category.Icons.SHOP
                R.drawable.ic_build -> Category.Icons.BUILD
                R.drawable.ic_restaurant -> Category.Icons.RESTAURANT
                R.drawable.ic_language -> Category.Icons.LANGUAGE
                R.drawable.ic_public -> Category.Icons.PUBLIC
                R.drawable.ic_location_on -> Category.Icons.LOCATION_ON
                R.drawable.ic_navigation -> Category.Icons.NAVIGATION
                R.drawable.ic_home -> Category.Icons.HOME
                R.drawable.ic_nature_people -> Category.Icons.NATURE_PEOPLE
                R.drawable.ic_cloud_queue -> Category.Icons.CLOUD_QUEUE
                else -> Category.Icons.HOME
            }
        }

        fun convertStringToIconResourceId(icon: String) : Int{
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
    }

    object Icons{
        const val ADD = "\uE710"
        const val EDIT = "\uE70F"
        const val CLEAR = "\uE711"
        const val CLEAR_2 = "\uE894"
        const val SEARCH = "\uE71E"
        const val CHECK = "\uE73E"
        const val CHECK_2 = "\uE8FB"
        const val CHECK_CIRCLE_OUTLINE = "\uE73A"
        const val PERSON = "\uE77B"
        const val PEOPLE = "\uE716"
        const val PORTRAIT = "\uE8B8"
        const val PERSON_PIN_CIRCLE = "\uE7EE"
        const val MOOD = "\uE76E"
        const val SENTIMENT_SATISFIED = "\uE899"
        const val THUMB_UP = "\uE8E1"
        const val THUMB_DOWN = "\uE8E0"
        const val STAR_BORDER = "\uE734"
        const val STAR = "\uE735"
        const val PHONE = "\uE717"
        const val MAIL_OUTLINE = "\uE715"
        const val DRAFTS = "\uE8C3"
        const val ALTERNATE_EMAIL = "\uE910"
        const val PHOTO_CAMERA = "\uE722"
        const val VIDEOCAM = "\uE714"
        const val DUO = "\uE8AA"
        const val SLIDESHOW = "\uE786"
        const val MIC = "\uE720"
        const val MUSIC_NOTE = "\uE8D6"
        const val QUEUE_MUSIC = "\uE90B"
        const val VOLUME_UP = "\uE767"
        const val VOLUME_OFF = "\uE74F"
        const val PLAY_ARROW = "\uE768"
        const val PAUSE = "\uE769"
        const val SHUFFLE = "\uE8B1"
        const val BOOKMARK = "\uE718"
        const val BOOKMARK_BORDER = "\uE77A"
        const val REFRESH = "\uE72C"
        const val SYNC = "\uE895"
        const val ROTATE_LEFT = "\uE7AD"
        const val REPEAT = "\uE8EB"
        const val SHARE = "\uE72D"
        const val CHAT_BUBBLE_OUTLINE = "\uE8BD"
        const val COMMENT = "\uE90A"
        const val ANNOUNCEMENT = "\uE8F3"
        const val OUTLINED_FLAG = "\uE7C1"
        const val NOTE = "\uE77F"
        const val DESCRIPTION = "\uE7C3"
        const val BUG_REPORT = "\uE7EF"
        const val REPORT = "\uE730"
        const val VISIBILITY = "\uE890"
        const val MY_LOCATION = "\uE81D"
        const val VERTICAL_ALIGN_BOTTOM = "\uE896"
        const val HELP_OUTLINE = "\uE897"
        const val PRIORITY_HIGH = "\uE8C9"
        const val FILTER_LIST = "\uE71C"
        const val LINK = "\uE71B"
        const val ATTACH_FILE = "\uE723"
        const val VPN_KEY = "\uE8D7"
        const val SETTINGS = "\uE713"
        const val KEYBOARD = "\uE765"
        const val SMARTPHONE = "\uE8EA"
        const val SAVE = "\uE74E"
        const val DELETE = "\uE74D"
        const val LOCAL_OFFER = "\uE8EC"
        const val SCHOOL = "\uE8EF"
        const val IMPORT_CONTACTS = "\uE8F1"
        const val SHOP = "\uE719"
        const val BUILD = "\uE90F"
        const val RESTAURANT = "\uE8C6"
        const val LANGUAGE = "\uE774"
        const val PUBLIC = "\uE909"
        const val LOCATION_ON = "\uE707"
        const val NAVIGATION = "\uE8F0"
        const val HOME = "\uE80F"
        const val NATURE_PEOPLE = "\uE913"
        const val CLOUD_QUEUE = "\uE753"
    }
}