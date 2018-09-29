package app.dav.universalsoundboard.models

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import app.dav.universalsoundboard.services.BUNDLE_SOUNDS_KEY
import app.dav.universalsoundboard.services.CUSTOM_ACTION_PLAY
import app.dav.universalsoundboard.services.MediaPlaybackService
import java.util.*

class PlayingSound(val uuid: UUID,
                   val currentSound: Int,
                   val sounds: ArrayList<Sound>,
                   val repetitions: Int,
                   val randomly: Boolean,
                   val volume: Double) {

    var mediaBrowser: MediaBrowserCompat? = null
    var mediaController: MediaControllerCompat? = null
    var isPlaying = false

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

    fun playOrPause(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.StartPlaying)
        }else{
            // Connection was already established; continue playing the sound
            if(isPlaying){
                mediaController?.transportControls?.pause()
                isPlaying = false
            }else{
                mediaController?.transportControls?.play()
                isPlaying = true
            }
        }
    }

    private fun startPlaying(){
        val sound = sounds[currentSound]
        val bundle = Bundle()
        bundle.putStringArrayList(BUNDLE_SOUNDS_KEY, arrayListOf<String>(sound.uuid.toString()))
        mediaController?.transportControls?.sendCustomAction(CUSTOM_ACTION_PLAY, bundle)
    }

    fun stop(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.Stop)
        }else{
            mediaController?.transportControls?.stop()
            isPlaying = false
        }
    }

    fun skipPrevious(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.SkipPrevious)
        }else{
            mediaController?.transportControls?.skipToPrevious()
        }
    }

    fun skipNext(context: Context){
        if(mediaController == null){
            initMediaConnection(context, MediaAction.SkipNext)
        }else{
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