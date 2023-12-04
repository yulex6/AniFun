package com.example.anifun.ui.screen.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.anifun.ui.screen.*
import com.example.anifun.ui.theme.BilibiliPink
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navHostController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    val name = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val unRegisterFlag = remember {
        mutableStateOf(false)
    }
    val alterText = remember {
        mutableStateOf("")
    }
    val showPassword = remember {
        mutableStateOf(false)
    }
    val nameIsEmpty = remember {
        mutableStateOf(false)
    }
    val passwordIsEmpty = remember {
        mutableStateOf(false)
    }
    val statusBarHeightDp = LocalDensity.current.run {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(Modifier.height(statusBarHeightDp))
            Row(
                Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "欢迎登录-v-",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4,
                color = BilibiliPink
            )
        }
        Column(
            Modifier.padding(10.dp)
        ) {
            Row {
                Column {
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {
                                        name.value = it
                                        nameIsEmpty.value = false},
                        singleLine = true,
                        shape = shapes.large,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            if(!nameIsEmpty.value)
                                Text(text = "用户名")
                            else
                                Text(text = "用户名不能为空")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = BilibiliPink
                        )
                    )
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it
                                         passwordIsEmpty.value = false},
                        singleLine = true,
                        shape = shapes.large,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            if (!passwordIsEmpty.value)
                                Text(text = "密码")
                            else
                                Text(text = "密码不能为空")
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = BilibiliPink
                        ),
                        visualTransformation = if (!showPassword.value) PasswordVisualTransformation('*') else VisualTransformation.None,
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.Visibility,
                                contentDescription = null,
                                Modifier.clickable { showPassword.value = ! showPassword.value }
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            CompositionLocalProvider(LocalScaffoldState provides scaffoldState) {
                CompositionLocalProvider(LocalScope provides scope) {
                    Row(Modifier.fillMaxWidth()) {
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = BilibiliPink),
                            onClick = {
                                if (name.value == ""){
                                    nameIsEmpty.value = true
                                    if (password.value == ""){
                                        passwordIsEmpty.value = true
                                        return@Button
                                    }
                                    return@Button
                                }
                                val rep = mainViewModel.login(name = name.value, password = password.value)
                                if (!rep) {
                                    alterText.value = "账户密码错错误或此账号未注册！"
                                    unRegisterFlag.value = true
                                } else {
                                    mainViewModel.checkIsLogin()
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("登录成功",actionLabel = null, duration =SnackbarDuration.Short )
                                    }

                                    navHostController.popBackStack()

                                }
                            }) {
                            Text(text = "登录", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = BilibiliPink),
                            onClick = {
                                if (name.value == ""){
                                    nameIsEmpty.value = true
                                    if (password.value == ""){
                                        passwordIsEmpty.value = true
                                        return@Button
                                    }
                                    return@Button
                                }
                                mainViewModel.register(name = name.value, password = password.value)
                                alterText.value = "注册成功"
                                unRegisterFlag.value = true
                            }) {
                            Text(text = "注册", color = Color.White)
                        }
                    }
                }

            }

        }

        Spacer(modifier = Modifier.height(10.dp))
        if (unRegisterFlag.value) {
            AlertDialog(
                onDismissRequest = {
                },
                text = {
                    Text(
                        text = alterText.value
                    )
                },
                confirmButton = {
                    TextButton(onClick = { unRegisterFlag.value = false }) {
                        Text(text = "确认")
                    }
                }
            )
        }

    }


}