package com.example.anifun.ui.screen.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.anifun.data.dataSource.RecommendVideoListDataSource
import com.example.anifun.data.dataSource.SearchVideoListDataSource
import com.example.anifun.data.dataSource.VideoListDataSource
import com.example.anifun.data.store.TimeLine
import com.example.anifun.repository.Repository


class HomeViewModel : ViewModel() {
    private val searchText = mutableStateOf("")
    val rankVideoItemList = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 20, // 第一次加载数量
            prefetchDistance = 2,
        )
    ) {
        VideoListDataSource(Repository)
    }.flow.cachedIn(viewModelScope)

    val videoItemList = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 20, // 第一次加载数量
            prefetchDistance = 2,
        )
    ) {
        RecommendVideoListDataSource(Repository)

    }.flow.cachedIn(viewModelScope)
    val searchVideoItemList = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 20, // 第一次加载数量
        )
    ) {
        SearchVideoListDataSource(Repository,searchText.value)
    }.flow.cachedIn(viewModelScope)

    suspend fun getAnimeSchedule(): TimeLine {
        return Repository.getTimeLine()
    }

    fun search(query: String){
        searchText.value  = query
    }


}