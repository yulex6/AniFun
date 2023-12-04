package com.example.anifun.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.anifun.R
import com.example.anifun.ui.screen.chat.ChatScreen
import com.example.anifun.ui.screen.home.HomePage
import com.example.anifun.ui.screen.login.LoginScreen
import com.example.anifun.ui.screen.musicScreen.MusicScreen
import com.example.anifun.ui.screen.photo.PhotosPage
import com.example.anifun.ui.screen.search.SearchScreen
import com.example.anifun.ui.screen.setting.SettingPage

/**
 * 首页底部页面路由定义
 * */
sealed class Screens(
    val title: String,
    val route: String,
    @DrawableRes val unSelIcons: Int,
    @DrawableRes val selIcons: Int
) {

    object Home : Screens(
        title = "首页",
        route = "home_route",
        unSelIcons = R.mipmap.home,
        selIcons = R.mipmap.home_selected
    )

    object Chat :
        Screens(
            title = "聊天",
            route = "chat_route",
            unSelIcons = R.drawable.robot,
            selIcons = R.drawable.robot_selected
        )

    object Music:
        Screens(
            title = "音乐",
            route = "music_route",
            unSelIcons = R.mipmap.music,
            selIcons = R.mipmap.music_selected
        )

    object Setting :
        Screens(
            title = "设置",
            route = "setting_route",
            unSelIcons = R.mipmap.setting,
            selIcons = R.mipmap.setting_selected
        )
}

sealed class SearchScreens(val title: String, val route: String) {
    object BeforeSearch : SearchScreens(title = "首页", route = "before_route")
}
sealed class LoginScreens(val title: String, val route: String) {
    object Login : LoginScreens(title = "登录", route = "login_route")
}


sealed class PhotoScreens(val title: String, val route: String) {
    object Photo : PhotoScreens(title = "相簿", route = "photo_route")
}
/**
 * 将Home设为默认页面
 * */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost(navHostController: NavHostController, onHeadClick: () -> Unit) {
    androidx.navigation.compose.NavHost(
        navController = navHostController,
        startDestination = Screens.Home.route
    ) {
        composable(route = Screens.Home.route) {
            HomePage(navHostController, onHeadClick = onHeadClick)
        }
        composable(route = Screens.Chat.route) {
            ChatScreen()
        }
        composable(route = Screens.Music.route) {
            MusicScreen()
        }
        composable(route = Screens.Setting.route) {
            SettingPage()
        }
        composable(route = SearchScreens.BeforeSearch.route) {
            SearchScreen(navHostController)
        }
        composable(route = LoginScreens.Login.route) {
            LoginScreen(navHostController = navHostController)
        }
        composable(route = PhotoScreens.Photo.route) {
            PhotosPage(navHostController = navHostController)
        }
    }
}
