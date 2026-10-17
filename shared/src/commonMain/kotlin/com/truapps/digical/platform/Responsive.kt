package com.truapps.digical.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * Standard design width used by the application.
 *
 * 390dp is used as the baseline for responsive scaling.
 */
private const val DESIGN_WIDTH = 390f


/**
 * Compact window scale range.
 *
 * Typically phones and small devices.
 */
private const val COMPACT_MIN_SCALE = 0.75f
private const val COMPACT_MAX_SCALE = 1.25f


/**
 * Medium window scale range.
 *
 * Typically small tablets and large foldables.
 */
private const val MEDIUM_MIN_SCALE = 1.00f
private const val MEDIUM_MAX_SCALE = 1.25f


/**
 * Expanded window scale range.
 *
 * Typically large tablets and desktop-sized windows.
 */
private const val EXPANDED_MIN_SCALE = 1.05f
private const val EXPANDED_MAX_SCALE = 1.30f


/**
 * Window/device width categories.
 */
enum class DeviceWidthClass {
    Compact,
    Medium,
    Expanded,
    Unspecified
}


/**
 * Complete responsive information available to the UI.
 */
internal data class ResponsiveInfo(

    /**
     * Current window width in Dp.
     */
    val screenWidth: Dp,

    /**
     * Current window height in Dp.
     */
    val screenHeight: Dp,

    /**
     * Current window width as an integer Dp value.
     */
    val screenWidthDp: Int,

    /**
     * Current window height as an integer Dp value.
     */
    val screenHeightDp: Int,

    /**
     * Material-style window width category.
     */
    val deviceWidthClass: DeviceWidthClass,

    /**
     * Calculated responsive scale.
     */
    val scale: Float
) {

    val isCompact: Boolean
        get() = deviceWidthClass == DeviceWidthClass.Compact

    val isMedium: Boolean
        get() = deviceWidthClass == DeviceWidthClass.Medium

    val isExpanded: Boolean
        get() = deviceWidthClass == DeviceWidthClass.Expanded

    val isSmallScreen: Boolean
        get() = screenWidthDp < DESIGN_WIDTH.toInt()

    val isLargeScreen: Boolean
        get() = screenWidthDp > DESIGN_WIDTH.toInt()
}


/**
 * Default responsive information.
 *
 * Used only as a fallback when ResponsiveProvider
 * is not present in the composition.
 */
internal val DefaultResponsiveInfo = ResponsiveInfo(
    screenWidth = 390.dp,
    screenHeight = 844.dp,
    screenWidthDp = 390,
    screenHeightDp = 844,
    deviceWidthClass = DeviceWidthClass.Compact,
    scale = 1f
)


/**
 * CompositionLocal for responsive information.
 *
 * compositionLocalOf is used because responsive information
 * can change when the window size changes.
 */
internal val LocalResponsive = compositionLocalOf {
    DefaultResponsiveInfo
}


/**
 * Provides responsive information to the composition.
 *
 * Wrap the application UI with this provider:
 *
 * ResponsiveProvider {
 *     App()
 * }
 */
@Composable
internal fun ResponsiveProvider(
    content: @Composable () -> Unit
) {
    val responsiveInfo = currentResponsiveInfo()

    CompositionLocalProvider(
        LocalResponsive provides responsiveInfo,
        content = content
    )
}


/**
 * Calculates the current Material 3 window width class.
 *
 * Platform-specific implementations should provide the
 * actual implementation.
 */
@Composable
expect fun calculateWidthSizeClass(): DeviceWidthClass


/**
 * Calculates the current responsive information.
 *
 * LocalWindowInfo.containerSize is reported in pixels,
 * so it is converted to Dp using the current density.
 *
 * minOf(width, height) is used as the reference dimension
 * so the responsive scale remains consistent between
 * portrait and landscape orientations.
 */
@Composable
private fun currentResponsiveInfo(): ResponsiveInfo {

    val windowSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current

    val screenWidth = with(density) {
        windowSize.width.toDp()
    }

    val screenHeight = with(density) {
        windowSize.height.toDp()
    }

    val screenWidthDp = screenWidth.value.toInt()
    val screenHeightDp = screenHeight.value.toInt()

    val deviceWidthClass = calculateWidthSizeClass()

    /**
     * Use the shorter dimension as the reference width.
     *
     * Example:
     *
     * Portrait:
     * 390 × 844 → 390
     *
     * Landscape:
     * 844 × 390 → 390
     */
    val referenceWidth = minOf(
        screenWidthDp,
        screenHeightDp
    )

    val scale = calculateScale(
        screenWidth = referenceWidth,
        deviceWidthClass = deviceWidthClass
    )

    return ResponsiveInfo(
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        screenWidthDp = screenWidthDp,
        screenHeightDp = screenHeightDp,
        deviceWidthClass = deviceWidthClass,
        scale = scale
    )
}


/**
 * Calculates the responsive scale.
 *
 * The scale is based on the actual reference width
 * relative to the 390dp design width.
 *
 * Compact:
 *     320dp → 0.85x
 *     360dp → 0.92x
 *     390dp → 1.00x
 *     412dp → 1.06x
 *     430dp → 1.10x
 *
 * Medium:
 *     600dp → 1.00x
 *     720dp → 1.15x
 *
 * Expanded:
 *     840dp → 1.05x
 *     1200dp → 1.20x
 */
private fun calculateScale(
    screenWidth: Int,
    deviceWidthClass: DeviceWidthClass
): Float {

    val width = screenWidth.toFloat()

    return when (deviceWidthClass) {

        DeviceWidthClass.Compact -> {
            (width / DESIGN_WIDTH).coerceIn(
                minimumValue = COMPACT_MIN_SCALE,
                maximumValue = COMPACT_MAX_SCALE
            )
        }

        DeviceWidthClass.Medium -> {
            (width / DESIGN_WIDTH).coerceIn(
                minimumValue = MEDIUM_MIN_SCALE,
                maximumValue = MEDIUM_MAX_SCALE
            )
        }

        DeviceWidthClass.Expanded -> {
            (width / DESIGN_WIDTH).coerceIn(
                minimumValue = EXPANDED_MIN_SCALE,
                maximumValue = EXPANDED_MAX_SCALE
            )
        }

        DeviceWidthClass.Unspecified -> {
            1f
        }
    }
}


/**
 * Responsive Dp.
 *
 * Example:
 *
 * 16.rdp
 *
 * At scale 0.85:
 *
 * 16.rdp = 13.6dp
 */
val Int.rdp: Dp
    @Composable
    get() = toFloat().rdp


val Float.rdp: Dp
    @Composable
    get() = (this * LocalResponsive.current.scale).dp


val Double.rdp: Dp
    @Composable
    get() = toFloat().rdp


/**
 * Responsive TextUnit.
 *
 * Example:
 *
 * 16.rsp
 *
 * At scale 0.85:
 *
 * 16.rsp = 13.6sp
 */
val Int.rsp: TextUnit
    @Composable
    get() = toFloat().rsp


val Float.rsp: TextUnit
    @Composable
    get() {
        val responsiveScale = LocalResponsive.current.scale
        val fontScale = LocalDensity.current.fontScale

        return (this * responsiveScale / fontScale).sp
    }


val Double.rsp: TextUnit
    @Composable
    get() = toFloat().rsp



/**
 * Current window width.
 */
val screenWidth: Dp
    @Composable
    get() = LocalResponsive.current.screenWidth


/**
 * Current window height.
 */
val screenHeight: Dp
    @Composable
    get() = LocalResponsive.current.screenHeight


/**
 * Current window width in integer Dp.
 */
val screenWidthDp: Int
    @Composable
    get() = LocalResponsive.current.screenWidthDp


/**
 * Current window height in integer Dp.
 */
val screenHeightDp: Int
    @Composable
    get() = LocalResponsive.current.screenHeightDp


/**
 * Current device/window width class.
 */
internal val deviceWidthClass: DeviceWidthClass
    @Composable
    get() = LocalResponsive.current.deviceWidthClass


/**
 * Current responsive scale.
 */
internal val responsiveScale: Float
    @Composable
    get() = LocalResponsive.current.scale