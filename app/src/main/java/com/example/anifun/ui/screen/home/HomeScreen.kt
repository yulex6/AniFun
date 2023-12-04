package com.example.anifun.ui.screen.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anifun.ui.screen.SearchScreens
import com.example.anifun.ui.theme.*

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

/**
 * 主页面
 */

@SuppressLint("SuspiciousIndentation")
@Composable
fun HomePage(navHostController: NavHostController, onHeadClick: () -> Unit) {
    val scrollState = rememberLazyGridState()
    Column(
        Modifier
            .fillMaxSize()
    ) {
        val homeViewModel: HomeViewModel = viewModel()
        val collectAsLazyPagingIDataList = homeViewModel.videoItemList.collectAsLazyPagingItems()
        val toolbarSize = 50.dp.toPx()
        // TopAppbar 的 offset
        val offset = remember {
            mutableStateOf(0f)
        }
        val nestedScrollConn = object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 滑动计算，消费让 TopAppbar 滑动的对应 Offset，同时让 TopAppbar 滑动
                var off = (offset.value) + available.y
                var con = available.y
                if (off >= 0) {
                    off = 0f
                    con = 0 - (offset.value)
                }

                if (off <= -(toolbarSize)) {
                    off = -(toolbarSize)
                    con = -(toolbarSize) - (offset.value)
                }
                offset.value = off
                return Offset(0f, con)
            }
        }
        ScrollableAppBar(
            onSearch = { homeViewModel.search(it) },
            headerOnclick = {
                onHeadClick()
            },
            SearchOnclick = {
                navHostController.navigate(SearchScreens.BeforeSearch.route) {
                    launchSingleTop = true
                    popUpTo(navHostController.graph.findStartDestination().id) {
                        // 防止状态丢失
                        saveState = true
                    }
                    // 恢复Composable的状态
                    restoreState = true
                }
            },
            offset = offset
        )
        TabRow(
            recommendList = collectAsLazyPagingIDataList,
            scrollState = scrollState,
            nestedScrollConnection = nestedScrollConn,
            offset = offset
        )

    }
}








