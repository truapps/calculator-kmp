package com.truapps.calculator.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun SetSystemBars(statusBarColor: Color,
                             navigationBarColor: Color,
                             darkIcons: Boolean
)