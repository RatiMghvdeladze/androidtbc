// In Theme.kt
package com.example.androidtbc.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    val PrimaryColor = Color(0xFFB683F7)
    val BackgroundColor = Color(0xFFF5F5F5)
    val TextColor = Color.Black
    val White = Color.White
    val ErrorColor = Color(0xFFB00020)
}

private val LightColors = lightColorScheme(
    primary = AppColors.PrimaryColor,
    onPrimary = AppColors.White,
    secondary = Color(0xFF9D4EDD),
    background = AppColors.BackgroundColor,
    surface = AppColors.White,
    error = AppColors.ErrorColor
)

private val DarkColors = darkColorScheme(
    primary = AppColors.PrimaryColor,
    onPrimary = AppColors.White,
    secondary = Color(0xFF9D4EDD),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

@Composable
fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = AppColors.White,
        unfocusedContainerColor = AppColors.White,
        focusedIndicatorColor = AppColors.PrimaryColor,
        unfocusedIndicatorColor = AppColors.PrimaryColor,
        focusedLabelColor = AppColors.PrimaryColor,
        unfocusedLabelColor = AppColors.PrimaryColor,
        focusedTextColor = AppColors.TextColor,
        unfocusedTextColor = AppColors.TextColor,
        cursorColor = AppColors.TextColor
    )
}