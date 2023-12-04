package com.example.anifun.data.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anifun.data.store.Item
import com.example.anifun.repository.Repository


class RecommendVideoListDataSource(private val repository: Repository) : PagingSource<Int, Item>() {
    private var freshIdx1h = 1
    private var freshIdx = 1
    private var brush = 0
    private var lastShowList = "av_445385152,av_953970057,av_357831582,av_n_360049245,av_n_699783255,av_n_572697411,av_n_742339738,av_n_445492541,av_n_357762773,av_n_657889963"

    private val TAG = "--ExamSource"

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            val currentPage = params.key ?: 1
            val pageSize = params.loadSize

            // 每一页请求几条数据
            val everyPageSize = 10
            // 第一次初始请求，多加载一点
            val initPageSize = 10
            // 当前请求的起始位置，指起始下标
            val curStartItem =
                if (currentPage == 1) 1 else (currentPage - 2) * everyPageSize + 1 + initPageSize
            val videoStore = repository.getRecommendVideoList(freshIdx1h = (freshIdx1h).toString(), freshIdx =(freshIdx).toString(), brush = (brush).toString(), lastShowList = lastShowList)
            //            Log.e("data",videoStore.data.toString())
            //            Log.e("result",videoStore.data.result.toString())
            val responseList = videoStore.data.item
            for (index in responseList.size - 1 downTo 0){
                if (responseList[index].id == 0L){
                    responseList.removeAt(index)
                }
            }

            // 上一页页码
            val preKey = if (currentPage == 1) null else currentPage.minus(1)
            // 下一页页码
            var nextKey: Int? = currentPage.plus(1)

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