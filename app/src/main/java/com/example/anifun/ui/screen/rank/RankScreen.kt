package com.example.anifun.ui.screen.rank

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.anifun.R
import com.example.anifun.data.store.ListElement
import com.example.anifun.ui.screen.home.HomeViewModel
import com.example.anifun.ui.screen.playVideo.DkPlayerActivity
import com.example.anifun.ui.screen.playVideo.PlayVideoActivity
import com.example.anifun.ui.theme.*
import com.example.anifun.utils.calString
import com.example.anifun.utils.calTime
import java.text.SimpleDateFormat

@Composable
fun RankPage(homeViewModel: HomeViewModel = viewModel(),nestedScrollConnection: NestedScrollConnection) {
    val resList = homeViewModel.rankVideoItemList.collectAsLazyPagingItems()
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
    Column(Modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = BgGray)
                .nestedScroll(nestedScrollConnection),
            columns = GridCells.Fixed(1),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 58.dp)
        ) {
            items(resList.itemCount) { index ->
                val bgColor = when (index) {
                    0 -> Brush.verticalGradient(listOf(Color.White, RankOne))
                    1 -> Brush.verticalGradient(listOf(Color.White, RankTwo))
                    2 -> Brush.verticalGradient(listOf(Color.White, RankThree))
                    else -> Brush.verticalGradient(listOf(RanKElse, RanKElse))
                }
                if (resList[index] != null) {
                    resList[index]?.let {
                        Box() {
                            RankCard(listElement = it, mPainter)
                            Row(
                                Modifier
                                    .wrapContentSize()
                                    .clip(RoundedCornerShape(topStart = 5.dp, bottomEnd = 5.dp))
                                    .background(bgColor)
                                    .padding(top = 3.dp, start = 5.dp, end = 8.dp, bottom = 3.dp)
                                ,
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = (index + 1).toString(), fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

}

@SuppressLint("SimpleDateFormat", "WeekBasedYear")
@Composable
fun RankCard(
    listElement: ListElement,
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
            val intent = Intent(context, DkPlayerActivity::class.java).apply {
                putExtra("title", listElement.title)
                putExtra("bvid", listElement.bvid)
                putExtra("avid", listElement.aid.toString())
                putExtra("cid", listElement.cid.toString())
            }
            context.startActivity(intent)
        }
    ) {
        Box(
            modifier = Modifier
                .width(configuration.screenWidthDp.dp / 2),
            contentAlignment = Alignment.BottomStart
        ) {
            AsyncImage(
                model = listElement.pic,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.clip(
                    RoundedCornerShape(5.dp)
                ),
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
                    text = calString(listElement.stat["view"]),
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
                    text = calString(listElement.stat["danmaku"]),
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
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = listElement.title,
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
                        text = listElement.owner.name,
                        maxLines = 1,
                        color = SearchTextColor,
                        fontSize = fontSize,
                    )
                }
                Row {
                    Text(
                        text = SimpleDateFormat("YY年MM月dd日 hh:mm:ss").format(listElement.pubdate * 1000),
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