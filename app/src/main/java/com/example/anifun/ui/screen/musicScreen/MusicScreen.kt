package com.example.anifun.ui.screen.musicScreen

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.anifun.R
import com.example.anifun.coil.transform.BlurTransformation
import com.example.anifun.ui.screen.LocalScaffoldState
import com.example.anifun.ui.screen.LocalScope
import com.example.anifun.ui.screen.scaffoldState
import com.example.anifun.ui.theme.*
import kotlinx.coroutines.launch

@SuppressLint("InflateParams", "MissingInflatedId")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicScreen(
    musicScreenViewModel: MusicScreenViewModel = viewModel(),
) {
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    val musicList = musicScreenViewModel.musicList
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val context = LocalContext.current
    val imgLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val mPainter = rememberAsyncImagePainter(R.drawable.loading, imgLoader)

    if (musicList.size >= 3) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(musicList[pagerState.currentPage].data.picurl)
                .transformations(BlurTransformation(LocalContext.current, 16f))
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    //背景遮上半透明颜色，改善明亮色调的背景下，白色操作按钮的显示效果
                    drawRect(Color.Gray, alpha = 0.7f)
                },
            contentScale = ContentScale.FillHeight,
            colorFilter = ColorFilter.tint(Color.Gray, BlendMode.Darken)
        )
        CompositionLocalProvider(LocalScaffoldState provides scaffoldState) {
            CompositionLocalProvider(LocalScope provides com.example.anifun.ui.screen.scope) {
                LaunchedEffect(musicScreenViewModel.isError) {
                    if (musicScreenViewModel.isError.value)
                        scaffoldState.snackbarHostState.showSnackbar(
                            "播放出错了！",
                            actionLabel = null,
                            duration = SnackbarDuration.Short
                        )
                }
                LaunchedEffect(musicScreenViewModel.isNeedToScrollToNextPage.value) {
                    scope.launch {
                        Log.e("LaunchedEffect", "LaunchedEffect")
                        if (musicScreenViewModel.isNeedToScrollToNextPage.value) {
                            pagerState.animateScrollToPage(musicScreenViewModel.curIndex)
                            musicScreenViewModel.isNeedToScrollToNextPage.value = false
                        }
                    }
                }
                Column(
                    Modifier
                        .fillMaxSize()
                ) {
                    Spacer(Modifier.height(statusBarHeightDp))
                    HorizontalPager(
                        pageCount = musicList.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 3 / 4),
                        state = pagerState,
                        userScrollEnabled = false,
                        contentPadding = PaddingValues(top = 25.dp, start = 35.dp, end = 35.dp),
                        pageSpacing = 20.dp
                    ) { index ->
                        Column {
                            Text(
                                text = musicList[index].data.name,
                                style = MaterialTheme.typography.h6,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = musicList[index].data.artistsname,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            AsyncImage(
                                model = musicList[index].data.picurl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.FillBounds,
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = musicScreenViewModel.curTime.value, color = Color.White)
                        Spacer(modifier = Modifier.width(5.dp))
                        //歌曲进度
                        Slider(
                            value = musicScreenViewModel.curProgress.value, onValueChange = {
                                musicScreenViewModel.seekMusic(it)
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .weight(2f),
                            colors = SliderDefaults.colors(
                                activeTrackColor = BilibiliPink,
                                inactiveTrackColor = SliderPlayed,
                                thumbColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = musicScreenViewModel.endTime.value, color = Color.White)
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    //播放操作
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //上一首
                        Icon(painter = painterResource(id = R.drawable.pre),
                            tint = PlayTiny,
                            contentDescription = "上一首",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null) {
                                    Log.e("currentPage", pagerState.currentPage.toString())
                                    Log.e("curIndex", musicScreenViewModel.curIndex.toString())
                                    if (pagerState.currentPage != 0) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                        musicScreenViewModel.preMusic()
                                    }
                                }
                        )
                        Spacer(modifier = Modifier.width(50.dp))
                        //播放暂停
                        if (musicScreenViewModel.isPlaying.value) {
                            Icon(painter = painterResource(id = R.drawable.music_pause),
                                tint = PlayTiny,
                                contentDescription = "暂停",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null) {

                                        musicScreenViewModel.pauseMusic()
                                    }
                            )
                        } else {
                            Icon(painter = painterResource(id = R.drawable.music_play),
                                tint = PlayTiny,
                                contentDescription = "播放",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable(interactionSource = remember { MutableInteractionSource() },
                                        indication = null) {
                                        if (musicScreenViewModel.isFirstPlay) {
                                            musicScreenViewModel.isFirstPlay = false
                                            musicScreenViewModel.startMusic(context)
                                        } else {
                                            musicScreenViewModel.playMusic()
                                        }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.width(50.dp))
                        //下一首
                        Icon(painter = painterResource(id = R.drawable.next),
                            tint = PlayTiny,
                            contentDescription = "下一首",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null) {
                                    if (musicScreenViewModel.checkServiceIsNull()) {
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "请先点击播放",
                                                actionLabel = null,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        return@clickable
                                    }
                                    if (pagerState.currentPage + 1 >= musicList.size || !musicScreenViewModel.checkHasNext()) {
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "点击太快了,数据缓冲中！",
                                                actionLabel = null,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        return@clickable
                                    }
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                    musicScreenViewModel.nextMusic()
                                }
                        )
                    }
                }
            }
        }
    } else {
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
    }
}

