package com.example.anifun.ui.screen.home

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.anifun.R
import com.example.anifun.data.store.Item
import com.example.anifun.ui.screen.playVideo.DkPlayerActivity
import com.example.anifun.ui.screen.playVideo.PlayVideoActivity
import com.example.anifun.ui.screen.setting.SettingViewModel
import com.example.anifun.ui.theme.BgGray
import com.example.anifun.ui.theme.BilibiliPink
import com.example.anifun.ui.theme.HighWhite
import com.example.anifun.ui.theme.SearchTextColor
import com.example.anifun.utils.calString
import com.example.anifun.utils.calTime
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * 推荐列表
 * @param recommendList 分页请求返回的视频列表
 */
@Composable
fun Recommend(
    recommendList: LazyPagingItems<Item>,
    scrollState: LazyGridState,
    nestedScrollConnection: NestedScrollConnection
) {
    val context = LocalContext.current
    val imgLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val mPainter = rememberAsyncImagePainter(R.drawable.loading, imgLoader)
    if(SettingViewModel.Setting.curCookie == ""){
        Log.e("11","11")
        Row(
            Modifier
                .fillMaxSize()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "请先设置Cookie")
                Button(shape = CircleShape,onClick = {
                     recommendList.refresh()
                }, colors = ButtonDefaults.buttonColors(backgroundColor = BilibiliPink)) {
                    Text(text = "刷新", color = Color.White)
                }
            }
        }
    } else if (recommendList.itemCount == 0) {
        Row(
            Modifier
                .fillMaxSize()
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = mPainter,
                contentDescription = "123",
                modifier = Modifier
                    .wrapContentSize()
            )
        }
    }else{
        SwipeRefresh(
            state = rememberSwipeRefreshState(recommendList.loadState.refresh is LoadState.Loading && recommendList.itemCount > 0),
            onRefresh = { recommendList.refresh() },
            modifier = Modifier.fillMaxSize(),
            indicator = { state, refreshTrigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTrigger,
                    contentColor = BilibiliPink
                )
            }
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = BgGray)
                    .nestedScroll(nestedScrollConnection),
                state = scrollState,
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(recommendList.itemCount) { index ->
                    if (recommendList[index] != null) {
                        recommendList[index]?.let { MyCard(listElement = it, mPainter) }
                    }
                }

            }
        }
    }

}

/**
 * 自定义的单个视频卡片
 * @param listElement 单个视频内容
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyCard(listElement: Item, mPainter: AsyncImagePainter) {
    val iconSize = 13.dp
    val fontSize = 10.sp
    val context = LocalContext.current
    Card(
        elevation = 10.dp,
        onClick = {
            val intent = Intent(context, DkPlayerActivity::class.java).apply {
                putExtra("title", listElement.title)
                putExtra("bvid", listElement.bvid)
                putExtra("avid", listElement.id.toString())
                putExtra("cid", listElement.cid.toString())
            }
            context.startActivity(intent)
        }
    ) {
        Column(
            modifier = Modifier
                .height(170.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Black),
                    contentAlignment = Alignment.BottomStart
                ) {
                    AsyncImage(
                        model = listElement.pic,
                        contentDescription = null,
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        placeholder = mPainter
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black
                                    )
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            bitmap = ImageBitmap.imageResource(
                                id = R.mipmap.watch
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(iconSize),
                            tint = HighWhite
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = calString(listElement.stat?.view),
                            color = Color.White,
                            fontSize = fontSize
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            bitmap = ImageBitmap.imageResource(
                                id = R.mipmap.barrage
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(iconSize),
                            tint = HighWhite
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = calString(listElement.stat?.danmaku),
                            color = Color.White,
                            fontSize = fontSize
                        )
                        Text(
                            text = calTime(listElement.duration),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            color = HighWhite,
                            fontSize = fontSize
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.Top,

                ) {
                Text(
                    text = listElement.title,
                    maxLines = 2,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Icon(
                    bitmap = ImageBitmap.imageResource(
                        id = R.mipmap.up
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = SearchTextColor
                )
                Spacer(modifier = Modifier.width(5.dp))
                listElement.owner?.name?.let {
                    Text(
                        text = it,
                        color = SearchTextColor,
                        fontSize = fontSize
                    )
                }
            }
        }
    }

}
