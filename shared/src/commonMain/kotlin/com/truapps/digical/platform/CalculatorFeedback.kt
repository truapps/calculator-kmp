package com.truapps.digical.platform

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State

abstract class CalculatorFeedback {

    protected abstract val _isMuted : MutableState<Boolean>
    abstract val isMuted : State<Boolean>

    fun toggleMute(){
        _isMuted.value = !_isMuted.value
    }

    protected fun isMuted(): Boolean = isMuted.value
    abstract fun lightFeedback()
    abstract fun mediumFeedback()
    abstract fun dispose()
}