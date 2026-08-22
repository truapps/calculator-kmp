package com.truapps.digical

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.truapps.digical.main.MainScreen
import com.truapps.digical.ui.theme.backgroundColor
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
@Preview
fun App() {
   SetSystemBars(statusBarColor = backgroundColor, navigationBarColor = backgroundColor, darkIcons = false)
   MainScreen()


}

@Composable
fun NoisyBackground(
   modifier: Modifier = Modifier,
   baseColor: Color = Color(0xFFD8D2C4),
   noiseAlpha: Float = 0.06f
) {

   val coroutineScope = rememberCoroutineScope()
   Canvas(
      modifier = modifier
         .background(baseColor)
   ) {
      val random = Random(1234)

      coroutineScope.launch {
         repeat((size.width * size.height / 18).toInt()) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height

            val brightness = random.nextFloat()

            drawCircle(
               color = if (brightness > 0.5f) {
                  Color.White.copy(alpha = noiseAlpha)
               } else {
                  Color.Black.copy(alpha = noiseAlpha)
               },
               radius = random.nextFloat() * 0.8f + 0.2f,
               center = Offset(x, y)
            )
         }
      }

   }
}

@Composable
fun NoiseOverlay(
   modifier: Modifier = Modifier,
   alpha: Float = 0.035f
) {
   val random = rememberNoiseRandom()

   Canvas(modifier = modifier) {

      repeat((size.width * size.height / 20).toInt()) {
         val x = random.nextFloat() * size.width
         val y = random.nextFloat() * size.height

         val light = random.nextBoolean()

         drawCircle(
            color = if (light) {
               Color.White.copy(alpha = alpha)
            } else {
               Color.Black.copy(alpha = alpha)
            },
            radius = random.nextFloat() * 0.7f + 0.2f,
            center = Offset(x, y)
         )
      }
   }
}

@Composable
private fun rememberNoiseRandom(
   seed: Int = 1234
): Random {
   return remember(seed) {
      Random(seed)
   }
}
