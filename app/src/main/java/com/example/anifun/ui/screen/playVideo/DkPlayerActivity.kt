package com.example.anifun.ui.screen.playVideo

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import com.example.anifun.R
import com.example.anifun.data.proto.Elem
import com.example.anifun.ui.screen.setting.SettingViewModel
import kotlinx.coroutines.*
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.*
import xyz.doikki.videoplayer.player.BaseVideoView.SimpleOnStateChangeListener
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager


class DkPlayerActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private var mMyDanmakuView: MyDanmakuView? = null
    private var mController: StandardVideoController? = null
    private var mTitleView: TitleView? = null
    private var bvid: String? = null
    private var cid: String? = null
    private var avid: String? = null
    private var title: String? = null
    private lateinit var playVideoViewModel: PlayVideoViewModel
    private lateinit var url: String
    private var autoPlay = true
    private  lateinit var controller : WindowInsetsControllerCompat

    object CurDm {
        var dmList: MutableLiveData<List<Elem>> = MutableLiveData<List<Elem>>()
    }

    private var dmIndex = 0
    private val mHandler: Handler = Handler(Looper.myLooper()!!)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dk_player)
        controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        playVideoViewModel = PlayVideoViewModel()
        title = intent.getStringExtra("title")
        bvid = intent.getStringExtra("bvid")
        avid = intent.getStringExtra("avid")
        cid = intent.getStringExtra("cid")
        videoView = findViewById(R.id.player)
        mMyDanmakuView = MyDanmakuView(this)
        //检测网络状态
        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isMetered = cm.isActiveNetworkMetered
        val trafficPlayAuto = SettingViewModel.Setting.trafficPlayAuto.value
        if (isMetered && !trafficPlayAuto) {
            autoPlay = false
        }
        //添加返回键回调，判断是退出全屏还是直接退出
        onBackPressedDispatcher.addCallback(
            this, // lifecycle owner
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (videoView.isFullScreen) {
                        videoView.stopFullScreen()
                        this@DkPlayerActivity.requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    } else {
                        finish()
                    }
                }
            })
        //观察弹幕列表是否为空，不为空则添加监听事件开始输出弹幕
        CurDm.dmList.observe(this) {
            videoView.addOnStateChangeListener(listener)
        }
        init()

    }
    private val listener=  object :SimpleOnStateChangeListener() {
        override fun onPlayStateChanged(playState: Int) {
            if (playState == VideoView.STATE_PREPARED) {
                simulateDanmu(CurDm.dmList.value!!)
            } else if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                mHandler.removeCallbacksAndMessages(null)
            }
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        //监听播放完毕退出小窗模式
        videoView.setOnStateChangeListener(object : SimpleOnStateChangeListener() {
            override fun onPlayStateChanged(playState: Int) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    if (videoView.isTinyScreen) {
                        videoView.stopTinyScreen()
                        releaseVideoView()
                    }
                }
            }
        })
        //获取控制器
        mController = StandardVideoController(this)
        mController!!.addDefaultControlComponent(title, false)
        //添加小窗组件
//        addControlComponent()
        //添加弹幕组件
        mController!!.addControlComponent(mMyDanmakuView)
        videoView.setVideoController(mController) //设置控制器
        GlobalScope.launch(Dispatchers.Main) {
            val deferred: Deferred<String> = async {
                if (bvid != null && cid != null) {
                    url = playVideoViewModel.getRealVideoLink(bvid!!, cid!!)
                    return@async url
                }
                return@async ""
            }
            deferred.await()
            val header = mapOf(
                "referer" to "https://www.bilibili.com/video/",
                "User-Agent" to "PostmanRuntime/7.32.2"
            )
            videoView.setUrl(url, header) //设置视频地址
            VideoViewManager.instance().setPlayOnMobileNetwork(autoPlay)
            mController!!.showNetWarning()
            videoView.start()
            //获取弹幕
            playVideoViewModel.getDm(cid = cid!!, avid = avid!!)
        }
    }


    //发送弹幕
    private fun simulateDanmu(elems: List<Elem>) {
        dmIndex = 0
        mHandler.post(object : java.lang.Runnable {
            override fun run() {
                if (dmIndex >= elems.size) {
                    mHandler.removeCallbacksAndMessages(null)
                    dmIndex = 0
                }else{
                    if (videoView.currentPosition >= elems[dmIndex].progress) {
                        mMyDanmakuView!!.addDanmaku(elems[dmIndex].content, elems[dmIndex].color,elems[dmIndex].fontsize, false)
                        dmIndex++
                    }
                    mHandler.postDelayed(this, 100)
                }
            }
        })
    }

    fun showDanMu() {
        mMyDanmakuView!!.show()
    }

    fun hideDanMu() {
        mMyDanmakuView!!.hide()
    }

    fun addDanmakuWithDrawable() {
        mMyDanmakuView!!.addDanmakuWithDrawable()
    }

    fun addDanmaku() {
        mMyDanmakuView!!.addDanmaku("这是一条文字弹幕~", android.graphics.Color.WHITE.toLong(),12, true)
    }

    private fun addControlComponent() {
        val completeView = CompleteView(this)
        val errorView = ErrorView(this)
        mTitleView = TitleView(this)
        mController!!.addControlComponent(completeView, errorView, mTitleView)
        mController!!.addControlComponent(VodControlView(this))
        mController!!.addControlComponent(GestureView(this))
    }

    private fun releaseVideoView() {
        videoView.release()
        if (videoView.isFullScreen) {
            videoView.stopFullScreen()
        }
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        videoView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()
        CurDm.dmList.value = emptyList()
        mHandler.removeCallbacksAndMessages(null)
        videoView.removeOnStateChangeListener(listener)
    }


}