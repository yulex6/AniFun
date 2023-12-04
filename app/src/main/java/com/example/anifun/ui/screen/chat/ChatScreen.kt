package com.example.anifun.ui.screen.chat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anifun.R
import com.example.anifun.data.entity.Chat
import com.example.anifun.ui.theme.BgGray
import com.example.anifun.ui.theme.BilibiliPink
import com.example.anifun.ui.theme.SearchTextColor


/**
 * 聊天界面
 * @param [chatViewModel] 聊天视图模型
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel()) {
    //状态栏高度
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    val chats = chatViewModel.flow.collectAsLazyPagingItems()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    MultiBottomSheetLayout(
        scaffoldState = scaffoldState,
        scope = scope,
        sheetElevation = 10.dp,
        sheetShape = RoundedCornerShape(20.dp),
        sheetModifier = Modifier.padding(top = statusBarHeightDp),
        topLeftIcon = {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier.clickable {
                    chatViewModel.deleteAll()
                },
                tint = Color.Black.copy(alpha = 0.45F),
            )
        },
        topCenterIcon = {
            Icon(
                painter = painterResource(id = R.drawable.line),
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.45F),
            )
        },
        topRightIcon = {
            Icon(
                Icons.Filled.Close,
                tint = Color.Black.copy(alpha = 0.45F),
                contentDescription = null
            )
        },
        sheetContent = { bundle ->
            //👇👇底部弹出来的BottomSheet👇👇
            PopBottomScreen(arguments = bundle, chats = chats)
        }, mainContent = { openSheet ->
            //主界面内容，使用：openSheet(MultiBottomSheet.Intent())触发Intent
            //会回调上面👆👆sheetContent={}👆👆这里
            MainContent(openSheet)
        })
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PopBottomScreen(arguments: Bundle?, chatViewModel: ChatViewModel = viewModel(),chats: LazyPagingItems<Chat>) {
    val id = arguments?.getInt("contact")
    val head = arguments?.getInt("head")
    val name = arguments?.getString("name")
    val screenHeight = LocalConfiguration.current.screenHeightDp
    if (id != null && head != null && name != null) {
        chatViewModel.setCurContact(id)
        LaunchedEffect(chatViewModel.curContact.value){
            chats.refresh()
        }
        val scrollState = rememberLazyListState()
        val input = remember {
            mutableStateOf("")
        }
        Column(
            Modifier
                .fillMaxWidth()
                .height(screenHeight.times(0.9).dp)
                .background(color = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f),
                state = scrollState,
                verticalArrangement = Arrangement.Bottom,
                reverseLayout = true,
                contentPadding = PaddingValues(8.dp, top = 40.dp, bottom = 10.dp),
            ) {
                items(chats.itemCount) { index ->
                    MessageCard(chat = chats[index], head = head)
                }
            }
            OutlinedTextField(
                value = input.value,
                onValueChange = { input.value = it },
                singleLine = true,
                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BilibiliPink
                ),
                trailingIcon = {
                    Icon(imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        Modifier.clickable {
                            if (input.value == "") return@clickable
                            chatViewModel.sendToRobot(input.value, name = name)
                            input.value = ""
                        }
                    )
                }
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    } else {
        Surface(
            Modifier
                .fillMaxWidth()
                .height(screenHeight.times(0.9).dp)
        ) {
        }
    }
}


@Composable
fun MainContent(
    openSheet: (MultiBottomSheet.Intent) -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    chatViewModel.findHistoryList()
    val historyList = chatViewModel.historyList
    val scrollState = rememberLazyListState()
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        )
        {
            val interactionSource = remember { MutableInteractionSource() }
            Image(
                painter = painterResource(R.drawable.tx_robot),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        openSheet(MultiBottomSheet.Intent(Bundle().apply {
                            putInt("contact", 0)
                            putInt("head", R.drawable.tx_robot)
                            putString("name", "天行Robot")
                        }))
                    }
            )
            Spacer(modifier = Modifier.width(5.dp))
            Image(
                painter = painterResource(R.drawable.own_think),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        openSheet(MultiBottomSheet.Intent(Bundle().apply {
                            putInt("contact", 1)
                            putInt("head", R.drawable.own_think)
                            putString("name", "ownThinkRobot")
                        }))
                    }
            )
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Divider(color = BilibiliPink, modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 5.dp), startIndent = 5.dp)
            Text(text = "聊天记录", color = SearchTextColor)
            Divider(color = BilibiliPink, modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 5.dp), startIndent = 5.dp)
        }
        LazyColumn(
            Modifier.fillMaxSize(),
            state = scrollState,
            contentPadding = PaddingValues(top = 10.dp)
        ) {
            items(count = historyList.size) { index ->
                val head =
                    when (historyList[index].contactId){
                        0 -> R.drawable.tx_robot
                        1 -> R.drawable.own_think
                        else -> R.drawable.robot
                    }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()

                        .clip(RoundedCornerShape(10.dp))
                        .background(color = BgGray)
                        .clickable {
                            openSheet(MultiBottomSheet.Intent(Bundle().apply {
                                putInt("contact", historyList[index].contactId)
                                putInt("head", head)
                                putString("name", historyList[index].contactName)
                            }))
                        }
                        .padding(5.dp)) {
                    Image(
                        painter = painterResource(head),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        historyList[index].contactName?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colors.secondaryVariant,
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            elevation = 1.dp,
                        ) {
                            Text(
                                text = historyList[index].msg,
                                modifier = Modifier.padding(all = 4.dp),
                                // If the message is expanded, we display all its content
                                // otherwise we only display the first line
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
fun MessageCard(chat: Chat?, head: Int) {
    if (chat != null) {
        when (chat.type) {
            0 -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(head),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    var isExpanded by remember { mutableStateOf(false) }

                    Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                        chat.contactName?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colors.secondaryVariant,
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            elevation = 1.dp,
                        ) {
                            Text(
                                text = chat.msg,
                                modifier = Modifier.padding(all = 4.dp),
                                // If the message is expanded, we display all its content
                                // otherwise we only display the first line
                                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                                style = MaterialTheme.typography.body2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            1 -> {
                Row(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isExpanded by remember { mutableStateOf(false) }

                    Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            elevation = 1.dp,
                        ) {
                            Text(
                                text = chat.msg,
                                modifier = Modifier.padding(all = 4.dp),
                                // If the message is expanded, we display all its content
                                // otherwise we only display the first line
                                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                                style = MaterialTheme.typography.body2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.test),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
                    )
                }
            }
        }
    }
}

