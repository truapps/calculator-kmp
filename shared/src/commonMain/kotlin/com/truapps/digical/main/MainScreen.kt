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
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
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
import com.truapps.digical.ui.theme.clearButtonColor
import com.truapps.digical.ui.theme.displayTextColor
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
    var cursorVisible by remember {
        mutableStateOf(true)
    }

    val cursorPainter = painterResource(
        Res.drawable.digital_cursor
    )

    LaunchedEffect(engine.cursorPosition) {

        cursorVisible = true

        while (true) {
            delay(500.milliseconds)
            cursorVisible = !cursorVisible
        }
    }


    val calculationHistory = engine.history.joinToString("\n") { it }
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
        ) {

            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(resource = Res.drawable.calculator_background),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Scaffold(containerColor = Color.Transparent) {
                Column(
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding().value.rdp,
                        bottom = it.calculateBottomPadding().value.rdp.plus(12.rdp),
                        start = 8.rdp,
                        end = 8.rdp
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
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(24.rdp))
                            .innerShadow(
                                shape = RoundedCornerShape(24.rdp),
                                shadow = Shadow(
                                    radius = 16.rdp,
                                    color = Color.Black.copy(alpha = 0.25f),
                                    spread = 4.rdp
                                )
                            )
                    ) {

                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(resource = Res.drawable.display_background),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier.padding(16.rdp),
                            verticalArrangement = Arrangement.Bottom
                        ) {

                            // HISTORY
                            Box(
                                modifier = Modifier.fillMaxWidth()
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
                                                fontSize = 24.rsp,
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
                                    .fillMaxWidth()
                                    .shakeOnError(errorMessage!=null)
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
                            ) {

                                Text(
                                    modifier = Modifier.fillMaxWidth(),

                                    text = errorMessage ?: engine.expression.value,

                                    textAlign = TextAlign.End,

                                    style = TextStyle(
                                        color = if(errorMessage!=null) clearButtonColor else displayTextColor,
                                        fontFamily = DisplayFontRegular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 24.rsp,
                                        letterSpacing = 1.rsp
                                    ),

                                    onTextLayout = {
                                        textLayoutResult = it
                                    }
                                )


                                Canvas(
                                    modifier = Modifier.matchParentSize()
                                ) {

                                    val layout = textLayoutResult ?: return@Canvas

                                    if(!cursorVisible || errorMessage!=null) return@Canvas

                                    layout.getCursorRect(
                                        engine.cursorPosition.value
                                    ).let { rect ->



                                        with(cursorPainter) {
                                            translate(
                                                left = rect.left,
                                                top = rect.top
                                            ) {
                                                draw(
                                                    size = Size(
                                                        2.dp.toPx(),
                                                        rect.height
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

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

       NumberButton(text = "0", width = 182.rdp) {
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