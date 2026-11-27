/*
 * Copyright (C) 2026 RevampedFMRadio Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.fmradio.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.io.File

private val googleSansFlexFile: File? by lazy {
    listOf(
        "/product/fonts/GoogleSansFlex-Regular.ttf",
        "/system/fonts/GoogleSansFlex-Regular.ttf"
    ).map(::File).firstOrNull { it.isFile && it.canRead() }
}

@OptIn(ExperimentalTextApi::class)
fun createGoogleSansFlexFont(
    wght: Float,
    wdth: Float = 100f,
    slnt: Float = 0f,
    rond: Float = 0f,
    opsz: Float = 18f,
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal
): Font {
    val variations = FontVariation.Settings(
        FontVariation.Setting("wght", wght.coerceIn(1f, 1000f)),
        FontVariation.Setting("wdth", wdth.coerceIn(25f, 151f)),
        FontVariation.Setting("slnt", slnt.coerceIn(-10f, 0f)),
        FontVariation.Setting("ROND", rond.coerceIn(0f, 100f)),
        FontVariation.Setting("opsz", opsz.coerceIn(6f, 144f))
    )
    val file = googleSansFlexFile
    return if (file != null) {
        Font(
            file = file,
            weight = weight,
            style = style,
            variationSettings = variations
        )
    } else {
        Font(
            familyName = DeviceFontFamilyName("google-sans-flex"),
            weight = weight,
            style = style,
            variationSettings = variations
        )
    }
}

fun createGoogleSansFlexFamily(
    wght: Float,
    wdth: Float = 100f,
    slnt: Float = 0f,
    rond: Float = 0f,
    opsz: Float = 18f,
    weight: FontWeight = FontWeight.Normal
): FontFamily = FontFamily(
    createGoogleSansFlexFont(
        wght = wght,
        wdth = wdth,
        slnt = slnt,
        rond = rond,
        opsz = opsz,
        weight = weight
    )
)

val GoogleSansFlexTitle = FontFamily(
    createGoogleSansFlexFont(wght = 700f, rond = 100f, opsz = 32f, weight = FontWeight.Bold),
    createGoogleSansFlexFont(wght = 600f, rond = 100f, opsz = 24f, weight = FontWeight.SemiBold),
    createGoogleSansFlexFont(wght = 500f, rond = 50f, opsz = 18f, weight = FontWeight.Medium)
)

val GoogleSansFlexBody = FontFamily(
    createGoogleSansFlexFont(wght = 400f, rond = 0f, opsz = 16f, weight = FontWeight.Normal),
    createGoogleSansFlexFont(wght = 500f, rond = 0f, opsz = 14f, weight = FontWeight.Medium),
    createGoogleSansFlexFont(wght = 600f, rond = 0f, opsz = 14f, weight = FontWeight.SemiBold)
)

val RevampedTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp
    ),
    displayMedium = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = GoogleSansFlexTitle,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GoogleSansFlexBody,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GoogleSansFlexBody,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = GoogleSansFlexBody,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GoogleSansFlexBody,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = GoogleSansFlexBody,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSansFlexBody,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

@Composable
fun ForceGoogleSansFlexTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = RevampedTypography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

@Composable
fun RevampedFmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = if (darkTheme) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RevampedTypography,
        content = content
    )
}
