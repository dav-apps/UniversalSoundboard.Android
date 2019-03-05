package app.dav.universalsoundboard.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.data.FileManager.PLAY_ONE_SOUND_AT_ONCE_KEY
import app.dav.universalsoundboard.data.FileManager.SAVE_PLAYING_SOUNDS_KEY
import app.dav.universalsoundboard.data.FileManager.playOneSoundAtOnce
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private var isInitialized = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        privacy_policy_textview.movementMethod = LinkMovementMethod.getInstance()
        usb_on_github_textview.movementMethod = LinkMovementMethod.getInstance()
        usb_for_windows_textview.movementMethod = LinkMovementMethod.getInstance()

        settings_play_one_sound_at_once_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setBooleanValue(PLAY_ONE_SOUND_AT_ONCE_KEY, isChecked)
        }

        settings_save_playing_sounds_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setBooleanValue(SAVE_PLAYING_SOUNDS_KEY, isChecked)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    fun updateValues(){
        isInitialized = false

        val playOneSoundAtOnce = FileManager.getBooleanValue(PLAY_ONE_SOUND_AT_ONCE_KEY, playOneSoundAtOnce)
        val savePlayingSounds = FileManager.getBooleanValue(SAVE_PLAYING_SOUNDS_KEY, FileManager.savePlayingSounds)

        // Set the values of the switches
        settings_play_one_sound_at_once_switch.isChecked = playOneSoundAtOnce
        settings_save_playing_sounds_switch.isChecked = savePlayingSounds
        isInitialized = true
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
