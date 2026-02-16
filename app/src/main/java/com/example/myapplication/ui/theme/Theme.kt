package com.example.myapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/* -------------------------
   EXTRA CUSTOM COLORS
--------------------------*/

data class AppExtraColors(
    val topColor: Color,
    val bottomColor: Color,
    val greenAccent: Color,
    val bandeOuter: Color,
    val boutonOuter: Color,
    val boutonInter: Color,
    val shadow: Color
)

private val DarkExtraColors = AppExtraColors(
    topColor = Color(0xFF0F0F0F),
    bottomColor = Color(0xFF1A1A1A),
    greenAccent = Color(0x885CFF88),
    bandeOuter = Color(0xFFFF8C32),
    boutonOuter = Color(0xFFFF8C32),
    boutonInter = Color(0xFFCC5A1A),
    shadow = Color(0xFF8F8F8F)
)

private val LightExtraColors = AppExtraColors(
    topColor = Color.Red,
    bottomColor = Color.White,
    greenAccent = Color.Transparent,
    bandeOuter = Color.Black,
    boutonOuter = Color.Black,
    boutonInter = Color.White,
    shadow = Color.Black
)

val LocalExtraColors = staticCompositionLocalOf { DarkExtraColors }

/* -------------------------
   THEME
--------------------------*/

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // ColorScheme Material = automatique
    val materialColors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        else -> if (darkTheme) darkColorScheme() else lightColorScheme()
    }

    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    CompositionLocalProvider(
        LocalExtraColors provides extraColors
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = Typography,
            content = content
        )
    }
}