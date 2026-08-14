package com.truapps.calculator.app

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import com.truapps.calculator.app.main.MainScreen
import com.truapps.calculator.app.ui.theme.backgroundColor

@Composable
@Preview
fun App() {
   SetSystemBars(statusBarColor = backgroundColor, navigationBarColor = backgroundColor, darkIcons = false)
   MainScreen()
}