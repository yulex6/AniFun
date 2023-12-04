package com.example.anifun.ui.screen.playVideo

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.anifun.R
import com.example.anifun.databinding.ActivityPlayerBinding
import com.example.anifun.ui.screen.setting.SettingViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.*


class PlayVideoActivity : ComponentActivity() {
    var playerViewMode = PlayViewMode.HALF_SCREEN
    private var player: ExoPlayer? = null
    private var myPlayWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private lateinit var  playVideoViewModel : PlayVideoViewModel
    private  var bvid : String? = null
    private  var cid :String? = null
    private  lateinit var controller : WindowInsetsControllerCompat
    private lateinit var url : String
    private var autoPlay = true
    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityPlayerBinding.inflate(layoutInflater)
    }
    @androidx.annotation.OptIn(BuildCompat.PrereleaseSdkCheck::class)
    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        playVideoViewModel = PlayVideoViewModel()
        bvid =  intent.getStringExtra("bvid")
        cid =  intent.getStringExtra("cid")
        bvid?.let { Log.e("bvid", it) }
        cid?.let { Log.e("cid", it) }
        // 返回按钮
        val backExitBtn = this.findViewById<ImageView>(R.id.back_play)
        backExitBtn.setOnClickListener {
            if (isFullScreen()) {
                switchPlayerViewMode()
            } else {
                this.finish()
            }
        }
        controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val fullScreenBtn = this.findViewById<ImageView>(R.id.exo_fullscreen)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        fullScreenBtn.setOnClickListener {
            switchPlayerViewMode()
        }

        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (isFullScreen()) {
                    switchPlayerViewMode()
                } else {
                    this.finish()
                }
            }
        } else {
            onBackPressedDispatcher.addCallback(
                this, // lifecycle owner
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (isFullScreen()) {
                            switchPlayerViewMode()
                        } else {
                            finish()
                        }
                    }
                })
        }
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isMetered = cm.isActiveNetworkMetered
        val trafficPlayAuto = SettingViewModel.Setting.trafficPlayAuto.value
        if (isMetered && !trafficPlayAuto){
            autoPlay = false
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
                exoPlayer.playWhenReady = myPlayWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = autoPlay
            }
        val factory = DefaultHttpDataSource.Factory()
        val header  = mapOf("referer" to "https://www.bilibili.com/video/","User-Agent" to "PostmanRuntime/7.32.2")
        factory.setDefaultRequestProperties(header)
        GlobalScope.launch(Dispatchers.Main){
            val deferred: Deferred<String> = async {
                if (bvid != null && cid != null){
                    url =  playVideoViewModel.getRealVideoLink(bvid!!, cid!!)
                    Log.e("url",url)
                    return@async url
                }
                return@async ""
            }

            deferred.await()
            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(url))
            player!!.addMediaSource(mediaSource)


        }


    }

    public override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation ==  2){
            playerViewMode = PlayViewMode.FULL_SCREEN
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }else{
            playerViewMode = PlayViewMode.HALF_SCREEN
        }
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            myPlayWhenReady = this.playWhenReady
            release()
        }
        player = null
    }



    private fun switchPlayerViewMode() {
        Log.e("switch","switchPlayerViewMode")
        if (this.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //切换竖屏
            playerViewMode = PlayViewMode.HALF_SCREEN
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            //切换横屏
            playerViewMode = PlayViewMode.FULL_SCREEN
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun isFullScreen(): Boolean = playerViewMode == PlayViewMode.FULL_SCREEN
}
enum class PlayViewMode { HALF_SCREEN, FULL_SCREEN }