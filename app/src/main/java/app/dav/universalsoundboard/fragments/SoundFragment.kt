package app.dav.universalsoundboard.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.data.FileManager.PLAY_ONE_SOUND_AT_ONCE_KEY
import app.dav.universalsoundboard.models.Sound
import app.dav.universalsoundboard.viewmodels.SoundViewModel
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch

/**
 * A fragment representing a list of Items.
 */
const val REQUEST_IMAGE_FILE_GET = 2

class SoundFragment :
        Fragment(),
        SoundListAdapter.OnItemClickListener,
        SoundListAdapter.OnItemLongClickListener {

    private var columnCount = 1
    private lateinit var viewModel: SoundViewModel
    private var selectedSound: Sound? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SoundViewModel::class.java)
        viewModel.soundListAdapter = SoundListAdapter(this, this)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        GlobalScope.launch(Dispatchers.Main) {
            FileManager.itemViewHolder.loadSounds()
        }
        FileManager.itemViewHolder.sounds.observe(this, Observer {
            if(it != null) viewModel.soundListAdapter?.submitList(it)
            viewModel.soundListAdapter?.notifyDataSetChanged()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sound, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = viewModel.soundListAdapter
            }
        }

        return view
    }

    override fun onItemClicked(sound: Sound) {
        GlobalScope.launch(Dispatchers.Main) {
            // If playOneSoundAtOnce, remove all playing sounds first
            if(FileManager.getBooleanValue(PLAY_ONE_SOUND_AT_ONCE_KEY, FileManager.playOneSoundAtOnce)){
                val playingSounds = FileManager.itemViewHolder.playingSounds.value
                if(playingSounds != null){
                    for (p in playingSounds){
                        p.stop(context!!)
                    }
                }

                FileManager.deleteAllPlayingSounds()

                // Wait for a short amount of time
                val handler = Handler()
                handler.postDelayed({
                    GlobalScope.launch(Dispatchers.Main) {
                        FileManager.addPlayingSound(null, arrayListOf(sound), 0, 0, false, 1.0)
                        FileManager.itemViewHolder.playingSounds.value?.last()?.playOrPause(context!!)
                    }
                }, 100)
            }else{
                FileManager.addPlayingSound(null, arrayListOf(sound), 0, 0, false, 1.0)
                FileManager.itemViewHolder.playingSounds.value?.last()?.playOrPause(context!!)
            }
        }
    }

    override fun onItemLongClicked(sound: Sound, item: View) {
        selectedSound = sound

        val dialog = BottomSheetDialog(context!!)
        val layout = layoutInflater.inflate(R.layout.fragment_sound_item_menu_dialog, null)

        // Set the click listeners for the menu items
        layout.findViewById<TextView>(R.id.sound_item_menu_dialog_set_category_item).setOnClickListener {
            dialog.dismiss()
            showSetCategoryDialog(sound)
        }

        layout.findViewById<TextView>(R.id.sound_item_menu_dialog_change_image_item).setOnClickListener {
            dialog.dismiss()
            changeSoundImage()
        }

        layout.findViewById<TextView>(R.id.sound_item_menu_dialog_rename_item).setOnClickListener {
            dialog.dismiss()
            renameSound(sound)
        }

        layout.findViewById<TextView>(R.id.sound_item_menu_dialog_delete_item).setOnClickListener {
            dialog.dismiss()
            deleteSound(sound)
        }

        dialog.setContentView(layout)
        dialog.show()
    }

    private fun showSetCategoryDialog(sound: Sound){
        val fragmentManager = activity?.supportFragmentManager ?: return
        val fragment = SetCategoryDialogFragment()
        fragment.sound = sound
        fragment.show(fragmentManager, "set_category")
    }

    private fun changeSoundImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_FILE_GET)
    }

    private fun renameSound(sound: Sound){
        val fragmentManager = activity?.supportFragmentManager ?: return
        val fragment = RenameSoundDialogFragment()
        fragment.sound = sound
        fragment.show(fragmentManager, "rename_sound")
    }

    private fun deleteSound(sound: Sound){
        val fragmentManager = activity?.supportFragmentManager ?: return
        val fragment = DeleteSoundDialogFragment()
        fragment.sound = sound
        fragment.show(fragmentManager, "delete_sound")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_FILE_GET && resultCode == Activity.RESULT_OK){
            val fileUri = data?.data

            val contentResolver = activity?.contentResolver
            val cacheDir = activity?.cacheDir
            if(fileUri != null && contentResolver != null && cacheDir != null) viewModel.changeSoundImage(fileUri, contentResolver, selectedSound!!, cacheDir)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
                SoundFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
