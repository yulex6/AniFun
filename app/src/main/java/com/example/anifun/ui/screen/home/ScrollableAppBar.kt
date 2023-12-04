package com.example.anifun.ui.screen.home

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.anifun.R
import com.example.anifun.ui.screen.search.MySearcher
import com.example.anifun.ui.theme.SearchBorder

/**
 * 顶部栏
 */
@Composable
fun ScrollableAppBar(
    onSearch: (String) -> Unit,
    headerOnclick: () -> Unit,
    SearchOnclick: () -> Unit,
    offset: MutableState<Float>
) {
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    val interactionSource = remember { MutableInteractionSource() }
    Spacer(modifier = Modifier.height(statusBarHeightDp))
    //api33 175
    //mz 16s pro 150
    val h = -offset.value / 150
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .graphicsLayer { translationY = (offset.value) }
            .alpha(1- h),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyHeaderImage(onClick = headerOnclick)
        Spacer(modifier = Modifier.width(10.dp))
        MySearcher(
            onSearch = onSearch, modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .height(35.dp)
                .clickable(
                    onClick = SearchOnclick, interactionSource = interactionSource,
                    indication = null
                ),
            enable = false,
            textModifier = Modifier
                .background(Color.White, CircleShape)
                .border(
                    width = 1.dp,
                    color = SearchBorder, shape = CircleShape
                ),
            placeholder = "老番茄"
        )
    }
}
/**
 * 自定义头像
 */
@Composable
fun MyHeaderImage(
    onClick: () -> Unit,
    @DrawableRes headImage: Int = R.drawable.test,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Image(
        painter = painterResource(id = headImage),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .clip(RoundedCornerShape(20.dp)),
    )
}