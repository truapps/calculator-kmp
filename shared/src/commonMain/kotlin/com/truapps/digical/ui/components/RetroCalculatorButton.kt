package com.truapps.digical.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truapps.digical.extensions.darken
import com.truapps.digical.platform.rdp
import com.truapps.digical.ui.theme.retroButtonBorderBrush

@Composable
fun RetroCalculatorButton(
    width: Dp,
    height: Dp,
    faceColor: Color,
    bottomColor: Color = faceColor.darken(0.80f),
    cornerRadius: Dp = 16.rdp,
    depth: Dp = 6.rdp,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()


    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = tween(
            durationMillis = 100
        ),
        label = "buttonScale"
    )


    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(bottomColor)
            .padding(bottom = depth)
            .border(width = 2.dp, color = bottomColor, shape = RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
                .clip(RoundedCornerShape(cornerRadius))
                .background(color = faceColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun RetroCalculatorButtonV2(
    width: Dp = 74.rdp,
    height: Dp = 48.rdp,
    faceColor: Color,
    shape: RoundedCornerShape = RoundedCornerShape(16.rdp),
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()


    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 100
        ),
        label = "buttonScale"
    )


    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(shape)
            .border(width = 2.rdp, color = Color.Black, shape = shape)
            .padding(1.rdp)
            .background(color = Color.Black)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {

        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
                .fillMaxSize()
                .clip(shape)
                .border(
                    width = 4.rdp,
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = shape
                )
                .border(
                    width = 4.rdp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = shape
                )
                .border(
                    width = 2.rdp,
                    brush = retroButtonBorderBrush,
                    shape = shape
                )
                .background(color = faceColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}