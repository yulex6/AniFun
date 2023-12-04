package com.example.anifun.ui.screen.search

import androidx.lifecycle.ViewModel
import com.example.anifun.data.RetrofitClient
import com.example.anifun.data.api.ApiService

class SearchScreenViewModel : ViewModel() {

    suspend fun getVideoCid(bvid: String) :String{
        val api : ApiService =  RetrofitClient.createApi(ApiService::class.java)
        val realVideoLink = api.getVideoMess(bvid)
        return realVideoLink.data.cid.toString()
    }

}