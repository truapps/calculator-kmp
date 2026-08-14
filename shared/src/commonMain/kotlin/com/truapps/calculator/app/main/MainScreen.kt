package com.truapps.calculator.app.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import calculatorapp.shared.generated.resources.Res
import calculatorapp.shared.generated.resources.header_image
import com.truapps.calculator.app.main.engine.CalculatorEngine
import com.truapps.calculator.app.main.engine.CalculatorKey
import com.truapps.calculator.app.ui.components.*
import com.truapps.calculator.app.ui.theme.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainScreen() {
    val engine = remember {
        CalculatorEngine(taxRate = 18.0)
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(engine.display.value) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }
    val calculationHistory = engine.history.joinToString("\n") { it }
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor) {

            Scaffold(containerColor = Color.Transparent){
                Column(
                    modifier = Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding().plus(12.dp), start = 8.dp, end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(resource = Res.drawable.header_image), contentDescription = null)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(displayColor)
                            .padding(8.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth().verticalScroll(state = scrollState),
                            text = buildAnnotatedString {

                                withStyle(
                                    SpanStyle(
                                        color = displayTextColor.copy(alpha = 0.5f),
                                        fontFamily = DisplayFontRegular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 24.sp
                                    )
                                ) {
                                    append(calculationHistory)
                                }

                                append("\n")

                                withStyle(
                                    SpanStyle(
                                        color = displayTextColor,
                                        fontFamily = DisplayFontRegular,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 24.sp
                                    )
                                ) {
                                    append(engine.expression.value)
                                }
                            },
                            textAlign = TextAlign.End,
                            softWrap = true
                        )
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
        },onMinus = {
            engine.press(CalculatorKey.TaxMinus)
        })
        FunctionButton(
            text = "√",
        ){
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
        ){
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
        ){
            engine.press(CalculatorKey.Divide)
        }
    }

}

@Composable
fun Row4(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

       NumberButton(text = "7"){
            engine.press(CalculatorKey.Digit(7))
       }
       NumberButton(text = "8"){
            engine.press(CalculatorKey.Digit(8))
        }
        NumberButton(text = "9"){
            engine.press(CalculatorKey.Digit(9))
        }
        FunctionButton(
            text = "×",
        ){
            engine.press(CalculatorKey.Multiply)
        }
    }

}

@Composable
fun Row5(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        NumberButton(text = "4"){
            engine.press(CalculatorKey.Digit(4))
        }
        NumberButton(text = "5"){
            engine.press(CalculatorKey.Digit(5))
        }
        NumberButton(text = "6"){
            engine.press(CalculatorKey.Digit(6))
        }
        FunctionButton(
            text = "−",
        ){
            engine.press(CalculatorKey.Subtract)
        }
    }

}

@Composable
fun Row6(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        NumberButton(text = "1"){
            engine.press(CalculatorKey.Digit(1))
        }
        NumberButton(text = "2"){
            engine.press(CalculatorKey.Digit(2))
        }
        NumberButton(text = "3"){
            engine.press(CalculatorKey.Digit(3))
        }
        FunctionButton(
            text = "+",
        ){
            engine.press(CalculatorKey.Add)
        }
    }

}

@Composable
fun Row7(engine: CalculatorEngine){

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        NumberButton(text = "0", width = 182.dp){
            engine.press(CalculatorKey.Digit(0))
        }
        NumberButton(text = "."){
            engine.press(CalculatorKey.Decimal)
        }

        SubmitButton{
            engine.press(CalculatorKey.Equals)
        }
    }

}
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}