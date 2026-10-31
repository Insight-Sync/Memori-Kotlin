package com.memori.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.memori.app.R

val PangolinFamily = FontFamily(
    Font(R.font.pangolin_regular, FontWeight.Normal)
)

private val base = Typography()

val Typography = Typography(
    displayLarge   = base.displayLarge.copy(fontFamily = PangolinFamily),
    displayMedium  = base.displayMedium.copy(fontFamily = PangolinFamily),
    displaySmall   = base.displaySmall.copy(fontFamily = PangolinFamily),
    headlineLarge  = base.headlineLarge.copy(fontFamily = PangolinFamily),
    headlineMedium = base.headlineMedium.copy(fontFamily = PangolinFamily),
    headlineSmall  = base.headlineSmall.copy(fontFamily = PangolinFamily),
    titleLarge     = base.titleLarge.copy(fontFamily = PangolinFamily),
    titleMedium    = base.titleMedium.copy(fontFamily = PangolinFamily),
    titleSmall     = base.titleSmall.copy(fontFamily = PangolinFamily),
    bodyLarge      = base.bodyLarge.copy(fontFamily = PangolinFamily),
    bodyMedium     = base.bodyMedium.copy(fontFamily = PangolinFamily),
    bodySmall      = base.bodySmall.copy(fontFamily = PangolinFamily),
    labelLarge     = base.labelLarge.copy(fontFamily = PangolinFamily),
    labelMedium    = base.labelMedium.copy(fontFamily = PangolinFamily),
    labelSmall     = base.labelSmall.copy(fontFamily = PangolinFamily),
)