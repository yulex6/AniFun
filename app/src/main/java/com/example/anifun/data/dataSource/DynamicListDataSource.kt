package com.example.anifun.data.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anifun.data.store.DynamicItem
import com.example.anifun.repository.Repository

/**
 * 动态列表数据源
 * @author yu
 * @date 2023/07/03
 * @constructor 创建[DynamicListDataSource]
 * @param [repository] 存储库
 * @param [uid] uid
 */
class DynamicListDataSource(private val repository: Repository, private val uid:String) : PagingSource<Int, DynamicItem>()  {
    private var offset = ""
    private var hasMore = true

    override fun getRefreshKey(state: PagingState<Int, DynamicItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DynamicItem> {

        return try {
            val currentPage = params.key ?: 1
            //访问api获取
            val responseList : List<DynamicItem> = if (uid != ""){
                val videoStore = repository.getDynamicList(offset, uid)
                offset = videoStore.data.offset
                hasMore = videoStore.data.hasMore
                videoStore.data.items
            }else{
                emptyList()
            }
            val res = mutableListOf<DynamicItem>()
            for (resItem in responseList){
                if (resItem.modules.moduleDynamic.major != null){
                    if (resItem.modules.moduleDynamic.major.type == "MAJOR_TYPE_DRAW"){
                        res.add(resItem)
                    }
                }
            }
            // 上一页页码
            val preKey = if (currentPage == 1) null else currentPage.minus(1)
            // 下一页页码
            var nextKey: Int? = currentPage.plus(1)
            if (!hasMore ||uid == "") {
                nextKey = null
            }
            LoadResult.Page(
                data = res,
                prevKey = preKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}