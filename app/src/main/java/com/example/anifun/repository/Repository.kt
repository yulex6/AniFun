package com.example.anifun.repository

import com.example.anifun.data.RetrofitClient
import com.example.anifun.data.api.ApiService
import com.example.anifun.ui.screen.setting.SettingViewModel

/**
 * 存储库
 * @author yu
 * @date 2023/07/03
 */
object Repository {

    suspend fun getVideoList() =
        RetrofitClient.createApi(ApiService::class.java)
            .getVideoList()

    suspend fun getSearchVideoList(keyWord: String,itemStart: Int, pageSize: Int) =
        RetrofitClient.createApi(ApiService::class.java)
            .getSearchVideoList( cookie = SettingViewModel.Setting.curCookie,itemStart,pageSize,keyWord)

    suspend fun getRecommendVideoList(freshIdx1h: String,freshIdx: String,brush:String,lastShowList : String) =
        RetrofitClient.createApi(ApiService::class.java)
            .getRecommendVideoList(cookie = SettingViewModel.Setting.curCookie,fresh_idx_1h = freshIdx1h, fresh_idx = freshIdx, brush = brush, last_showlist = lastShowList)

    suspend fun getDynamicList(offset: String,uid: String) =
        RetrofitClient.createApi(ApiService::class.java)
            .getDynamic(cookie = SettingViewModel.Setting.curCookie,offset = offset, host_mid = uid)


    suspend fun getRandomMusic() =
        RetrofitClient.createApi(ApiService::class.java)
            .getRandomMusic()

    suspend fun getMusicMes(id: String) =
        RetrofitClient.createApi(ApiService::class.java)
            .getMusicMes(id)

    suspend fun getTimeLine() =
        RetrofitClient.createApi(ApiService::class.java)
            .getTimeLine()

    suspend fun sendToTXRobot(question:String) =
        RetrofitClient.createApi(ApiService::class.java)
            .sendToTXRobot(question= question)

    suspend fun sendToOwnThink(spoken:String) =
        RetrofitClient.createApi(ApiService::class.java)
            .sendToOwnThink(spoken = spoken)

    suspend fun getDm(cid: String,avid:String) =
        RetrofitClient.createApi(ApiService::class.java)
            .getDm(oid = cid, pid = avid)
}