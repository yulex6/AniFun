package com.example.anifun.ui.screen.setting

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import com.example.anifun.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class SettingViewModel : ViewModel() {

    //流量网络下是否自动播放
    private val trafficPlaybackChecked = booleanPreferencesKey("trafficPlaybackChecked")


    object Setting{
        private val cookie = stringPreferencesKey("cookie")
        val trafficPlayAuto = mutableStateOf(false)
        var curCookie by mutableStateOf("")

        suspend fun readCookie(context: Context):String {
            val exampleCounterFlow: String = context.dataStore.data.map { preferences ->
                preferences[cookie] ?: ""
            }.first()
            return exampleCounterFlow
        }
        suspend fun writeCookie(context: Context, newCookie:String) {
            context.dataStore.edit { settings ->
                settings[cookie] = newCookie
            }
        }
    }

     fun readTrafficPlaybackChecked(context: Context):Flow<Boolean> {
        val exampleCounterFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
                preferences[trafficPlaybackChecked] ?: false
            }
        return exampleCounterFlow
    }
    suspend fun writeTrafficPlaybackChecked(context: Context, boolean: Boolean) {
        context.dataStore.edit { settings ->
            settings[trafficPlaybackChecked] = boolean
        }
    }



    fun setTrafficPlayAuto(boolean: Boolean) {
        Setting.trafficPlayAuto.value = boolean
    }

}