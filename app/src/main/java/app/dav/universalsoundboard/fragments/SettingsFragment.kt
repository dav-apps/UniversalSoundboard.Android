package app.dav.universalsoundboard.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager.PACKAGE_NAME
import app.dav.universalsoundboard.data.FileManager.PLAY_ONE_SOUND_AT_ONCE_KEY
import app.dav.universalsoundboard.data.FileManager.SAVE_PLAYING_SOUNDS_KEY
import app.dav.universalsoundboard.data.FileManager.playOneSoundAtOnce
import app.dav.universalsoundboard.data.FileManager.savePlayingSounds
import app.dav.universalsoundboard.viewmodels.SettingsViewModel
import kotlinx.android.synthetic.main.settings_fragment.*


class SettingsFragment : Fragment() {
    private lateinit var viewModel: SettingsViewModel
    private var isInitialized = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        privacy_policy_textview.movementMethod = LinkMovementMethod.getInstance()
        usb_on_github_textview.movementMethod = LinkMovementMethod.getInstance()

        settings_play_one_sound_at_once_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            saveSetting(PLAY_ONE_SOUND_AT_ONCE_KEY, isChecked)
        }

        settings_save_playing_sounds_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            saveSetting(SAVE_PLAYING_SOUNDS_KEY, isChecked)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    fun updateValues(){
        isInitialized = false
        val prefs = activity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE)

        val playOneSoundAtOnce = prefs?.getBoolean(PLAY_ONE_SOUND_AT_ONCE_KEY, playOneSoundAtOnce) ?: playOneSoundAtOnce
        val savePlayingSounds = prefs?.getBoolean(SAVE_PLAYING_SOUNDS_KEY, savePlayingSounds) ?: savePlayingSounds

        // Set the values of the switches
        settings_play_one_sound_at_once_switch.isChecked = playOneSoundAtOnce
        settings_save_playing_sounds_switch.isChecked = savePlayingSounds
        isInitialized = true
    }

    private fun saveSetting(key: String, value: Boolean){
        val prefs = activity?.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE) ?: return
        prefs.edit().putBoolean(key, value).apply()
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
