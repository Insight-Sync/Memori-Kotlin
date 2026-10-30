package com.memori.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.memori.app.R

val Pangolin = FontFamily(
    Font(
        resId = R.font.pangolin_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    )
)

val Manjari = FontFamily(
    Font(
        resId = R.font.manjari_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    )
)

private val TipografiaBase = Typography()

private fun TextStyle.comFonteMemori(): TextStyle {
    return copy(
        fontFamily = Pangolin,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )
}

val Typography = Typography(
    displayLarge = TipografiaBase.displayLarge.comFonteMemori(),
    displayMedium = TipografiaBase.displayMedium.comFonteMemori(),
    displaySmall = TipografiaBase.displaySmall.comFonteMemori(),
    headlineLarge = TipografiaBase.headlineLarge.comFonteMemori(),
    headlineMedium = TipografiaBase.headlineMedium.comFonteMemori(),
    headlineSmall = TipografiaBase.headlineSmall.comFonteMemori(),
    titleLarge = TipografiaBase.titleLarge.comFonteMemori(),
    titleMedium = TipografiaBase.titleMedium.comFonteMemori(),
    titleSmall = TipografiaBase.titleSmall.comFonteMemori(),
    bodyLarge = TipografiaBase.bodyLarge.comFonteMemori(),
    bodyMedium = TipografiaBase.bodyMedium.comFonteMemori(),
    bodySmall = TipografiaBase.bodySmall.comFonteMemori(),
    labelLarge = TipografiaBase.labelLarge.comFonteMemori(),
    labelMedium = TipografiaBase.labelMedium.comFonteMemori(),
    labelSmall = TipografiaBase.labelSmall.comFonteMemori()
)