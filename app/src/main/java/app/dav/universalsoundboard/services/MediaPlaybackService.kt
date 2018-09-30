package app.dav.universalsoundboard.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import app.dav.universalsoundboard.MainActivity
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Sound
import app.dav.universalsoundboard.utilities.Utils
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.HashMap


private const val ACTION_PLAY = "app.dav.universalsoundboard.ACTION_PLAY"
private const val ACTION_PAUSE = "app.dav.universalsoundboard.ACTION_PAUSE"
private const val ACTION_NEXT = "app.dav.universalsoundboard.ACTION_NEXT"
private const val ACTION_PREVIOUS = "app.dav.universalsoundboard.ACTION_PREVIOUS"
private const val ACTION_STOP = "app.dav.universalsoundboard.ACTION_STOP"
private const val NOTIFICATION_ID = 4123
private const val NOTIFICATION_CHANNEL_ID = "app.dav.universalsoundboard.PlaybackNotificationChannel"
const val CUSTOM_ACTION_INIT = "init"
const val CUSTOM_ACTION_PLAY = "play"
const val CUSTOM_ACTION_PAUSE = "pause"
const val CUSTOM_ACTION_NEXT = "next"
const val CUSTOM_ACTION_PREVIOUS = "previous"
const val CUSTOM_ACTION_STOP = "stop"
const val BUNDLE_UUID_KEY = "uuid"
const val BUNDLE_SOUNDS_KEY = "sounds"
const val BUNDLE_CURRENT_SOUND_KEY = "current_sound"
const val BUNDLE_PLAY = "play"
private const val MEDIA_SESSION_TAG = "app.dav.universalsoundboard.MediaPlaybackService"
const val METADATA_TITLE = "title"
const val METADATA_CATEGORY = "category"

class MediaPlaybackService : MediaBrowserServiceCompat(){
    lateinit var mediaSession: MediaSessionCompat
    var sounds = HashMap<UUID, ArrayList<Sound>>()
    var currentSounds = HashMap<UUID, Int>()
    var players = HashMap<UUID, MediaPlayer>()

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(this,
                MEDIA_SESSION_TAG,
                ComponentName(applicationContext, MediaPlaybackService::class.java),
                null)
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        sessionToken = mediaSession.sessionToken
        mediaSession.setCallback(object : MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()

                // Find the first mediaPlayer that is playing
                val uuid = getFirstPlayingSound() ?: return
                play(uuid)
            }

            override fun onPause() {
                super.onPause()
                val uuid = getFirstPlayingSound() ?: return
                pause(uuid)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                val uuid = getFirstPlayingSound() ?: return
                skipToNext(uuid)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                val uuid = getFirstPlayingSound() ?: return
                skipToPrevious(uuid)
            }

            override fun onStop() {
                super.onStop()
                val uuid = getFirstPlayingSound() ?: return
                stop(uuid)
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)

                when(action){
                    CUSTOM_ACTION_INIT -> {
                        // Get the sounds from the bundle
                        val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY))
                        val soundsList = extras?.getStringArrayList(BUNDLE_SOUNDS_KEY)
                        val currentSound = extras?.getInt(BUNDLE_CURRENT_SOUND_KEY) ?: 0
                        val play = extras?.getBoolean(BUNDLE_PLAY) ?: false

                        if(uuid != null && soundsList != null){
                            GlobalScope.launch(Dispatchers.Main) {
                                addPlayingSound(uuid, soundsList, currentSound)

                                if(play) play(uuid)
                            }
                        }
                    }
                    CUSTOM_ACTION_PLAY -> {
                        // Get the uuid and play the sound
                        val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY))
                        if(uuid != null) play(uuid)
                    }
                    CUSTOM_ACTION_PAUSE -> {
                        // Get the uuid and pause the sound
                        val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY))
                        if(uuid != null) pause(uuid)
                    }
                    CUSTOM_ACTION_NEXT -> {
                        val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY))
                        if(uuid != null) skipToNext(uuid)
                    }
                    CUSTOM_ACTION_PREVIOUS -> {
                        val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY))
                        if(uuid != null) skipToPrevious(uuid)
                    }
                    CUSTOM_ACTION_STOP -> {
                        val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY))
                        if(uuid != null) stop(uuid)
                    }
                }
            }
        })

        mediaSession.isActive = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when(action){
            ACTION_PLAY -> mediaSession.controller.transportControls.play()
            ACTION_PAUSE -> mediaSession.controller.transportControls.pause()
            ACTION_NEXT -> mediaSession.controller.transportControls.skipToNext()
            ACTION_PREVIOUS -> mediaSession.controller.transportControls.skipToPrevious()
            ACTION_STOP -> mediaSession.controller.transportControls.stop()
            else -> MediaButtonReceiver.handleIntent(mediaSession, intent)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Release all MediaPlayers
        for(player in players){
            player.value.release()
        }

        mediaSession.isActive = false
        removeNotification()
    }

    private suspend fun addPlayingSound(uuid: UUID, soundsList: ArrayList<String>, currentSound: Int){
        // Create a new MediaPlayer
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            skipToNext(uuid)
        }
        players[uuid] = mediaPlayer

        // Create new List with the sounds
        updateSoundsList(uuid, soundsList)

        // Create new currentSound
        currentSounds[uuid] = currentSound

        prepare(uuid)
    }

    private fun removePlayingSound(uuid: UUID){
        players[uuid]?.release()
        players.remove(uuid)
        sounds.remove(uuid)
        currentSounds.remove(uuid)
    }

    private suspend fun updateSoundsList(uuid: UUID, soundsList: ArrayList<String>){
        sounds[uuid] = ArrayList()
        sounds[uuid]?.clear()

        for(soundUuidString in soundsList){
            val soundUuid = Utils.getUuidFromString(soundUuidString) ?: continue
            val sound = FileManager.getSound(soundUuid) ?: continue
            sounds[uuid]?.add(sound)
        }
    }

    private fun sendNotification(uuid: UUID){
        val soundsList = sounds[uuid] ?: return
        val currentSound = currentSounds[uuid] ?: return
        val sound = soundsList[currentSound]
        val player = players[uuid] ?: return

        val pendingMainActivityIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channelId = NOTIFICATION_CHANNEL_ID
        val name = getString(R.string.notification_channel_name)

        // Build the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance);
            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
        if(sound.category != null) builder.setContentText(sound.category?.name)

        builder
                .setContentTitle(sound.name)
                .setSmallIcon(R.drawable.ic_usb_logo)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.music_note))
                .setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setColor(getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingMainActivityIntent)
                .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(mediaSession.sessionToken))

        if(sound.image != null)
            builder.setLargeIcon(sound.image)
        else
            builder.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.music_note))

        if(soundsList.count() > 1 && currentSound != 0){
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_skip_previous, getString(R.string.notification_action_previous), getPendingPreviousIntent()).build())
        }

        if(player.isPlaying){
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_pause, getString(R.string.notification_action_pause), getPendingPauseIntent()).build())
        }else{
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_play_arrow, getString(R.string.notification_action_play), getPendingPlayIntent()).build())
        }

        if(soundsList.count() > 1 && currentSound != soundsList.count() - 1){
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_skip_next, getString(R.string.notification_action_next), getPendingNextIntent()).build())
        }

        builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_clear, getString(R.string.notification_action_stop), getPendingStopIntent()).build())

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    private fun removeNotification(){
        getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }

    private fun getPendingPlayIntent() : PendingIntent{
        val playIntent = Intent(this, MediaPlaybackService::class.java)
        playIntent.action = ACTION_PLAY
        return PendingIntent.getService(this, 0, playIntent, 0)
    }

    private fun getPendingPauseIntent() : PendingIntent{
        val pauseIntent = Intent(this, MediaPlaybackService::class.java)
        pauseIntent.action = ACTION_PAUSE
        return PendingIntent.getService(this, 0, pauseIntent, 0)
    }

    private fun getPendingNextIntent() : PendingIntent{
        val nextIntent = Intent(this, MediaPlaybackService::class.java)
        nextIntent.action = ACTION_NEXT
        return PendingIntent.getService(this, 0, nextIntent, 0)
    }

    private fun getPendingPreviousIntent() : PendingIntent{
        val previousIntent = Intent(this, MediaPlaybackService::class.java)
        previousIntent.action = ACTION_PREVIOUS
        return PendingIntent.getService(this, 0, previousIntent, 0)
    }

    private fun getPendingStopIntent() : PendingIntent{
        val stopIntent = Intent(this, MediaPlaybackService::class.java)
        stopIntent.action = ACTION_STOP
        return PendingIntent.getService(this, 0, stopIntent, 0)
    }

    private fun prepare(uuid: UUID){
        val soundsList = sounds[uuid] ?: return
        val currentSoundInt = currentSounds[uuid] ?: return
        val player = players[uuid] ?: return

        if(soundsList.count() > currentSoundInt){
            val currentSound = soundsList[currentSoundInt]
            val currentSoundPath = currentSound.audioFile?.path

            if(currentSoundPath != null){
                player.reset()
                player.setDataSource(currentSoundPath)
                player.prepare()
            }
        }
    }

    fun play(uuid: UUID){
        val player = players[uuid] ?: return

        if(!player.isPlaying){
            players[uuid]?.start()
            sendNotification(uuid)
            setPlaybackState(uuid, PlaybackStateCompat.STATE_PLAYING)
        }
    }

    fun pause(uuid: UUID){
        val player = players[uuid] ?: return

        if(player.isPlaying){
            players[uuid]?.pause()
            sendNotification(uuid)
            setPlaybackState(uuid, PlaybackStateCompat.STATE_PAUSED)
        }
    }

    fun skipToNext(uuid: UUID){
        val soundsList = sounds[uuid] ?: return
        val currentSound = currentSounds[uuid] ?: return

        // Play the next sound if there is one
        currentSounds[uuid] = currentSound + 1
        if(soundsList.count() > currentSound){
            // Play the sound
            prepare(uuid)
            play(uuid)
        }else{
            stop(uuid)
        }
    }

    fun skipToPrevious(uuid: UUID){
        val currentSound = currentSounds[uuid] ?: return

        // Play the previous sound if there is one
        if(currentSound == 0) return

        currentSounds[uuid] = currentSound - 1

        // Play the sound
        prepare(uuid)
        play(uuid)
    }

    fun stop(uuid: UUID){
        val player = players[uuid] ?: return

        if(player.isPlaying) players[uuid]?.stop()

        // Remote the notification
        removeNotification()
        mediaSession.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_STOPPED, player.currentPosition.toLong(), 1f).build())
        currentSounds[uuid] = 0
        sounds[uuid]?.clear()
    }

    private fun setPlaybackState(uuid: UUID, state: Int){
        val bundle = Bundle()
        bundle.putString(BUNDLE_UUID_KEY, uuid.toString())

        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(state, players[uuid]?.currentPosition?.toLong() ?: 0, 1f)
                .setExtras(bundle)
                .build())
    }

    private fun getFirstPlayingSound() : UUID?{
        for(player in players){
            if(player.value.isPlaying) return player.key
        }
        return null
    }
}