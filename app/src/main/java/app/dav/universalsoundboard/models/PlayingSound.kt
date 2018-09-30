package app.dav.universalsoundboard.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import app.dav.universalsoundboard.services.*
import app.dav.universalsoundboard.utilities.Utils
import java.util.*

class PlayingSound(val uuid: UUID,
                   val currentSound: Int,
                   val sounds: ArrayList<Sound>,
                   val repetitions: Int,
                   val randomly: Boolean,
                   val volume: Double) {

    var mediaBrowser: MediaBrowserCompat? = null
    var mediaController: MediaControllerCompat? = null
    private val isPlayingData = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean>
        get() = isPlayingData

    init {
        isPlayingData.value = false
    }

    constructor(context: Context,
                uuid: UUID,
                currentSound: Int,
                sounds: ArrayList<Sound>,
                repetitions: Int,
                randomly: Boolean,
                volume: Double) : this(uuid, currentSound, sounds, repetitions, randomly, volume){

        initMediaConnection(context, null)
    }

    private fun initMediaConnection(context: Context, action: MediaAction?){
        if(mediaBrowser == null){
            mediaBrowser = MediaBrowserCompat(context,
                    ComponentName(context, MediaPlaybackService::class.java),
                    object : MediaBrowserCompat.ConnectionCallback(){
                        override fun onConnected() {
                            super.onConnected()
                            val browser = mediaBrowser ?: return

                            mediaController = MediaControllerCompat(context, browser.sessionToken)

                            mediaController?.registerCallback(object : MediaControllerCompat.Callback() {
                                override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                                    super.onMetadataChanged(metadata)
                                }

                                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                                    super.onPlaybackStateChanged(state)

                                    val uuid = Utils.getUuidFromString(state?.extras?.getString(BUNDLE_UUID_KEY)) ?: return
                                    if(uuid != this@PlayingSound.uuid) return

                                    val playbackState = state?.playbackState as PlaybackState

                                    if(playbackState.state == PlaybackStateCompat.STATE_PLAYING){
                                        isPlayingData.value = true
                                    }else if(playbackState.state == PlaybackStateCompat.STATE_PAUSED ||
                                            playbackState.state == PlaybackStateCompat.STATE_STOPPED){
                                        isPlayingData.value = false
                                    }else{
                                        Log.d("onPlaybackChange", "Else: " + playbackState.state)
                                    }
                                }
                            })

                            browser.subscribe(browser.root, object : MediaBrowserCompat.SubscriptionCallback(){
                                override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                                    super.onChildrenLoaded(parentId, children)
                                }
                            })

                            when(action){
                                MediaAction.StartPlaying -> startPlaying()
                                //MediaAction.Pause -> pause(context)
                                MediaAction.Stop -> stop(context)
                                MediaAction.SkipPrevious -> skipPrevious(context)
                                MediaAction.SkipNext -> skipNext(context)
                            }
                        }

                        override fun onConnectionFailed() {
                            super.onConnectionFailed()
                        }

                        override fun onConnectionSuspended() {
                            super.onConnectionSuspended()
                        }
                    }, null)

            mediaBrowser?.connect()
        }
    }

    fun getCurrentSoundObject() : Sound{
        return sounds[currentSound]
    }

    fun getSkipPreviousButtonVisibility() : Int{
        return if(currentSound == 0){
            View.GONE
        }else{
            View.VISIBLE
        }
    }

    fun getSkipNextButtonVisibility() : Int{
        return if(sounds.count() > 1 && currentSound == sounds.count() - 1){
            View.GONE
        }else if(sounds.count() > 1){
            View.VISIBLE
        }else{
            View.GONE
        }
    }

    fun playOrPause(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.StartPlaying)
        }else{
            val bundle = Bundle()
            bundle.putString(BUNDLE_UUID_KEY, uuid.toString())

            // Connection was already established; continue playing the sound
            if(isPlayingData.value == true){
                mediaController?.transportControls?.sendCustomAction(CUSTOM_ACTION_PAUSE, bundle)
                isPlayingData.value = false
            }else{
                mediaController?.transportControls?.sendCustomAction(CUSTOM_ACTION_PLAY, bundle)
                isPlayingData.value = true
            }
        }
    }

    private fun startPlaying(){
        val soundsUuidList = ArrayList<String>()
        for(sound in sounds){
            soundsUuidList.add(sound.uuid.toString())
        }

        val bundle = Bundle()
        bundle.putStringArrayList(BUNDLE_SOUNDS_KEY, soundsUuidList)
        bundle.putString(BUNDLE_UUID_KEY, uuid.toString())
        bundle.putBoolean(BUNDLE_PLAY, true)
        mediaController?.transportControls?.sendCustomAction(CUSTOM_ACTION_INIT, bundle)
    }

    fun stop(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.Stop)
        }else{
            val bundle = Bundle()
            bundle.putString(BUNDLE_UUID_KEY, uuid.toString())
            mediaController?.transportControls?.sendCustomAction(CUSTOM_ACTION_STOP, bundle)
            isPlayingData.value = false
        }
    }

    fun skipPrevious(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.SkipPrevious)
        }else{
            val bundle = Bundle()
            bundle.putString(BUNDLE_UUID_KEY, uuid.toString())
            mediaController?.transportControls?.sendCustomAction(CUSTOM_ACTION_PREVIOUS, bundle)
        }
    }

    fun skipNext(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.SkipNext)
        }else{
            val bundle = Bundle()
            bundle.putString(BUNDLE_UUID_KEY, uuid.toString())
            mediaController?.transportControls?.sendCustomAction(CUSTOM_ACTION_NEXT, bundle)
            mediaController?.transportControls?.skipToNext()
        }
    }
}

enum class MediaAction{
    StartPlaying(),
    Stop(),
    SkipPrevious(),
    SkipNext()
}