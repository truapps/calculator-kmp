package com.truapps.calculator.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truapps.calculator.app.ui.theme.NumberFontBold
import com.truapps.calculator.app.ui.theme.submitButtonGradientColor
import com.truapps.calculator.app.ui.theme.clearButtonColor
import com.truapps.calculator.app.ui.theme.functionButtonGradientColor
import com.truapps.calculator.app.ui.theme.functionButtonTextColor
import com.truapps.calculator.app.ui.theme.numberButtonColor
import com.truapps.calculator.app.ui.theme.numberButtonTextColor
import com.truapps.calculator.app.ui.theme.symbolButtonColor
import com.truapps.calculator.app.ui.theme.taxButtonBackgroundColor
import com.truapps.calculator.app.ui.theme.taxButtonColor

@Composable
private fun Center(height: Dp,width: Dp,shape: Shape,color: Color?=null,gradientColor: Brush?=null,onClick: () -> Unit,content: @Composable () -> Unit) {

    val colorModifier = if (color!=null) Modifier.background(color) else if(gradientColor!=null) Modifier.background(gradientColor) else Modifier

    Box(modifier = Modifier.clip(shape).then(colorModifier).height(height).width(width).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
fun NumberButton(text: String,height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {

    Center(height = height, width =width, color = numberButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
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
fun FunctionButton(text: String,height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {
    Center(height = height, width =width, gradientColor = functionButtonGradientColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
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
fun SubmitButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {
    Center(height = height, width =width, gradientColor = submitButtonGradientColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
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
fun TaxButton(height: Dp = 60.dp, width: Dp = 164.dp, onPlus: () -> Unit,onMinus:()->Unit) {

    Row(modifier = Modifier.clip(RoundedCornerShape(10.dp))
    .background(color = taxButtonBackgroundColor)
        .padding(horizontal = 8.dp)
        .width(width).height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        Center(height = height.minus(12.dp), width = width.div(2), color = taxButtonColor, shape = RoundedCornerShape(10.dp), onClick = onPlus) {
            Text(
                text = "TAX+",
                maxLines = 1,
                style = buttonTextStyle(
                    color = Color.White,
                    fontFamily = NumberFontBold,
                    fontSize = 24.sp
                )
            )
        }
        Center(height = height.minus(12.dp), width = width.div(2), color = taxButtonColor, shape = RoundedCornerShape(10.dp), onClick = onMinus) {
            Text(
                text = "TAX-",
                maxLines = 1,
                style = buttonTextStyle(
                    color = Color.White,
                    fontFamily = NumberFontBold,
                    fontSize = 24.sp
                )
            )
        }

    }

}

@Composable
fun DeleteButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {

    Center(height = height, width =width, color = clearButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
            text = "←",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold,
                fontSize = 24.sp
            )
        )
    }

}

@Composable
fun ClearButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {

    Center(height = height, width =width, color = clearButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
            text = "AC",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.White,
                fontFamily = NumberFontBold,
                fontSize = 24.sp
            )
        )
    }

}

@Composable
fun SqButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {

    Center(height = height, width =width, color = symbolButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
            text = "x²",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.sp,
            )
        )
    }

}

@Composable
fun CubeButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {
    Center(height = height, width =width, color = symbolButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
            text = "x³",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.sp
            )
        )
    }
}
@Composable
fun OpenBracketButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {
    Center(height = height, width =width, color = symbolButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
            text = "(",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.sp
            )
        )
    }
}
@Composable
fun CloseBracketButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {
    Center(height = height, width =width, color = symbolButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
            text = ")",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.sp
            )
        )
    }
}

@Composable
fun PlusMinusButton(height: Dp = 60.dp, width: Dp = 84.dp, onClick: () -> Unit) {
    Center(height = height, width =width, color = symbolButtonColor, shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Text(
            text = "+/-",
            maxLines = 1,
            style = buttonTextStyle(
                color = Color.Black,
                fontFamily = NumberFontBold,
                fontSize = 24.sp
            )
        )
    }
}


@Composable
private fun buttonTextStyle(
    fontSize: TextUnit = 36.sp,
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