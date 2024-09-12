package com.example.umbrella.ui.theme.utils

import androidx.compose.material3.CardColors
import com.example.umbrella.ui.theme.ExtendedColorScheme

fun getCardColors(uvi: Double, extendedColorScheme: ExtendedColorScheme): CardColors {
    return when {
        uvi <= 2 -> CardColors(
            containerColor = extendedColorScheme.lowRisk.colorContainer,
            contentColor = extendedColorScheme.lowRisk.onColorContainer,
            disabledContainerColor = extendedColorScheme.lowRisk.colorContainer,
            disabledContentColor = extendedColorScheme.lowRisk.onColorContainer
        )

        uvi <= 5 -> CardColors(
            containerColor = extendedColorScheme.moderateRisk.colorContainer,
            contentColor = extendedColorScheme.moderateRisk.onColorContainer,
            disabledContainerColor = extendedColorScheme.moderateRisk.colorContainer,
            disabledContentColor = extendedColorScheme.moderateRisk.onColorContainer
        )

        uvi <= 7 -> CardColors(
            containerColor = extendedColorScheme.highRisk.colorContainer,
            contentColor = extendedColorScheme.highRisk.onColorContainer,
            disabledContainerColor = extendedColorScheme.highRisk.colorContainer,
            disabledContentColor = extendedColorScheme.highRisk.onColorContainer
        )

        uvi <= 10 -> CardColors(
            containerColor = extendedColorScheme.veryHighRisk.colorContainer,
            contentColor = extendedColorScheme.veryHighRisk.onColorContainer,
            disabledContainerColor = extendedColorScheme.veryHighRisk.colorContainer,
            disabledContentColor = extendedColorScheme.veryHighRisk.onColorContainer
        )

        else -> CardColors(
            containerColor = extendedColorScheme.veryHighRisk.colorContainer,
            contentColor = extendedColorScheme.veryHighRisk.onColorContainer,
            disabledContainerColor = extendedColorScheme.veryHighRisk.colorContainer,
            disabledContentColor = extendedColorScheme.veryHighRisk.onColorContainer
        )
    }
}