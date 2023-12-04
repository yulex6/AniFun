package com.example.anifun.ui.screen.photo

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.anifun.R
import com.example.anifun.ui.screen.search.MySearcher
import com.example.anifun.ui.theme.BgGray
import com.example.anifun.ui.theme.SearchBg
import com.example.anifun.ui.theme.UnSelGray

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotosPage(navHostController: NavHostController,photoScreenViewModel: PhotoScreenViewModel = viewModel()) {
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    val resList = photoScreenViewModel.dynamicItemList.collectAsLazyPagingItems()
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
    Column {
        Spacer(Modifier.height(statusBarHeightDp))
        Row(
            modifier = Modifier.padding(5.dp) ,
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            MySearcher(
                onSearch = {
                    if (it == "") return@MySearcher
                    photoScreenViewModel.search(it)
                    photoScreenViewModel.setFlag(true)
                    resList.refresh()
                },
                textType = KeyboardType.Number,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
                enable = true,
                textModifier = Modifier.background(SearchBg, CircleShape),
                placeholder = ""
            )
        }
        if (photoScreenViewModel.getFlag()) {
            if (resList.itemCount == 0){
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
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = BgGray),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                contentPadding = PaddingValues(8.dp)
            ) {
                items(resList.itemCount) { index ->
                    if (resList[index] != null) {
                        resList[index]?.let { Card(elevation = 10.dp ){
                            AsyncImage(
                                model = it.modules.moduleDynamic.major!!.draw!!.items[0].src+"@50q.webp",
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    photoScreenViewModel.openPreView(context,it.modules.moduleDynamic.major.draw!!.items[0].src)
                                                              },
                                contentScale = ContentScale.FillWidth,
                                placeholder = mPainter
                            )

                        }
                        }
                    }
                }
            }
        }else{
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically
            , horizontalArrangement = Arrangement.Center) {
                Text(text = "输入uid查询相簿", color = UnSelGray)
            }
        }
    }
}
