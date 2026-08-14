package com.truapps.calculator.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import calculatorapp.shared.generated.resources.Res
import calculatorapp.shared.generated.resources.bitcount_prop_single_light
import calculatorapp.shared.generated.resources.bitcount_prop_single_regular
import calculatorapp.shared.generated.resources.chivomono_bold
import org.jetbrains.compose.resources.Font


val DisplayFontLight
   @Composable get() = FontFamily(Font(resource = Res.font.bitcount_prop_single_light, weight = FontWeight.ExtraLight))

val DisplayFontRegular
    @Composable get() = FontFamily(Font(resource = Res.font.bitcount_prop_single_regular, weight = FontWeight.Normal))

val NumberFontBold
@Composable get() = FontFamily(Font(resource = Res.font.chivomono_bold, weight = FontWeight.Bold))
