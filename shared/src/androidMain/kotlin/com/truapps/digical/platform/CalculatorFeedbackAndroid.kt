package com.truapps.digical.platform

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.truapps.digical.shared.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CalculatorFeedbackAndroid (private val context: Context) : CalculatorFeedback() {


    override val _isMuted: MutableState<Boolean>  = mutableStateOf(false)
    override val isMuted: State<Boolean> =  _isMuted


    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(
                Context.VIBRATOR_MANAGER_SERVICE
            ) as VibratorManager

            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }
    }

    private val soundPool: SoundPool by lazy {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attributes)
            .build()
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val clickSoundId: Int = soundPool.load(
        this@CalculatorFeedbackAndroid.context,
        R.raw.button_press,
        1
    )

    private fun click(volume: Float = 0.5f) {

        if(isMuted()) return
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        val systemVolume = if (maxVolume > 0) {
            (currentVolume.toFloat() / maxVolume)
        } else {
            0f
        }


        val adjustedVolume = when {
            systemVolume >= 0.75f -> {
                (volume * 0.1f).coerceIn(0.01f, 0.05f)
            }

            systemVolume>= 0.5f -> {
                (volume * 0.1f).coerceIn(0.05f, 0.25f)
            }

            else -> {
                (volume * systemVolume).coerceIn(0.25f, 0.5f)
            }
        }
        soundPool.play(
            clickSoundId,
            adjustedVolume,
            adjustedVolume,
            1,
            0,
            1f
        )
    }


    @SuppressLint("MissingPermission")
    private fun lightHaptic() {
        if (!vibrator.hasVibrator()) {
            println("DiGiCAL: NO VIBRATOR")
            return
        }

        val pattern = longArrayOf(
            0L,   // start immediately
            10L,  // strong press
            20L,  // tiny gap
            5L    // tiny rebound
        )

        val amplitudes = intArrayOf(
            0,
            25,
            0,
            10
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    pattern,
                    amplitudes,
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern,-1)
        }

        println("DiGiCAL: vibration requested")
    }


    @SuppressLint("MissingPermission")
    private fun mediumHaptic() {
        if (!vibrator.hasVibrator()) return

        val pattern = longArrayOf(
            0L,
            15L,
            20L,
            10L
        )

        val amplitudes = intArrayOf(
            0,
            125,
            0,
            100
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    pattern,
                    amplitudes,
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern,-1)
        }

        println("DiGiCAL: vibration requested")
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
        soundPool.release()
    }
}