package com.example.anifun.ui.screen.photo

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cc.shinichi.library.ImagePreview
import com.example.anifun.data.dataSource.DynamicListDataSource
import com.example.anifun.repository.Repository

class PhotoScreenViewModel : ViewModel() {
    private val uid = mutableStateOf("")
    private val searchFlag = mutableStateOf(false)
    val dynamicItemList = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 20, // 第一次加载数量
            prefetchDistance = 2,
        )
    ) {
        DynamicListDataSource(Repository,uid.value)
    }.flow.cachedIn(viewModelScope)

    fun search(query: String){
        uid.value  = query
    }
    fun setFlag(boolean: Boolean){
        searchFlag.value =boolean
    }
    fun getFlag():Boolean{
        return searchFlag.value
    }

    fun openPreView(context: Context , url: String){
        ImagePreview.instance.setContext(context).setIndex(0).setImage(url).start();
    }
}