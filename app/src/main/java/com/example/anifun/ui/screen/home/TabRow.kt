package com.example.anifun.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.example.anifun.R
import com.example.anifun.data.store.Item
import com.example.anifun.ui.screen.rank.RankPage
import com.example.anifun.ui.theme.BilibiliPink
import com.example.anifun.ui.theme.UnSelGray
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

/**
 * Tab页
 * @param recommendList 分页请求返回的视频列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabRow(
    items: List<String> = listOf("推荐", "排行榜", "番剧", "电影", "纪录片", "新闻"),
    recommendList: LazyPagingItems<Item>,
    scrollState: LazyGridState,
    nestedScrollConnection: NestedScrollConnection,
    offset: MutableState<Float>
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()    // 可滑动TabView
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier
            .wrapContentWidth()
            .height(35.dp)
            .graphicsLayer { translationY = (offset.value) },
        edgePadding = 0.dp,
        // 这里用默认指示器，可自定义
        indicator = { tabIndicator ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(
                    pagerState, tabIndicator
                ),
                color = BilibiliPink
            )
        },
        // 背景色，有了一列tab，会挡住此背景，首尾各露出edgePadding的长度
        backgroundColor = colorResource(id = R.color.white),
        // 底部分割线，可缺省，目的是为了与下面正文隔离开来
        divider = {
            TabRowDefaults.Divider(color = Color.Gray)
        }
    ) {
        items.forEachIndexed { index, title ->
            val selected = index == pagerState.currentPage
            val color = if (selected) BilibiliPink else UnSelGray
            val isBold = if (selected) FontWeight.Bold else null
            Tab(
                modifier = Modifier.background(color = colorResource(id = R.color.white)),
                text = { Text(title, color = color, fontSize = 15.sp, fontWeight = isBold) },
                selected = selected,
                selectedContentColor = BilibiliPink,
                onClick = {
                    scope.launch {
                        // Pager的切换
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
    // 横向Pager类似PagerView
    HorizontalPager(
        state = pagerState,
        pageCount = items.size,
        reverseLayout = false,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationY = (offset.value) }
    ) { indexPage ->
        // 以下是Pager的内容
        Column(Modifier.fillMaxSize()) {
            when (indexPage) {
                0 -> Recommend(
                    recommendList = recommendList,
                    scrollState = scrollState,
                    nestedScrollConnection = nestedScrollConnection
                )
                1 -> RankPage(nestedScrollConnection = nestedScrollConnection)
                2 -> AnimeScheduleScreen(nestedScrollConnection = nestedScrollConnection)
                in 3..(items.size) -> Text(text = items[indexPage])

            }
        }


    }
}