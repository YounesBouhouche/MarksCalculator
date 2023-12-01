package com.example.markscalculator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.markscalculator.*
import com.example.markscalculator.datastores.Settings
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val dynamicColor = Settings(LocalContext.current).dynamicColors.collectAsState(initial = false).value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val extraDark = Settings(LocalContext.current).extraDark.collectAsState(initial = false).value
    val colorTheme = Settings(LocalContext.current).colorTheme.collectAsState(initial = "green").value
    val lightColors = when(colorTheme) {
        "blue" -> BlueLightColors
        "green" -> GreenLightColors
        "red" -> RedLightColors
        "orange" -> OrangeLightColors
        "purple" -> PurpleLightColors
        else -> BlueLightColors
    }
    val darkColors = when(colorTheme) {
        "blue" -> BlueDarkColors
        "green" -> GreenDarkColors
        "red" -> RedDarkColors
        "orange" -> OrangeDarkColors
        "purple" -> PurpleDarkColors
        else -> BlueDarkColors
    }
    val isDark = when (Settings(LocalContext.current).theme.collectAsState(initial = "auto").value) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        dynamicColor && isDark -> {
            if(extraDark) dynamicDarkColorScheme(LocalContext.current).copy(
                background = Color.Black,
                surface = Color.Black
            )
            else dynamicDarkColorScheme(LocalContext.current)
        }
        dynamicColor && !isDark -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        isDark and extraDark -> darkColors.copy(
            background = Color.Black,
            surface = Color.Black
        )
        isDark -> darkColors
        else -> lightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = /*colorScheme.surface*/ Color.Transparent, darkIcons = !isDark)
    systemUiController.setNavigationBarColor(color = /*colorScheme.primary.copy(alpha = .08f).compositeOver(colorScheme.surface.copy())*/ Color.Transparent)
}