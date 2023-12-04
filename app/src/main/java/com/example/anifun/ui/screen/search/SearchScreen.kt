package com.example.anifun.ui.screen.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.anifun.R
import com.example.anifun.data.store.SearchListElement
import com.example.anifun.ui.screen.home.HomeViewModel
import com.example.anifun.ui.screen.playVideo.DkPlayerActivity
import com.example.anifun.ui.screen.playVideo.PlayVideoActivity
import com.example.anifun.ui.theme.*
import com.example.anifun.utils.calString
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SearchScreen(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val searchFlag = remember {
        mutableStateOf(false)
    }
    val resList = homeViewModel.searchVideoItemList.collectAsLazyPagingItems()
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Spacer(Modifier.height(statusBarHeightDp))
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            MySearcher(
                onSearch = {
                    homeViewModel.search(it)
                    searchFlag.value = true
                    resList.refresh()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
                enable = true,
                textModifier = Modifier.background(SearchBg, CircleShape),
                placeholder = "老番茄"
            )
        }
        if (searchFlag.value) {
            if (resList.itemCount == 0) {
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
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = BgGray),
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(resList.itemCount) { index ->
                    if (resList[index] != null) {
                        resList[index]?.let {
                            SearchCard(
                                searchListElement = it,
                                mPainter = mPainter
                            )
                        }
                    }
                }
            }
        }
    }


}


@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("SimpleDateFormat", "WeekBasedYear")
@Composable
fun SearchCard(
    searchListElement: SearchListElement,
    searchScreenViewModel: SearchScreenViewModel = viewModel(),
    mPainter: AsyncImagePainter
) {
    val iconSize = 14.dp
    val fontSize = 11.sp
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    Row(modifier = Modifier
        .height(100.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(color = Color.White)
        .clickable {
            GlobalScope.launch(Dispatchers.Main) {
                val deferred: Deferred<String> = async {
                    return@async searchScreenViewModel.getVideoCid(searchListElement.bvid)
                }
                val cid = deferred.await()
                val intent = Intent(context, DkPlayerActivity::class.java).apply {
                    putExtra("title", searchListElement.title)
                    putExtra("bvid", searchListElement.bvid)
                    putExtra("avid", searchListElement.aid.toString())
                    putExtra("cid", cid)
                }
                context.startActivity(intent)
            }
        }
    ) {
        Box(
            modifier = Modifier
                .width(configuration.screenWidthDp.dp / 2),
            contentAlignment = Alignment.BottomStart
        ) {
            AsyncImage(
                model = "https:" + searchListElement.pic,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.clip(RoundedCornerShape(5.dp)),
                placeholder = mPainter
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black
                        )
                    )
                )
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
                    text = calString(searchListElement.play),
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
                    text = calString(searchListElement.videoReview),
                    color = Color.White,
                    fontSize = fontSize
                )
                Text(
                    text = searchListElement.duration,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    color = HighWhite,
                    fontSize = fontSize
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = searchListElement.title.replace("<em class=\"keyword\">", "")
                    .replace("</em>", ""),
                maxLines = 2,
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(5.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(5.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row {
                    Icon(
                        bitmap = ImageBitmap.imageResource(
                            id = R.mipmap.up
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = SearchTextColor
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = searchListElement.author,
                        maxLines = 1,
                        color = SearchTextColor,
                        fontSize = fontSize,
                    )
                }
                Row {
                    Text(
                        text = SimpleDateFormat("YY年MM月dd日 hh:mm:ss").format(searchListElement.senddate * 1000),
                        color = SearchTextColor,
                        fontSize = fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }


    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MySearcher(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    enable: Boolean = true,
    placeholder: String = "请输入",
    textModifier: Modifier = Modifier,
    textType: KeyboardType = KeyboardType.Text
) {
    Row(modifier = modifier) {
        // 用来管理软键盘
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = FocusRequester()
        var text by remember { mutableStateOf("") }
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            enabled = enable,
            modifier = textModifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        keyboardController?.show()
                    }
                }
                .fillMaxWidth(),
            singleLine = true,
            // 完成时动作自定义处理
            keyboardActions = KeyboardActions(onSearch = {
                onSearch(if (text == "") placeholder else text)
                keyboardController?.hide()
            }),
            keyboardOptions = KeyboardOptions(
                keyboardType = textType,
                imeAction = ImeAction.Search
            ),
            cursorBrush = SolidColor(BilibiliPink),
            decorationBox = @Composable { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { onSearch(if (text == "") placeholder else text) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = UnSelGray
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (text.isEmpty())
                            Text(text = placeholder, color = UnSelGray)
                        innerTextField()
                    }
                    // 这里设计为：如果有输入文字，展示清除按钮
                    if (text.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier
                                .padding(4.dp),
                            onClick = {
                                text = ""
                            },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(
                                        color = SearchDelBg,
                                        shape = CircleShape
                                    ),
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "clear",
                                tint = UnSelGray
                            )
                        }
                    }
                }
            }
        )
    }
}


