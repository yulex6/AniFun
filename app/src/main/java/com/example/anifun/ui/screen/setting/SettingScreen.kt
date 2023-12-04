package com.example.anifun.ui.screen.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anifun.ui.screen.LocalScaffoldState
import com.example.anifun.ui.screen.LocalScope
import com.example.anifun.ui.screen.scaffoldState
import com.example.anifun.ui.theme.BilibiliPink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingPage(settingViewModel: SettingViewModel = viewModel()) {
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    val context = LocalContext.current
    val trafficPlaybackChecked =
        settingViewModel.readTrafficPlaybackChecked(context).collectAsState(
            initial = false
        )
    val scope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(statusBarHeightDp))

        SettingItem(text = "流量网络下自动播放", checked = trafficPlaybackChecked.value, onCheckedChange = {
            scope.launch{
                launch(Dispatchers.IO) {
                    settingViewModel.writeTrafficPlaybackChecked(context,it)
                    settingViewModel.setTrafficPlayAuto(trafficPlaybackChecked.value)
                }
            }
        })
        Column(Modifier.fillMaxWidth()) {
            var cookie by remember {
                mutableStateOf("")
            }
            CompositionLocalProvider(LocalScaffoldState provides scaffoldState) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "设置Cookie")
                        Button(shape = CircleShape,onClick = {
                            scope.launch{
                                launch(Dispatchers.IO) {
                                    SettingViewModel.Setting.writeCookie(context,cookie)
                                    SettingViewModel.Setting.curCookie = SettingViewModel.Setting.readCookie(context)
                                    cookie = ""
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("Cookie设置成功！", duration = SnackbarDuration.Short)
                                    }
                                }
                            }
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = BilibiliPink)) {
                            Text(text = "保存", color = Color.White)
                        }
                    }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = cookie, onValueChange = {cookie = it} , modifier = Modifier.fillMaxWidth(), maxLines = 3)
        }
    }
}

@Composable
fun SettingItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview
@Composable
fun p(){
    SettingPage()
}
