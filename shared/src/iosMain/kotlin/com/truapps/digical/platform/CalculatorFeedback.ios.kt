package com.truapps.digical.platform

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

class CalculatorFeedbackIOS: CalculatorFeedback() {


    override val _isMuted: MutableState<Boolean> = mutableStateOf(false)
    override val isMuted: State<Boolean> = _isMuted
    private fun click() {
        AudioServicesPlaySystemSound(1104u)
    }

    private fun lightHaptic() {
        val generator = UIImpactFeedbackGenerator(
            style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
        )

        generator.prepare()
        generator.impactOccurred()
    }

    private fun mediumHaptic() {
        val generator = UIImpactFeedbackGenerator(
            style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
        )

        generator.prepare()
        generator.impactOccurred()
    }


     override fun lightFeedback() {
        click()
        lightHaptic()
    }

     override fun mediumFeedback() {
        click()
        mediumHaptic()
    }
     override fun dispose() {
        // Nothing to release
    }
}