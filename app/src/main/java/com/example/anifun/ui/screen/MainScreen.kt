package com.example.anifun.ui.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.anifun.R
import com.example.anifun.ui.screen.setting.SettingViewModel
import com.example.anifun.ui.theme.BilibiliPink
import com.example.anifun.ui.theme.UnSelGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

lateinit var scaffoldState : ScaffoldState
lateinit var scope : CoroutineScope
val LocalScaffoldState = compositionLocalOf { scaffoldState }
val LocalScope = compositionLocalOf { scope }
/**
 * 首屏 主页面
 * */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "SuspiciousIndentation")
@Preview
@Composable
fun MainPage(mainViewModel: MainViewModel = viewModel(), settingViewModel: SettingViewModel = viewModel()) {
    // 底部导航对应页面
    val list = listOf(
        Screens.Home,
        Screens.Music,
        Screens.Chat,
        Screens.Setting,
    )
    val desList = listOf(
        "home_route",
        "chat_route",
        "music_route",
        "setting_route"
    )
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    scaffoldState = rememberScaffoldState()
    scope = rememberCoroutineScope()
    mainViewModel.checkIsLogin()
    val setting = settingViewModel.readTrafficPlaybackChecked(LocalContext.current)
        .collectAsState(initial = false)
    settingViewModel.setTrafficPlayAuto(setting.value)
    val name = if (mainViewModel.isLogin.value) mainViewModel.session!!.curName else "未登录"
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawerContent(scaffoldState, scope,name = name, logoutClick = {
                val res =   mainViewModel.logout()
                if (res == "success"){
                    scope.launch {
                        scaffoldState.drawerState.close()

                    }
                }
            },
                toPhotoPage = {
                    scope.launch {
                        scaffoldState.drawerState.close()

                    }
                    navController.navigate(PhotoScreens.Photo.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            // 防止状态丢失
                            saveState = true
                        }
                        // 恢复Composable的状态
                        restoreState = true
                    }
                }
            )
        },
        bottomBar = {
            if ( currentDestination?.route in desList)
                BottomNavigationScreen(navController = navController, items = list)
        }
    ) {
        // 导航容器，导航目的地改变时改变内容

            NavHost(navHostController = navController, onHeadClick = {
                if (mainViewModel.isLogin.value){
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }else{
                    navController.navigate(LoginScreens.Login.route) {
                        launchSingleTop = true
                    }
                }

            })
        }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppDrawerContent(
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    name: String = "未登录",
    @DrawableRes headImage: Int = R.drawable.test,
    logoutClick: () -> Unit,
    toPhotoPage: () -> Unit
) {
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    Spacer(Modifier.height(statusBarHeightDp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = headImage),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp)),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.h5,
                color = BilibiliPink
            )
        }
        Icon(imageVector = if (isSystemInDarkTheme()) Icons.Filled.DarkMode  else Icons.Filled.LightMode, contentDescription = null)
    }
    ListItem(
        icon = {
            Icon(imageVector = Icons.Filled.ImageSearch, null)
        },
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                toPhotoPage()
            }
    ) {
        Text("相簿")
    }
    ListItem(
        icon = {
            Icon(Icons.Filled.Logout, null)
        },
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                logoutClick()
            }
    ) {
        Text("登出")
    }


    // 编写逻辑
    // 如果 drawer 已经展开了，那么点击返回键收起而不是直接退出 app
    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }
}


/**
 * 底部导航栏
 * */
@Composable
fun BottomNavigationScreen(navController: NavController, items: List<Screens>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    BottomNavigation(
        backgroundColor = Color.White,
        elevation = 12.dp,
        modifier = Modifier.height(50.dp)
    ) {
        items.forEach { screen ->
            // 底部的每一个选项
            val selFlag =
                if (destination?.route == screen.route) screen.selIcons else screen.unSelIcons
            BottomNavigationItem(
                selected = destination?.route == screen.route,
                // 点击响应跳转
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            // 防止状态丢失
                            saveState = true
                        }
                        // 恢复Composable的状态
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = selFlag),
//                        Icons.Filled.Home,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = { Text(screen.title) },
                alwaysShowLabel = true,
                unselectedContentColor = UnSelGray,
                selectedContentColor = BilibiliPink,
            )

        }
    }
}

