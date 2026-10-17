package com.truapps.digical.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import calculatorapp.shared.generated.resources.Res
import calculatorapp.shared.generated.resources.backspace
import com.truapps.digical.platform.rdp
import com.truapps.digical.platform.rsp
import com.truapps.digical.ui.theme.*
import org.jetbrains.compose.resources.painterResource

@Composable
private fun Center(height: Dp, width: Dp, shape: Shape, color: Color?=null, gradientColor: Brush?=null, onClick: () -> Unit, content: @Composable () -> Unit) {

    val colorModifier = if (color!=null) Modifier.background(color) else if(gradientColor!=null) Modifier.background(gradientColor) else Modifier

    Box(modifier = Modifier.clip(shape).then(colorModifier).height(height).width(width).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
fun NumberButton(text: String,height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {

    RetroCalculatorButton(height = height, width =width, faceColor = numberButtonColor, onClick = onClick) {
        ButtonText(
            text = text,
            maxLines = 1,
            style = buttonTextStyle(
                color = numberButtonTextColor,
                fontFamily = NumberFontBold
            )
        )
    }
}

@Composable
fun FunctionButton(text: String,height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {
    RetroCalculatorButton(height = height, width =width, faceColor = functionButtonColor, onClick = onClick) {
        ButtonText(
            text = text,
            maxLines = 1,
            style = buttonTextStyle(
                color = functionButtonTextColor,
                fontFamily = NumberFontBold
            )
        )
    }

}

@Composable
fun SubmitButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {
    RetroCalculatorButton(height = height, width =width, faceColor = submitButtonColor, onClick = onClick) {
        ButtonText(
            text = "=",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold
            )
        )
    }

}
@Composable
fun TaxButton(height: Dp = 60.rdp, width: Dp = 164.rdp, onPlus: () -> Unit,onMinus:()->Unit) {

    Row(modifier = Modifier.clip(RoundedCornerShape(16.rdp))
    .background(color = taxButtonBackgroundColor)
        .padding(horizontal = 6.rdp)
        .width(width).height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.rdp)) {

        RetroCalculatorButton(height = height.minus(6.rdp), width = width.div(2), faceColor = taxButtonColor, onClick = onPlus) {
            ButtonText(
                text = "TAX+",
                maxLines = 1,
                style = buttonTextStyle(
                    color = Color.White,
                    fontFamily = NumberFontBold,
                    fontSize = 24.rsp
                )
            )
        }
        RetroCalculatorButton(height = height.minus(6.rdp), width = width.div(2), faceColor = taxButtonColor, onClick = onMinus) {
            ButtonText(
                text = "TAX-",
                maxLines = 1,
                style = buttonTextStyle(
                    color = Color.White,
                    fontFamily = NumberFontBold,
                    fontSize = 24.rsp
                )
            )
        }

    }

}

@Composable
fun DeleteButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {

    RetroCalculatorButton(height = height, width =width, faceColor = clearButtonColor, onClick = onClick) {
        Icon(
            painter = painterResource(resource = Res.drawable.backspace),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 4.rdp, end = 2.rdp)
                .size(36.rdp),
            tint = Color.White
        )
    }

}

@Composable
fun ClearButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {

    RetroCalculatorButton(height = height, width =width, faceColor = clearButtonColor, onClick = onClick) {
        ButtonText(
            text = "AC",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold,
                fontSize = 24.rsp
            )
        )
    }

}

@Composable
fun SqButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {

    RetroCalculatorButton(height = height, width =width, faceColor = symbolButtonColor, onClick = onClick) {
        ButtonText(
            text = "×²",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.rsp,
            )
        )
    }

}

@Composable
fun CubeButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {
    RetroCalculatorButton(height = height, width =width, faceColor = symbolButtonColor, onClick = onClick) {
        ButtonText(
            text = "x³",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.rsp
            )
        )
    }
}
@Composable
fun OpenBracketButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {
    RetroCalculatorButton(height = height, width =width, faceColor = symbolButtonColor, onClick = onClick) {
        ButtonText(
            text = "(",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.rsp
            )
        )
    }
}
@Composable
fun CloseBracketButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {
    RetroCalculatorButton(height = height, width =width, faceColor = symbolButtonColor, onClick = onClick) {
        ButtonText(
            text = ")",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.rsp
            )
        )
    }
}

@Composable
fun PlusMinusButton(height: Dp = 60.rdp, width: Dp = 84.rdp, onClick: () -> Unit) {
    RetroCalculatorButton(height = height, width =width, faceColor = symbolButtonColor, onClick = onClick) {
        ButtonText(
            text = "+/-",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.rsp
            )
        )
    }
}


@Composable
private fun buttonTextStyle(
    fontSize: TextUnit = 36.rsp,
    color: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold,
    fontFamily: FontFamily = FontFamily.Default) : TextStyle {
    return TextStyle(
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        fontFamily = fontFamily,
        shadow = Shadow(color = Color.Black, offset = Offset(0f,-2f),blurRadius = 4f)
    )
}

@Composable
private fun ButtonText(
    text: String,
    maxLines: Int,
    style: TextStyle) {
    Text(
        modifier = Modifier
            .padding(bottom = 4.rdp, end = 2.rdp),
        text = text,
        maxLines = maxLines,
        style = style
    )
}