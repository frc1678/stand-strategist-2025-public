package org.citruscircuits.standstrategist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.citruscircuits.standstrategist.R

// Font styles
private val Inter =
    FontFamily(
        Font(R.font.inter_thin, weight = FontWeight.Thin),
        Font(R.font.inter_extralight, weight = FontWeight.ExtraLight),
        Font(R.font.inter_light, weight = FontWeight.Light),
        Font(R.font.inter_regular, weight = FontWeight.Normal),
        Font(R.font.inter_medium, weight = FontWeight.Medium),
        Font(R.font.inter_semibold, weight = FontWeight.SemiBold),
        Font(R.font.inter_bold, weight = FontWeight.Bold),
        Font(R.font.inter_extrabold, weight = FontWeight.ExtraBold),
        Font(R.font.inter_black, weight = FontWeight.Black)
    )

// Text sizes
val Typography =
    with(Typography()) {
        Typography(
            displayLarge = displayLarge.copy(fontFamily = Inter, fontWeight = FontWeight.Bold),
            displayMedium = displayMedium.copy(fontFamily = Inter, fontWeight = FontWeight.Bold),
            displaySmall = displaySmall.copy(fontFamily = Inter, fontWeight = FontWeight.Bold),
            headlineLarge = headlineLarge.copy(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
            headlineMedium = headlineMedium.copy(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
            headlineSmall = headlineSmall.copy(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
            titleLarge = titleLarge.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
            titleMedium = titleMedium.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
            titleSmall = titleSmall.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
            bodyLarge = bodyLarge.copy(fontFamily = Inter, fontWeight = FontWeight.Normal),
            bodyMedium = bodyMedium.copy(fontFamily = Inter, fontWeight = FontWeight.Normal),
            bodySmall = bodySmall.copy(fontFamily = Inter, fontWeight = FontWeight.Normal),
            labelLarge = labelLarge.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
            labelMedium = labelMedium.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
            labelSmall = labelSmall.copy(fontFamily = Inter, fontWeight = FontWeight.Medium)
        )
    }
