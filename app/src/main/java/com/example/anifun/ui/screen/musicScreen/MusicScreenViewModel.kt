package com.example.anifun.ui.screen.musicScreen

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anifun.data.store.Music
import com.example.anifun.repository.Repository
import com.example.anifun.service.MusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 音乐屏幕ViewModel
 * @author yu
 * @date 2023/07/03
 * @constructor 创建[MusicScreenViewModel]
 */
class MusicScreenViewModel : ViewModel() {
    /** 音乐列表 */
    val musicList = mutableStateListOf<Music>()
    /** 播放进度 */
    val curProgress = mutableStateOf(0f)
    /** 时长 */
    var duration = 0L
    /** 播放进度字符串格式 */
    val curTime = mutableStateOf("00:00")
    /** 结束时间 */
    val endTime = mutableStateOf("00:00")
    /** 播放状态 */
    val isPlaying = mutableStateOf(false)
    /** 错误状态 */
    val isError = mutableStateOf(false)
    /** 是否滚动到下一个页面 */
    val isNeedToScrollToNextPage = mutableStateOf(false)
    /** 是否通过点击进入下一页 */
    val isClickToNextPage = mutableStateOf(false)
    /** 当前歌曲index */
    var curIndex by mutableStateOf(0)
    @SuppressLint("StaticFieldLeak")
    private var service: MusicService? = null
    var isFirstPlay: Boolean = true

    //初始化先获取三首
    init {
        getRandomMusic()
    }

    private fun getRandomMusic() {
        //协程
        viewModelScope.launch(Dispatchers.IO) {
            repeat(3) {
                val randomMusic = Repository.getRandomMusic()
                val musicMes = Repository.getMusicMes(
                    randomMusic.data.url.replace(
                        "http://music.163.com/song/media/outer/url?id=",
                        ""
                    )
                )
                randomMusic.data.url = musicMes.data[0].url
                musicList.add(randomMusic)
                //必须在主线程操作,不然报错
                withContext(Dispatchers.Main){
                    service?.addMusic(randomMusic.data.url)
                }
            }
        }
    }

    fun startMusic(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    fun reset() {
        curProgress.value = 0f
        duration = 0L
        curTime.value = "00:00"
        endTime.value = "00:00"
    }

    fun calDuration(long: Long) {
        val total = long / 1000
        val min = total / 60
        val sec = total - min * 60
        if (sec >= 10) {
            endTime.value = "0$min:$sec"
        } else {
            endTime.value = "0$min:0$sec"
        }

    }

    private fun calProcess(long: Long) {
        val min = long / 60
        val sec = long - min * 60
        if (sec >= 10) {
            curTime.value = "0$min:$sec"
        } else {
            curTime.value = "0$min:0$sec"
        }
        curProgress.value = (long.toFloat() / (duration / 1000).toFloat())
    }

    fun checkServiceIsNull() : Boolean{
        return service == null
    }


    fun pauseMusic() {
        isPlaying.value = false
        service?.pauseMusic()
    }

    fun seekMusic(value: Float) {
        curProgress.value = value
        service?.seek((duration * value).toLong())
    }

    fun playMusic() {
        isPlaying.value = true
        service?.playMusic()
        updateProcess()
    }

    fun checkHasNext():Boolean{
       return  service!!.checkHasNext()
    }

    fun nextMusic() {
        if (service == null) {
            return
        }
        isClickToNextPage.value = true
        reset()
        curIndex += 1
        service?.nextPlay()
        duration = service?.getDuration(musicList[curIndex].data.url)!!.toLong()
        calDuration(duration)
        if (service?.checkIsPlaying() != true) {
            playMusic()
        }
        viewModelScope.launch {
            getRandomMusic()
        }
    }


    fun preMusic() {
        if (service == null) {
            return
        }
        curIndex -= 1
        isClickToNextPage.value = true
        reset()
        service?.prePlay()
        duration = service?.getDuration(musicList[curIndex].data.url)!!.toLong()
        calDuration(duration)
        if (service?.checkIsPlaying() != true) {
            playMusic()
        }
    }

    fun updateProcess() {
        service?.updateProcess { position ->
            calProcess(position)
        }
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, IBinder: IBinder?) {
            val binder = IBinder as MusicService.LocalBinder
            service = binder.getService()
            for ((index, music) in musicList.withIndex()) {
                if (index == 0) {
                    service?.initPlayer(music.data.url,
                        onPlayErrorCallBack = { isError.value = true },
                        onPlayEnd = {
                            Log.e("isClickToNextPage.value", isClickToNextPage.value.toString())
                            if (!isClickToNextPage.value) {
                                curIndex += 1
                                isNeedToScrollToNextPage.value = true
                                duration =
                                    service?.getDuration(musicList[curIndex].data.url)!!.toLong()
                                calDuration(duration)
                            } else {
                                isClickToNextPage.value = false
                            }
                        })!!
                    duration = service?.getDuration(music.data.url)!!.toLong()
                    calDuration(duration)
                } else {
                    service?.addMusic(music.data.url)
                }
            }
            playMusic()
            Log.e("service", service.toString())
            Log.i("服务绑定成功", "服务绑定成功")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("服务失去连接", "服务失去连接")
        }
    }

}