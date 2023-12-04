package com.example.anifun.ui.screen.playVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anifun.data.RetrofitClient
import com.example.anifun.data.api.ApiService
import com.example.anifun.data.kotlin.Dm
import com.example.anifun.data.proto.DmRes
import com.example.anifun.repository.Repository
import com.google.gson.Gson
import com.google.protobuf.util.JsonFormat
import kotlinx.coroutines.launch


class PlayVideoViewModel : ViewModel() {
    val api : ApiService =  RetrofitClient.createApi(ApiService::class.java)

     suspend fun getRealVideoLink(bvid: String, cid : String) :String{
        val realVideoLink = api.getRealVideoLink(bvid, cid)
        return   realVideoLink.data.durl[0].url
    }

    fun getDm(cid: String,avid:String){
        viewModelScope.launch {
            val responseBody = Repository.getDm(cid=cid, avid = avid)
            val parseFrom = Dm.DmSegMobileReply.parseFrom(responseBody.bytes())
            val json = JsonFormat.printer().print(parseFrom)
            val dm = Gson().fromJson(json, DmRes::class.java)
            if (dm.elems == null) return@launch
            if (dm.elems.isNotEmpty()){
                dm.elems = dm.elems.sortedBy { it.progress }
                DkPlayerActivity.CurDm.dmList.value = dm.elems
                DkPlayerActivity.CurDm.dmList.value!!.forEach(::println)
            }
        }
    }

}
