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
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import app.dav.universalsoundboard.MainActivity
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.PlayingSound
import app.dav.universalsoundboard.utilities.Utils
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


private const val ACTION_PLAY = "app.dav.universalsoundboard.ACTION_PLAY"
private const val ACTION_PAUSE = "app.dav.universalsoundboard.ACTION_PAUSE"
private const val ACTION_NEXT = "app.dav.universalsoundboard.ACTION_NEXT"
private const val ACTION_PREVIOUS = "app.dav.universalsoundboard.ACTION_PREVIOUS"
private const val ACTION_STOP = "app.dav.universalsoundboard.ACTION_STOP"
private const val NOTIFICATION_ID = 4123
private const val NOTIFICATION_CHANNEL_ID = "app.dav.universalsoundboard.PlaybackNotificationChannel"
const val CUSTOM_ACTION_PLAY = "play"
const val CUSTOM_ACTION_PAUSE = "pause"
const val CUSTOM_ACTION_NEXT = "next"
const val CUSTOM_ACTION_PREVIOUS = "previous"
const val CUSTOM_ACTION_STOP = "stop"
const val BUNDLE_UUID_KEY = "uuid"
private const val MEDIA_SESSION_TAG = "app.dav.universalsoundboard.MediaPlaybackService"

class MediaPlaybackService : MediaBrowserServiceCompat(){
    lateinit var mediaSession: MediaSessionCompat
    var players = HashMap<UUID, MediaPlayer>()
    var playingSounds = ArrayList<PlayingSound>()
    var notificationPlayingSoundUuid: UUID? = null

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
                val uuid = notificationPlayingSoundUuid ?: return
                play(uuid)
            }

            override fun onPause() {
                super.onPause()
                val uuid = notificationPlayingSoundUuid ?: return
                pause(uuid)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                val uuid = notificationPlayingSoundUuid ?: return
                skipToNext(uuid)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                val uuid = notificationPlayingSoundUuid ?: return
                skipToPrevious(uuid)
            }

            override fun onStop() {
                super.onStop()
                val uuid = notificationPlayingSoundUuid ?: return
                stop(uuid)
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)

                when(action){
                    CUSTOM_ACTION_PLAY -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            // Get the uuid and play the sound
                            val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY)) ?: return@launch
                            init(uuid)
                            play(uuid)
                        }
                    }
                    CUSTOM_ACTION_PAUSE -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            // Get the uuid and pause the sound
                            val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY)) ?: return@launch
                            init(uuid)
                            pause(uuid)
                        }
                    }
                    CUSTOM_ACTION_NEXT -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY)) ?: return@launch
                            init(uuid)
                            skipToNext(uuid)
                        }
                    }
                    CUSTOM_ACTION_PREVIOUS -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY)) ?: return@launch
                            init(uuid)
                            skipToPrevious(uuid)
                        }
                    }
                    CUSTOM_ACTION_STOP -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            val uuid = Utils.getUuidFromString(extras?.getString(BUNDLE_UUID_KEY)) ?: return@launch
                            init(uuid)
                            stop(uuid)
                        }
                    }
                }
            }
        })

        mediaSession.isActive = true
    }

    private suspend fun init(uuid: UUID){
        if(!players.containsKey(uuid)){
            // Get the PlayingSound and add it to the list
            val playingSound = FileManager.getPlayingSound(uuid)
            if(playingSound != null){
                // Create the MediaPlayer
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setOnCompletionListener {
                    skipToNext(uuid)
                }
                players[uuid] = mediaPlayer
                playingSounds.add(playingSound)
                prepare(uuid)
            }
        }
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

    private fun removePlayingSound(uuid: UUID){
        players[uuid]?.release()
        players.remove(uuid)
        val removedPlayingSound = playingSounds.find { p -> p.uuid == uuid }
        if(removedPlayingSound != null) playingSounds.remove(removedPlayingSound)
    }

    private fun sendNotification(uuid: UUID){
        val playingSound = playingSounds.find { p -> p.uuid == uuid } ?: return
        val sound = playingSound.sounds[playingSound.currentSound]
        val player = players[uuid] ?: return
        notificationPlayingSoundUuid = uuid

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

        if(playingSound.sounds.count() > 1 && playingSound.currentSound != 0){
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_skip_previous, getString(R.string.notification_action_previous), getPendingPreviousIntent()).build())
        }

        if(player.isPlaying){
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_pause, getString(R.string.notification_action_pause), getPendingPauseIntent()).build())
        }else{
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_play_arrow, getString(R.string.notification_action_play), getPendingPlayIntent()).build())
        }

        if(playingSound.sounds.count() > 1 && playingSound.currentSound != playingSound.sounds.count() - 1){
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_skip_next, getString(R.string.notification_action_next), getPendingNextIntent()).build())
        }

        builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_clear, getString(R.string.notification_action_stop), getPendingStopIntent()).build())

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    private fun removeNotification(){
        getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
        notificationPlayingSoundUuid = null
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
        val playingSound = playingSounds.find { p -> p.uuid == uuid } ?: return
        val player = players[uuid] ?: return

        if(playingSound.sounds.count() > playingSound.currentSound){
            val currentSound = playingSound.sounds[playingSound.currentSound]
            val currentSoundPath = currentSound.audioFile?.path ?: return

            player.reset()
            player.setDataSource(currentSoundPath)
            player.prepare()
            setMetadata(uuid)
            sendNotification(uuid)
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
        val playingSound = playingSounds.find { p -> p.uuid == uuid } ?: return
        val mediaPlayer = players[uuid] ?: return
        val wasPlaying = mediaPlayer.isPlaying

        // Play the next sound if there is one
        if((playingSound.currentSound + 1) >= playingSound.sounds.count()){
            stop(uuid)
            return
        }

        playingSound.currentSound++

        // Update the PlayingSound in the database
        GlobalScope.launch(Dispatchers.Main) {
            FileManager.setCurrentOfPlayingSound(uuid, playingSound.currentSound)

            // Play the sound
            prepare(uuid)
            if(wasPlaying) play(uuid)
        }
    }

    fun skipToPrevious(uuid: UUID){
        val playingSound = playingSounds.find { p -> p.uuid == uuid } ?: return
        val mediaPlayer = players[uuid] ?: return
        val wasPlaying = mediaPlayer.isPlaying

        // Play the previous sound if there is one
        if(playingSound.currentSound == 0) return    // TODO: Check for repetitions & randomly

        playingSound.currentSound--

        // Update the PlayingSound in the database
        GlobalScope.launch(Dispatchers.Main) {
            FileManager.setCurrentOfPlayingSound(uuid, playingSound.currentSound)

            // Play the sound
            prepare(uuid)
            if(wasPlaying) play(uuid)
        }
    }

    fun stop(uuid: UUID){
        val player = players[uuid] ?: return

        if(player.isPlaying) players[uuid]?.stop()
        players.remove(uuid)

        // Remote the notification
        removeNotification()
        setPlaybackState(uuid, PlaybackStateCompat.STATE_STOPPED)

        val playingSound = playingSounds.find { p -> p.uuid == uuid} ?: return
        playingSounds.remove(playingSound)
    }

    private fun setPlaybackState(uuid: UUID, state: Int){
        val bundle = Bundle()
        bundle.putString(BUNDLE_UUID_KEY, uuid.toString())

        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(state, players[uuid]?.currentPosition?.toLong() ?: 0, 1f)
                .setExtras(bundle)
                .build())
    }

    private fun setMetadata(uuid: UUID){
        mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, uuid.toString())
                .build())
    }
}