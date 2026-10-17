package com.truapps.digical.platform

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Calculates the current Material 3 window width class.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun calculateWidthSizeClass(): DeviceWidthClass {

    val context = LocalContext.current

    if (context is Activity) {

        val windowSizeClass = calculateWindowSizeClass(context)

        return when (windowSizeClass.widthSizeClass) {

            WindowWidthSizeClass.Compact ->
                DeviceWidthClass.Compact

            WindowWidthSizeClass.Medium ->
                DeviceWidthClass.Medium

            WindowWidthSizeClass.Expanded ->
                DeviceWidthClass.Expanded

            else ->
                DeviceWidthClass.Unspecified
        }
    }

    return DeviceWidthClass.Unspecified
}