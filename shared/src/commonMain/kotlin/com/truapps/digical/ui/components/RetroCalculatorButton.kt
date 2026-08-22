package com.truapps.digical.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truapps.digical.extensions.darken

@Composable
fun RetroCalculatorButton(
    width: Dp,
    height: Dp,
    faceColor: Color,
    bottomColor: Color = faceColor.darken(0.20f),
    cornerRadius: Dp = 16.dp,
    depth: Dp = 6.dp,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val faceOffset by animateDpAsState(
        targetValue = if (pressed) 0.dp else depth,
        animationSpec = tween(
            durationMillis = 70
        ),
        label = "buttonOffset"
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(bottomColor)
            .padding(bottom = faceOffset)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(color = faceColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
