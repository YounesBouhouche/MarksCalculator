package com.example.markscalculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.markscalculator.R

@OptIn(ExperimentalTextApi::class)
val ReadexPro = FontFamily(
    Font(R.font.readexpro, FontWeight.Normal, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.Normal.weight)
    )),
    Font(R.font.readexpro, FontWeight.SemiBold, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.SemiBold.weight)
    )),
    Font(R.font.readexpro, FontWeight.Medium, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.Medium.weight)
    )),
    Font(R.font.readexpro, FontWeight.Bold, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.Bold.weight)
    )),
    Font(R.font.readexpro, FontWeight.ExtraBold, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.ExtraBold.weight)
    )),
    Font(R.font.readexpro, FontWeight.Black, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.Black.weight)
    )),
    Font(R.font.readexpro, FontWeight.Light, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.Light.weight)
    )),
    Font(R.font.readexpro, FontWeight.ExtraLight, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.ExtraLight.weight)
    )),
    Font(R.font.readexpro, FontWeight.Thin, variationSettings = FontVariation.Settings(
        FontVariation.weight(FontWeight.Thin.weight)
    ))
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.0125.em
    ),
    bodyMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)