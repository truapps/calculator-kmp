package com.truapps.digical.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
fun NumberButton(text: String,isAtCorner: Boolean = false,onClick: () -> Unit) {

    RetroCalculatorButtonV2(
        shape = if(isAtCorner) RoundedCornerShape(
            topStart = 16.rdp,
            topEnd = 16.rdp,
            bottomEnd = 16.rdp,
            bottomStart = 32.rdp,
        )  else RoundedCornerShape(16.rdp),
        faceColor = numberButtonColor, onClick = onClick) {
        ButtonText(
            text = text,
            style = buttonTextStyle(
                color = numberButtonTextColor,
                fontFamily = NumberFontBold
            )
        )
    }
}

@Composable
fun FunctionButton(text: String, isAtCorner: Boolean = false, onClick: () -> Unit) {
    RetroCalculatorButtonV2(
        shape = if(isAtCorner) RoundedCornerShape(
            topStart = 16.rdp,
            topEnd = 32.rdp,
            bottomEnd = 16.rdp,
            bottomStart = 16.rdp,
        )  else RoundedCornerShape(16.rdp),
        faceColor = functionButtonColor, onClick = onClick) {
        ButtonText(
            text = text,
            style = buttonTextStyle(
                color = functionButtonTextColor,
                fontFamily = NumberFontBold,
                fontSize = 32.rsp
            )
        )
    }

}

@Composable
fun SubmitButton(onClick: () -> Unit) {
    RetroCalculatorButtonV2(
        width = 152.rdp,
        shape = RoundedCornerShape(
            topStart = 16.rdp,
            topEnd = 16.rdp,
            bottomStart = 16.rdp,
            bottomEnd = 32.rdp,
        ),
        faceColor = submitButtonColor, onClick = onClick) {
        ButtonText(
            text = "=",
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold,
                fontSize = 32.rsp
            )
        )
    }

}
@Composable
fun TaxButton(onPlus: () -> Unit,onMinus:()->Unit) {

    val height = 48.rdp
    val width = 156.rdp
    Row(modifier = Modifier.clip(RoundedCornerShape(16.rdp))
    .background(color = taxButtonBackgroundColor)
        .padding(horizontal = 6.rdp)
        .width(width).height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.rdp)) {

        RetroCalculatorButtonV2(height = height.minus(6.rdp), width = width.div(2), faceColor = taxButtonColor, onClick = onPlus) {
            ButtonText(
                text = "TAX+",
                style = buttonTextStyle(
                    color = Color.White,
                    fontFamily = NumberFontBold
                )
            )
        }
        RetroCalculatorButtonV2(height = height.minus(6.rdp), width = width.div(2), faceColor = taxButtonColor, onClick = onMinus) {
            ButtonText(
                text = "TAX-",
                style = buttonTextStyle(
                    color = Color.White,
                    fontFamily = NumberFontBold
                )
            )
        }

    }

}

@Composable
fun DeleteButton(onClick: () -> Unit) {

    RetroCalculatorButtonV2(
        shape = RoundedCornerShape(
            topStart = 32.rdp,
            topEnd = 16.rdp,
            bottomStart = 16.rdp,
            bottomEnd = 16.rdp,
        ),
        faceColor = clearButtonColor, onClick = onClick) {
        Icon(
            painter = painterResource(resource = Res.drawable.backspace),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 4.rdp, end = 2.rdp)
                .size(24.rdp),
            tint = Color.White
        )
    }

}

@Composable
fun ClearButton(onClick: () -> Unit) {

    RetroCalculatorButtonV2(faceColor = clearButtonColor, onClick = onClick) {
        ButtonText(
            text = "AC",
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold
            )
        )
    }

}

@Composable
fun SqButton(onClick: () -> Unit) {

    RetroCalculatorButtonV2(faceColor = buttonColorV2, onClick = onClick) {
        ButtonText(
            text = "×²",
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold
            )
        )
    }

}

@Composable
fun CubeButton(onClick: () -> Unit) {
    RetroCalculatorButtonV2(faceColor = buttonColorV2, onClick = onClick) {
        ButtonText(
            text = "x³",
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold
            )
        )
    }
}
@Composable
fun OpenBracketButton(onClick: () -> Unit) {
    RetroCalculatorButtonV2(faceColor = buttonColorV2, onClick = onClick) {
        ButtonText(
            text = "(",
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold,
                fontSize = 24.rsp
            )
        )
    }
}
@Composable
fun CloseBracketButton(onClick: () -> Unit) {
    RetroCalculatorButtonV2(faceColor = buttonColorV2, onClick = onClick) {
        ButtonText(
            text = ")",
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold
            )
        )
    }
}

@Composable
fun PlusMinusButton(onClick: () -> Unit) {
    RetroCalculatorButtonV2(faceColor = buttonColorV2, onClick = onClick) {
        ButtonText(
            text = "+/-",
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold
            )
        )
    }
}


@Composable
private fun buttonTextStyle(
    fontSize: TextUnit = 20.rsp,
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
    style: TextStyle) {
    Text(
        modifier = Modifier
            .padding(bottom = 4.rdp, end = 2.rdp),
        text = text,
        maxLines = 1,
        style = style
    )
}