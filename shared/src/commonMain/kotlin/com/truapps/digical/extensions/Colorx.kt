package com.truapps.digical.extensions

import androidx.compose.ui.graphics.Color

fun Color.lighten(amount: Float): Color {
    return Color(
        red = red + (1f - red) * amount,
        green = green + (1f - green) * amount,
        blue = blue + (1f - blue) * amount,
        alpha = alpha
    )
}

fun Color.darken(amount: Float): Color {
    return Color(
        red = red * (1f - amount),
        green = green * (1f - amount),
        blue = blue * (1f - amount),
        alpha = alpha
    )
}