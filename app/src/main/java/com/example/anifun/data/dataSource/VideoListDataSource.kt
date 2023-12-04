package com.example.anifun.data.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anifun.data.store.ListElement
import com.example.anifun.repository.Repository

class VideoListDataSource(private val repository: Repository) : PagingSource<Int, ListElement>() {


    override fun getRefreshKey(state: PagingState<Int, ListElement>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListElement> {

        return try {

            val videoStore = repository.getVideoList()
            val responseList =  videoStore.data.list

            LoadResult.Page(
                data = responseList,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}
