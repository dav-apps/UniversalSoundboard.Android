package app.dav.universalsoundboard.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.data.FileManager.PLAY_ONE_SOUND_AT_ONCE_KEY
import app.dav.universalsoundboard.data.FileManager.SAVE_PLAYING_SOUNDS_KEY
import app.dav.universalsoundboard.data.FileManager.playOneSoundAtOnce
import app.dav.universalsoundboard.data.FileManager.savePlayingSounds
import kotlinx.android.synthetic.main.settings_fragment.*


class SettingsFragment : Fragment() {
    private var isInitialized = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        privacy_policy_textview.movementMethod = LinkMovementMethod.getInstance()
        usb_on_github_textview.movementMethod = LinkMovementMethod.getInstance()

        settings_play_one_sound_at_once_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setSetting(PLAY_ONE_SOUND_AT_ONCE_KEY, isChecked)
        }

        settings_save_playing_sounds_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setSetting(SAVE_PLAYING_SOUNDS_KEY, isChecked)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    fun updateValues(){
        isInitialized = false

        val playOneSoundAtOnce = FileManager.getSetting(PLAY_ONE_SOUND_AT_ONCE_KEY) ?: playOneSoundAtOnce
        val savePlayingSounds = FileManager.getSetting(SAVE_PLAYING_SOUNDS_KEY) ?: savePlayingSounds

        // Set the values of the switches
        settings_play_one_sound_at_once_switch.isChecked = playOneSoundAtOnce
        settings_save_playing_sounds_switch.isChecked = savePlayingSounds
        isInitialized = true
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
