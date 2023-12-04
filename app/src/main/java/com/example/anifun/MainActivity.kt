package com.example.anifun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.anifun.data.AppDatabase
import com.example.anifun.service.MusicService
import com.example.anifun.ui.screen.MainViewModel
import com.example.anifun.ui.screen.MainPage
import com.example.anifun.ui.screen.setting.SettingViewModel
import com.example.anifun.ui.theme.AniFunTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : ComponentActivity() {
    private val mailScope = MainScope()
    private val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        MainViewModel.initDb(AppDatabase.getDatabase(this))
        mailScope.launch {
            SettingViewModel.Setting.curCookie = SettingViewModel.Setting.readCookie(context)
            Log.e("cookie",SettingViewModel.Setting.curCookie)
        }
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            //使用使用IjkPlayer解码
            .setPlayerFactory(IjkPlayerFactory.create())
            .build());
        // 1. 设置状态栏沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AniFunTheme {
                // 加入ProvideWindowInsets
                ProvideWindowInsets {
                    // 2. 设置状态栏颜色
                    rememberSystemUiController().setStatusBarColor(
                        Color.Transparent, darkIcons = MaterialTheme.colors.isLight)
                    Column {
                        MainPage()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        val intent = Intent(this, MusicService::class.java)
        stopService(intent)
        super.onDestroy()
    }
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AniFunTheme {
        MainPage()
    }
}
