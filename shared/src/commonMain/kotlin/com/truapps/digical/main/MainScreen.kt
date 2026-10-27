package com.truapps.digical.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import calculatorapp.shared.generated.resources.*
import com.truapps.digical.main.engine.CalculatorEngine
import com.truapps.digical.main.engine.CalculatorKey
import com.truapps.digical.platform.CalculatorFeedback
import com.truapps.digical.platform.rdp
import com.truapps.digical.platform.rsp
import com.truapps.digical.ui.components.*
import com.truapps.digical.ui.theme.AppTheme
import com.truapps.digical.ui.theme.DisplayFontRegular
import com.truapps.digical.ui.theme.NumberFontBold
import com.truapps.digical.ui.theme.backgroundColor
import com.truapps.digical.ui.theme.buttonAreaColorV2
import com.truapps.digical.ui.theme.clearButtonColor
import com.truapps.digical.ui.theme.displayBorderBrush
import com.truapps.digical.ui.theme.displayColorV2
import com.truapps.digical.ui.theme.displayTextColor
import com.truapps.digical.ui.theme.retroButtonBorderBrush
import com.truapps.digical.ui.theme.surfaceColorV2
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MainScreen(feedback: CalculatorFeedback) {

    val engine = remember {
        CalculatorEngine(feedback)
    }

    var expressionValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = engine.expression.value,
                selection = TextRange(engine.expression.value.length)
            )
        )
    }

    val errorMessage by engine.errorMessage

    val scrollState = rememberScrollState()

    LaunchedEffect(engine.display.value) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    LaunchedEffect(engine.expression.value) {

        val newText = engine.expression.value

        if (expressionValue.text != newText) {

            expressionValue = TextFieldValue(
                text = newText,
                selection = TextRange(
                    engine.cursorPosition.value.coerceIn(
                        0,
                        newText.length
                    )
                )
            )
        }
    }
    var textLayoutResult by remember {
        mutableStateOf<TextLayoutResult?>(null)
    }

    val textScrollState = rememberScrollState()
    val expression = errorMessage ?: engine.expression.value

    LaunchedEffect(expression) {
        textScrollState.animateScrollTo(textScrollState.maxValue)
    }

    var cursorVisible by remember {
        mutableStateOf(true)
    }
    val cursorPosition by engine.cursorPosition
    LaunchedEffect(cursorPosition,expression) {

        cursorVisible = true
        delay(500.milliseconds)
        while (true) {
            delay(500.milliseconds)
            cursorVisible = !cursorVisible
        }
    }
    val blinkIndex = when {
        errorMessage != null -> -1
        expression.isEmpty() -> -1
        cursorPosition < expression.length -> cursorPosition-1
        else -> expression.lastIndex
    }

    val annotatedExpression = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = if (errorMessage != null) {
                    clearButtonColor
                } else {
                    displayTextColor
                },
                fontFamily = if(errorMessage!=null) NumberFontBold else DisplayFontRegular,
                fontWeight = FontWeight.Normal,
                fontSize =  if(errorMessage!=null) 14.rsp else 36.rsp,
                letterSpacing = 1.rsp
            )
        ) {
            append(expression)
        }

        if (blinkIndex >= 0) {
            addStyle(
                style = SpanStyle(
                    color = if (cursorVisible) {
                        displayTextColor
                    } else {
                        Color.Transparent
                    }
                ),
                start = blinkIndex,
                end = blinkIndex + 1
            )
        }
    }


    val calculationHistory = engine.history.joinToString(separator = "\n", prefix = "\n") { it }
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
        ) {

            Scaffold(containerColor = surfaceColorV2) {
                Column(
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding().value.rdp,
                        bottom = it.calculateBottomPadding().value.rdp.plus(12.rdp),
                        start = 16.rdp,
                        end = 16.rdp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.rdp)
                ) {

                    Box{
                        Image(
                            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                            painter = painterResource(resource = Res.drawable.header_image), contentDescription = null
                        )

                        Box(modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxWidth(0.2f)) {

                            Icon(painter = painterResource(if(feedback.isMuted.value) Res.drawable.mute_sound else  Res.drawable.unmute_sound), contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(36.rdp).clickable {

                                        feedback.toggleMute()
                                        feedback.mediumFeedback()

                                    })

                        }

                    }


                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.rdp))
                            .border(
                                width = 4.rdp,
                                color = Color.White.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(32.rdp)
                            )
                            .border(
                                width = 6.dp,
                                color = Color.Black.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(32.rdp)
                            )
                            .padding(2.rdp)
                            .border(
                                width = 4.dp,
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(32.rdp)
                            )
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.BottomEnd
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(2.rdp)
                                .clip(RoundedCornerShape(32.rdp))
                                .background(displayColorV2)
                                .padding(start = 16.rdp,end = 16.rdp, bottom = 8.rdp, top = 8.rdp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.End
                        ) {

                            // HISTORY
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .graphicsLayer{
                                        compositingStrategy = CompositingStrategy.Offscreen
                                    }
                                    .drawWithContent{
                                        drawContent()
                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colorStops = arrayOf(
                                                    0f to Color.Transparent,
                                                    1f to Color.White,
                                                ),
                                                startY = 0f,
                                                endY = size.height * 0.15f,
                                            ),
                                            blendMode = BlendMode.DstIn,
                                        )

                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colorStops = arrayOf(
                                                    0f to Color.White,
                                                    1f to Color.Transparent,
                                                ),
                                                startY = size.height * 0.95f,
                                                endY = size.height,
                                            ),
                                            blendMode = BlendMode.DstIn,
                                        )
                                    }
                                    .weight(1f), contentAlignment = Alignment.BottomEnd
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(scrollState),

                                    text = buildAnnotatedString {

                                        withStyle(
                                            SpanStyle(
                                                color = displayTextColor.copy(
                                                    alpha = 0.5f
                                                ),
                                                fontFamily = DisplayFontRegular,
                                                fontSize = 20.rsp,
                                                letterSpacing = 1.rsp
                                            )
                                        ) {
                                            append(calculationHistory)
                                        }
                                    },

                                    textAlign = TextAlign.End
                                )
                            }

                            // CURRENT EXPRESSION
                            Box(
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .shakeOnError(errorMessage!=null)
                                    .horizontalScroll(state = textScrollState)
                                    .focusable()
                                    .onKeyEvent { event ->

                                        val calculatorKey = engine.getCalculatorKey(event)

                                        if (calculatorKey != null) {
                                            engine.press(calculatorKey)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                    .pointerInput(Unit) {

                                        detectTapGestures { offset ->

                                            val layout = textLayoutResult ?: return@detectTapGestures

                                            val position = layout.getOffsetForPosition(offset)

                                            engine.setCursorPosition(position)
                                        }
                                    }
                            ,contentAlignment = Alignment.BottomEnd) {

                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = annotatedExpression,
                                    textAlign = TextAlign.End,
                                    maxLines = 1,
                                    softWrap = false,

                                    onTextLayout = {layout->
                                        textLayoutResult = layout
                                    }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.clip(
                            RoundedCornerShape(32.rdp)
                        ).background(color = buttonAreaColorV2)
                        .padding(16.rdp),
                        verticalArrangement = Arrangement.spacedBy(16.rdp),horizontalAlignment = Alignment.CenterHorizontally) {

                        Row1(engine)
                        Row2(engine)
                        Row3(engine)
                        Row4(engine)
                        Row5(engine)
                        Row6(engine)
                        Row7(engine)
                    }



                }
            }

        }


    }
}


@Composable
fun Row1(engine: CalculatorEngine) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

       DeleteButton {
            engine.press(CalculatorKey.Backspace)
        }
       TaxButton(onPlus = {
            engine.press(CalculatorKey.TaxPlus)
        }, onMinus = {
            engine.press(CalculatorKey.TaxMinus)
        })
       FunctionButton(
            text = "√",
           isAtCorner = true,
        ) {
            engine.press(CalculatorKey.SqRoot)
        }
    }

}

@Composable
fun Row2(engine: CalculatorEngine) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

       ClearButton {
            engine.press(CalculatorKey.Clear)
        }

       SqButton {
            engine.press(CalculatorKey.Square)
        }
       CubeButton {
            engine.press(CalculatorKey.Cube)
        }
       FunctionButton(
            text = "%",
        ) {
            engine.press(CalculatorKey.Percentage)
        }
    }

}
@Composable
fun Row3(engine: CalculatorEngine) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

       PlusMinusButton {
            engine.press(CalculatorKey.PlusMinus)
        }
       OpenBracketButton {
            engine.press(CalculatorKey.OpenParenthesis)
        }

       CloseBracketButton {
            engine.press(CalculatorKey.CloseParenthesis)
        }
       FunctionButton(
            text = "÷",
        ) {
            engine.press(CalculatorKey.Divide)
        }
    }

}

@Composable
fun Row4(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

      NumberButton(text = "7") {
           engine.press(CalculatorKey.Digit(7))
       }
       NumberButton(text = "8") {
            engine.press(CalculatorKey.Digit(8))
        }
       NumberButton(text = "9") {
            engine.press(CalculatorKey.Digit(9))
        }
       FunctionButton(
            text = "×",
        ) {
            engine.press(CalculatorKey.Multiply)
        }
    }

}

@Composable
fun Row5(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

       NumberButton(text = "4") {
            engine.press(CalculatorKey.Digit(4))
        }
       NumberButton(text = "5") {
            engine.press(CalculatorKey.Digit(5))
        }
       NumberButton(text = "6") {
            engine.press(CalculatorKey.Digit(6))
        }
       FunctionButton(
            text = "−",
        ) {
            engine.press(CalculatorKey.Subtract)
        }
    }

}

@Composable
fun Row6(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

       NumberButton(text = "1") {
            engine.press(CalculatorKey.Digit(1))
        }
       NumberButton(text = "2") {
            engine.press(CalculatorKey.Digit(2))
        }
       NumberButton(text = "3") {
            engine.press(CalculatorKey.Digit(3))
        }
       FunctionButton(
            text = "+",
        ) {
            engine.press(CalculatorKey.Add)
        }
    }

}

@Composable
fun Row7(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

       NumberButton(text = "0",isAtCorner = true) {
            engine.press(CalculatorKey.Digit(0))
        }
       NumberButton(text = ".") {
            engine.press(CalculatorKey.Decimal)
        }

       SubmitButton {
            engine.press(CalculatorKey.Equals)
        }
    }

}

fun Modifier.shakeOnError(
    error: Boolean,
    shakeDistance: Dp = 4.dp,
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(error) {
        if (error) {
            offsetX.snapTo(0f)

            repeat(2) {
                offsetX.animateTo(
                    targetValue = shakeDistance.value,
                    animationSpec = tween(40)
                )
                offsetX.animateTo(
                    targetValue = -shakeDistance.value,
                    animationSpec = tween(40)
                )
            }

            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = spring()
            )
        }
    }
    offset(x = offsetX.value.dp)
}