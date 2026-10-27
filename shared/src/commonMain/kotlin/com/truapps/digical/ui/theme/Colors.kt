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
val numberButtonColor = Color(0xFF505050)
val numberButtonTextColor = Color(0xFFF0F0F0)
val clearButtonColor = Color(0xFF9A3F3F)
val clearButtonTextColor = Color.White
val taxButtonBackgroundColor = Color(0xFF15B2D9)
val taxButtonColor = Color(0xFF137992)
val taxButtonTextColor = Color.White

val symbolButtonColor = Color(0xFF808A8B)
val symbolButtonTextColor = Color.Black
val submitButtonGradientColor = Brush.verticalGradient(
    colors = listOf(Color(0xFF458077), Color(0xFF0E1A18)),
    startY = 32.dp.value,
)
val submitButtonColor = Color(0xFF458077)


//v2

val surfaceColorV2 = Color(0xFF2B2B2B)
val displayColorV2 = Color(0xFFBFBFA2)
val buttonLightBorderColor = Color(0xFFF0F0F0)
val buttonAreaColorV2 = Color(0xFF494949)
val buttonColorV2 = Color(0xFF505050)
val redButtonColorV2 = Color(0xFFD42228)

val retroButtonBorderBrush = Brush.verticalGradient(
    0.0f to Color.White,
    0.25f to Color.White.copy(alpha = 0.75f),
    0.5f to Color.White.copy(alpha = 0.50f),
    0.75f to Color.White.copy(alpha = 0.25f),
    1f to Color.Black.copy(alpha = 1f),

)

val displayBorderBrush = Brush.verticalGradient(
    0.0f to Color.White,
    0.25f to Color.White.copy(alpha = 0.15f),
    0.5f to Color.White.copy(alpha = 0.30f),
    0.75f to Color.White.copy(alpha = 0.75f),
    1f to Color.White.copy(alpha = 1f),

    )