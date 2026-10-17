package com.truapps.digical

import androidx.compose.ui.window.ComposeUIViewController
import com.truapps.digical.platform.CalculatorFeedbackIOS

fun MainViewController() = ComposeUIViewController { App(CalculatorFeedbackIOS()) }