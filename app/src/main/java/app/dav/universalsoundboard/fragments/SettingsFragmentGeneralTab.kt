package app.dav.universalsoundboard.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.data.FileManager.PLAY_ONE_SOUND_AT_ONCE_KEY
import app.dav.universalsoundboard.data.FileManager.SAVE_PLAYING_SOUNDS_KEY
import app.dav.universalsoundboard.data.FileManager.SHOW_SOUND_TABS_KEY
import app.dav.universalsoundboard.data.FileManager.playOneSoundAtOnceDefault
import app.dav.universalsoundboard.data.FileManager.savePlayingSoundsDefault
import app.dav.universalsoundboard.data.FileManager.showSoundTabsDefault
import kotlinx.android.synthetic.main.fragment_settings_general_tab.*

class SettingsFragmentGeneralTab : Fragment() {
    private var isInitialized = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_general_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        privacy_policy_textview.movementMethod = LinkMovementMethod.getInstance()
        usb_on_github_textview.movementMethod = LinkMovementMethod.getInstance()
        usb_for_windows_textview.movementMethod = LinkMovementMethod.getInstance()

        settings_play_one_sound_at_once_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setBooleanValue(PLAY_ONE_SOUND_AT_ONCE_KEY, isChecked)
        }

        settings_show_favourites_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setBooleanValue(SHOW_SOUND_TABS_KEY, isChecked)
            FileManager.itemViewHolder.setShowSoundTabs(isChecked)
        }

        settings_save_playing_sounds_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setBooleanValue(SAVE_PLAYING_SOUNDS_KEY, isChecked)
            FileManager.itemViewHolder.setSavePlayingSounds(isChecked)
        }
    }

    private fun init(){
        val playOneSoundAtOnce = FileManager.getBooleanValue(PLAY_ONE_SOUND_AT_ONCE_KEY, playOneSoundAtOnceDefault)
        val showFavourites = FileManager.getBooleanValue(SHOW_SOUND_TABS_KEY, showSoundTabsDefault)
        val savePlayingSounds = FileManager.getBooleanValue(SAVE_PLAYING_SOUNDS_KEY, savePlayingSoundsDefault)

        // Set the values of the switches
        settings_play_one_sound_at_once_switch.isChecked = playOneSoundAtOnce
        settings_show_favourites_switch.isChecked = showFavourites
        settings_save_playing_sounds_switch.isChecked = savePlayingSounds

        FileManager.itemViewHolder.showPlayingSounds.observe(this, Observer {
            // Show or hide the Save playing sounds switch
            settings_save_playing_sounds_switch.visibility = if(it) View.VISIBLE else View.GONE
        })

        isInitialized = true
    }
}