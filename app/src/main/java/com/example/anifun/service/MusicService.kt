package com.example.anifun.service

import android.app.*
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.anifun.MainActivity
import com.example.anifun.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.upstream.DefaultAllocator
import kotlinx.coroutines.*


class MusicService : Service() {
    var channelId = "myChannelId"
    var channelName = "myChannelName"
    var description = "this is myChannel's description"
    var curMusicName = ""
    var curArtiName = ""
    private var notificationManager: NotificationManager? = null
    private val binder: IBinder = LocalBinder()
    private val ONGOING_NOTIFICATION_ID = 666
    private var player: ExoPlayer? = null
    private var videoWatchedTime: Long = 0
    inner class LocalBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    fun initPlayer(url: String, onPlayErrorCallBack: () -> Unit, onPlayEnd: () -> Unit) {
                val mediaItem = MediaItem.fromUri(url)
                player?.setMediaItem(mediaItem)
                player?.addListener(playbackStateListener(onPlayErrorCallBack, onPlayEnd))
    }


    fun addMusic(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player?.addMediaItem(mediaItem)
    }

    fun playMusic() {
        player?.play()
    }

    fun pauseMusic() {
        player?.pause()

    }

    fun checkHasNext() :Boolean{
      return  player!!.hasNextMediaItem()
    }
    fun prePlay() {
        player?.seekToPreviousMediaItem()
    }

    fun nextPlay() {
        player?.seekToNextMediaItem()
    }

    fun seek(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    fun checkIsPlaying(): Boolean {
        return player!!.isPlaying
    }


    fun getDuration(path: String): String? {
        val retriever = MediaMetadataRetriever();
        //2.设置音视频资源路径
        retriever.setDataSource(path, HashMap())
        //3.获取音视频资源总时长
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        curMusicName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()
        curArtiName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString()
        retriever.release()
        return time
    }


    //创建通知需要创建通道
    private fun createNotificationChannel() {
        //Android8.0(API26)以上需要调用下列方法，但低版本由于支持库旧，不支持调用
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = description
        channel.setSound(null, null);
        channel.setShowBadge(true)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.createNotificationChannel(channel)
    }

    //通知
    fun sendNotification(): Notification {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        return Notification.Builder(this, channelId)
            .setContentTitle(getText(R.string.notification_title))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(null)
            .build()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = sendNotification()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        val defaultLoadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setTargetBufferBytes(C.LENGTH_UNSET)
            .setBufferDurationsMs(10000, 120000, 1000, 1000)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        val defaultRenderersFactory = DefaultRenderersFactory(this)
        player = ExoPlayer.Builder(this, defaultRenderersFactory)
            .setLoadControl(defaultLoadControl)
            .build()
            .also { exoPlayer ->
                exoPlayer.playWhenReady = true
                exoPlayer.prepare()
            }
        super.onCreate()

    }

    private fun playbackStateListener(onPlayErrorCallBack: () -> Unit, onPlayEnd: () -> Unit) =
        object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("error", error.toString())
                onPlayErrorCallBack()
                super.onPlayerError(error)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Log.e("onMediaItemTransition", "onMediaItemTransition")
                onPlayEnd()
                super.onMediaItemTransition(mediaItem, reason)
            }
        }


    /**
     * 更新进度条
     * @param [callback] 回调
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun updateProcess(callback: (position: Long) -> Unit) {
        // 在IO线程中执行耗时操作
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                videoWatchedTime = player!!.currentPosition / 1000
                callback(videoWatchedTime)
                delay(1000)
                updateProcess(callback)
            }
        }

    }
}