package com.example.anifun.data.dataSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anifun.data.store.SearchListElement
import com.example.anifun.repository.Repository

class SearchVideoListDataSource(private val repository: Repository, private val query: String) : PagingSource<Int, SearchListElement>() {
    override fun getRefreshKey(state: PagingState<Int, SearchListElement>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchListElement> {
        return try {
            val currentPage = params.key ?: 1
            val pageSize = params.loadSize

            val responseList : List<SearchListElement> = if (query != ""){
                val videoStore = repository.getSearchVideoList(query,currentPage, pageSize = pageSize)
                videoStore.data.result[videoStore.data.result.size-1].data
            }else{
                emptyList()
            }

            // 上一页页码
            val preKey = if (currentPage == 1) null else currentPage.minus(1)
            // 下一页页码
            var nextKey: Int? = currentPage.plus(1)
            if (nextKey != null) {
                if (query == ""||nextKey > 50) {
                    nextKey = null
                }
            }
            LoadResult.Page(
                data = responseList,
                prevKey = preKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}
