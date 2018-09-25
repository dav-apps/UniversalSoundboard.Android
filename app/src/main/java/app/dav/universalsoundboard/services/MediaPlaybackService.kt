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
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.util.*


private const val ACTION_PLAY = "app.dav.universalsoundboard.ACTION_PLAY"
private const val ACTION_PAUSE = "app.dav.universalsoundboard.ACTION_PAUSE"
private const val ACTION_NEXT = "app.dav.universalsoundboard.ACTION_NEXT"
private const val ACTION_PREVIOUS = "app.dav.universalsoundboard.ACTION_PREVIOUS"
private const val ACTION_STOP = "app.dav.universalsoundboard.ACTION_STOP"
private const val NOTIFICATION_ID = 4123
private const val NOTIFICATION_CHANNEL_ID = "app.dav.universalsoundboard.PlaybackNotificationChannel"
const val CUSTOM_ACTION_PLAY = "play"
const val BUNDLE_SOUNDS_KEY = "sounds"
private const val MEDIA_SESSION_TAG = "app.dav.universalsoundboard.MediaPlaybackService"

class MediaPlaybackService : MediaBrowserServiceCompat(){
    lateinit var mediaSession: MediaSessionCompat
    val player = MediaPlayer()
    var soundsList = ArrayList<Sound>()
    var currentSound = 0

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
                play()
            }

            override fun onPause() {
                super.onPause()
                pause()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                playNext()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                playPrevious()
            }

            override fun onStop() {
                super.onStop()
                stop()
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)

                if(action == CUSTOM_ACTION_PLAY){
                    val soundsList = extras?.getStringArrayList(BUNDLE_SOUNDS_KEY)
                    GlobalScope.launch(Dispatchers.Main) {
                        if(soundsList != null) updateSoundsList(soundsList)
                        prepare()
                        play()
                    }
                }
            }
        })

        player.setOnCompletionListener {
            playNext()
        }

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
        player.release()
        mediaSession.isActive = false
        removeNotification()
    }

    private suspend fun updateSoundsList(soundsList: ArrayList<String>){
        this.soundsList.clear()

        for(soundUuidString in soundsList){
            val soundUuid = UUID.fromString(soundUuidString) ?: continue
            val sound = FileManager.getSound(soundUuid) ?: continue
            this.soundsList.add(sound)
        }
    }

    private fun sendNotification(){
        val sound = soundsList[currentSound]

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

    private fun prepare(){
        if(soundsList.count() > currentSound){
            val currentSound = soundsList[currentSound]
            val currentSoundPath = currentSound.audioFile?.path

            if(currentSoundPath != null){
                player.reset()
                player.setDataSource(currentSoundPath)
                player.prepare()
            }
        }
    }

    fun play(){
        if(!player.isPlaying){
            player.start()
            sendNotification()
        }
    }

    fun pause(){
        if(player.isPlaying){
            player.pause()
            sendNotification()
        }
    }

    fun playNext(){
        // Play the next sound if there is one
        currentSound++
        if(soundsList.count() > currentSound){
            // Play the sound
            prepare()
            play()
        }else{
            stop()
        }
    }

    fun playPrevious(){
        // Play the previous sound if there is one
        if(currentSound == 0) return

        currentSound--

        // Play the sound
        prepare()
        play()
    }

    fun stop(){
        if(player.isPlaying) player.stop()

        // Remote the notification
        removeNotification()
        currentSound = 0
        soundsList.clear()
    }
}