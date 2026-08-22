package com.truapps.digical.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val backgroundColor = Color(0xFF4E5253)
val displayColor = Color(0xFFB7C1A6)
val displayTextColor = Color.Black
val functionButtonGradientColor = Brush.verticalGradient(
    colors = listOf(Color(0xFFD68A41), Color(0xFF704822)),
    startY = 32.dp.value,

)

val functionButtonColor = Color(0xFFD68A41)
val functionButtonTextColor = Color.White
val numberButtonColor = Color(0xFFCCCBC6)
val numberButtonTextColor = Color.Black
val clearButtonColor = Color(0xFF9A3F3F)
val clearButtonTextColor = Color.White
val taxButtonBackgroundColor = Color(0xFF15B2D9)
val taxButtonColor = Color.Black.copy(alpha = 0.20f)
val taxButtonTextColor = Color.White

val symbolButtonColor = Color(0xFFA6B0B1)
val symbolButtonTextColor = Color.Black
val submitButtonGradientColor = Brush.verticalGradient(
    colors = listOf(Color(0xFF458077), Color(0xFF0E1A18)),
    startY = 32.dp.value,
)
val submitButtonColor = Color(0xFF458077)