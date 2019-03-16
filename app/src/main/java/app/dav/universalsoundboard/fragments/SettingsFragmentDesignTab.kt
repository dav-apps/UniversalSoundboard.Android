package app.dav.universalsoundboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.data.FileManager.SHOW_CATEGORIES_OF_SOUNDS_KEY
import app.dav.universalsoundboard.data.FileManager.SHOW_PLAYING_SOUNDS_KEY
import app.dav.universalsoundboard.data.FileManager.showCategoriesOfSoundsDefault
import app.dav.universalsoundboard.data.FileManager.showPlayingSoundsDefault
import kotlinx.android.synthetic.main.fragment_settings_design_tab.*

class SettingsFragmentDesignTab : Fragment() {
    private var isInitialized = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_design_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()

        settings_show_playing_sounds_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setBooleanValue(SHOW_PLAYING_SOUNDS_KEY, isChecked)
            FileManager.itemViewHolder.setShowPlayingSounds(isChecked)
        }

        settings_show_categories_of_sounds_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isInitialized) return@setOnCheckedChangeListener
            FileManager.setBooleanValue(SHOW_CATEGORIES_OF_SOUNDS_KEY, isChecked)
            FileManager.itemViewHolder.setShowCategoriesOfSounds(isChecked)
        }
    }

    private fun init(){
        val showPlayingSounds = FileManager.getBooleanValue(SHOW_PLAYING_SOUNDS_KEY, showPlayingSoundsDefault)
        val showCategoriesOfSounds = FileManager.getBooleanValue(SHOW_CATEGORIES_OF_SOUNDS_KEY, showCategoriesOfSoundsDefault)

        // Set the values of the switches
        settings_show_playing_sounds_switch.isChecked = showPlayingSounds
        settings_show_categories_of_sounds_switch.isChecked = showCategoriesOfSounds

        isInitialized = true
    }
}