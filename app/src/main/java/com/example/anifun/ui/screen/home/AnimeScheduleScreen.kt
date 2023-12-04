package com.example.anifun.ui.screen.home


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.anifun.R
import com.example.anifun.data.store.Episode
import com.example.anifun.data.store.TimeLine
import com.example.anifun.ui.theme.BilibiliPink
import com.example.anifun.ui.theme.UnSelGray
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeScheduleScreen(homeViewModel: HomeViewModel = viewModel(),nestedScrollConnection: NestedScrollConnection) {
    var timeLine by remember {
        mutableStateOf<TimeLine?>(
            null
        )
    }
    val dates = remember {
        mutableStateListOf<String>()
    }
    LaunchedEffect(Unit) {
        timeLine = homeViewModel.getAnimeSchedule()
        for (one in timeLine!!.result) {
            dates.add(one.date.split("-")[1])
        }
    }
    if (dates.size >= 7) {
        dates[LocalDate.now().dayOfWeek.value - 1] = "今"
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = Color.White)
                .nestedScroll(nestedScrollConnection)
        ) {
            val items = listOf("一", "二", "三", "四", "五", "六", "日")
            val pagerState = rememberPagerState(initialPage = LocalDate.now().dayOfWeek.value - 1)
            val scope = rememberCoroutineScope()    // 可滑动TabView
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(52.dp)
                    .padding(start = 3.dp, end = 3.dp),
                // 这里用默认指示器，可自定义
                indicator = { tabIndicator ->
                    //修改指示器长度
                    val indicatorOffset by animateDpAsState(
                        targetValue = 3.dp,
                    )
                    TabRowDefaults.Indicator(
                        Modifier
                            .pagerTabIndicatorOffset(
                                pagerState, tabIndicator
                            )
                            .offset(y = -indicatorOffset)
                            .height(25.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(color = BilibiliPink),
                        color = BilibiliPink
                    )
                },
                // 背景色，有了一列tab，会挡住此背景，首尾各露出edgePadding的长度
                backgroundColor = colorResource(id = R.color.white),
                // 底部分割线，可缺省，目的是为了与下面正文隔离开来
                divider = {}
            ) {
                items.forEachIndexed { index, title ->
                    val selected = index == pagerState.currentPage
                    val color = UnSelGray
                    val dateColor = if (selected) Color.White else Color.Black
                    Tab(
                        modifier = Modifier.zIndex(1f),
                        text = {
                            Column {
                                Text(
                                    title,
                                    color = color,
                                    fontSize = 15.sp,
                                    modifier = Modifier.align(CenterHorizontally)
                                )
                                Text(
                                    text = dates[index],
                                    color = dateColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.align(CenterHorizontally)
                                )
                            }
                        },
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
//                pageNestedScrollConnection = nestedScrollConnection,
                ) { indexPage ->
                // 以下是Pager的内容
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    AnimeList(list = timeLine!!.result[indexPage].episodes)
                }
            }
        }


    }

}

@Composable
fun AnimeList(list: List<Episode>) {
    val scrollState = rememberLazyGridState()
    if (list.isEmpty()) {
        Text(text = "暂无番剧更新！")
    } else {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            state = scrollState,
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(list.size) { index ->
                Column {
                    Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                        AsyncImage(
                            model = list[index].cover,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = null,
                            modifier = Modifier
                                .height(170.dp)

                        )
                        Row(
                            Modifier
                                .height(20.dp)
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.Black
                                        )
                                    )
                                )
                                .align(BottomCenter)
                        ) {
                            Text(text = list[index].pubIndex,
                                Modifier.fillMaxWidth(),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = list[index].title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = 15.sp
                    )
                }
            }

        }
    }

}
