package app.dav.universalsoundboard.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Sound
import app.dav.universalsoundboard.services.BUNDLE_SOUNDS_KEY
import app.dav.universalsoundboard.services.CUSTOM_ACTION_PLAY
import app.dav.universalsoundboard.services.MediaPlaybackService
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
    lateinit var mediaBrowser: MediaBrowserCompat
    //lateinit var mediaController: MediaControllerCompat

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

        mediaBrowser = MediaBrowserCompat(context,
                ComponentName(context!!, MediaPlaybackService::class.java),
                object : MediaBrowserCompat.ConnectionCallback(){
                    override fun onConnected() {
                        super.onConnected()

                        FileManager.itemViewHolder.mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)

                        FileManager.itemViewHolder.mediaController.registerCallback(object : MediaControllerCompat.Callback() {
                            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                                super.onMetadataChanged(metadata)
                            }

                            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                                super.onPlaybackStateChanged(state)
                            }
                        })

                        mediaBrowser.subscribe(mediaBrowser.root, object : MediaBrowserCompat.SubscriptionCallback(){
                            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                                super.onChildrenLoaded(parentId, children)
                            }
                        })
                    }

                    override fun onConnectionFailed() {
                        super.onConnectionFailed()
                    }

                    override fun onConnectionSuspended() {
                        super.onConnectionSuspended()
                    }
                }, null)

        mediaBrowser.connect()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaBrowser.disconnect()
    }

    override fun onItemClicked(sound: Sound) {
        val bundle = Bundle()
        bundle.putStringArrayList(BUNDLE_SOUNDS_KEY, arrayListOf<String>(sound.uuid.toString()))
        FileManager.itemViewHolder.mediaController.transportControls.sendCustomAction(CUSTOM_ACTION_PLAY, bundle)
    }

    override fun onItemLongClicked(sound: Sound, item: View) {
        selectedSound = sound

        val menu = PopupMenu(context!!, item)
        menu.inflate(R.menu.sound_item_context_menu)
        menu.show()

        menu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.sound_item_context_menu_change_image -> changeSoundImage()
                R.id.sound_item_context_menu_rename -> renameSound(sound)
                R.id.sound_item_context_menu_delete -> deleteSound(sound)
            }
            true
        }
    }

    private fun changeSoundImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/png"
        startActivityForResult(intent, REQUEST_IMAGE_FILE_GET)
    }

    private fun renameSound(sound: Sound){
        val fragmentManager = activity?.fragmentManager
        if(fragmentManager != null){
            val fragment = RenameSoundDialogFragment()
            fragment.sound = sound
            fragment.show(fragmentManager, "rename_sound")
        }
    }

    private fun deleteSound(sound: Sound){
        val fragmentManager = activity?.fragmentManager
        if(fragmentManager != null){
            val fragment = DeleteSoundDialogFragment()
            fragment.sound = sound
            fragment.show(fragmentManager, "delete_sound")
        }
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
