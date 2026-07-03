package com.omniflow.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.omniflow.R

// plus_jakarta_sans.ttf tek bir VARIABLE font dosyasıdır. Her ağırlık için
// "wght" eksenini açıkça vermezsek Compose hep aynı (regular) instance'ı kullanıp
// Bold/SemiBold'u sahte (algoritmik) kalınlaştırır → Figma'daki gerçek ağırlıktan farklı görünür.
@OptIn(ExperimentalTextApi::class)
private fun jakarta(weight: Int) = Font(
    resId = R.font.plus_jakarta_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private val PlusJakartaSans = FontFamily(
    jakarta(400), // Normal
    jakarta(500), // Medium
    jakarta(600), // SemiBold
    jakarta(700), // Bold
)

val OmniFlowTypography = Typography(
    displaySmall = omniTextStyle(FontWeight.Bold, 36.sp, 44.sp),
    headlineLarge = omniTextStyle(FontWeight.Bold, 31.sp, 38.sp),
    headlineMedium = omniTextStyle(FontWeight.Bold, 28.sp, 36.sp),
    headlineSmall = omniTextStyle(FontWeight.SemiBold, 24.sp, 32.sp),
    titleLarge = omniTextStyle(FontWeight.SemiBold, 22.sp, 28.sp),
    titleMedium = omniTextStyle(FontWeight.SemiBold, 16.sp, 24.sp),
    titleSmall = omniTextStyle(FontWeight.SemiBold, 14.sp, 18.sp),
    bodyLarge = omniTextStyle(FontWeight.Normal, 16.sp, 24.sp),
    bodyMedium = omniTextStyle(FontWeight.Normal, 15.sp, 22.sp),
    bodySmall = omniTextStyle(FontWeight.Normal, 13.sp, 18.sp),
    labelLarge = omniTextStyle(FontWeight.SemiBold, 15.sp, 20.sp),
    labelMedium = omniTextStyle(FontWeight.Medium, 13.sp, 16.sp),
    labelSmall = omniTextStyle(FontWeight.Medium, 11.sp, 16.sp),
)

private fun omniTextStyle(
    fontWeight: FontWeight,
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit,
) = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
)

object OmniTextStyles {
    val greeting = omniTextStyle(FontWeight.SemiBold, 19.sp, 24.sp)
    val heroTitle = omniTextStyle(FontWeight.Bold, 20.sp, 26.sp)
    val statusLabel = omniTextStyle(FontWeight.SemiBold, 11.sp, 14.sp)
        .copy(letterSpacing = 0.8.sp)
    val captionTiny = omniTextStyle(FontWeight.Bold, 10.sp, 14.sp)
    val bodyXSmall = omniTextStyle(FontWeight.Normal, 12.sp, 16.sp)

    val profileHandle = omniTextStyle(FontWeight.SemiBold, 18.sp, 24.sp)
    val profileStat = omniTextStyle(FontWeight.Bold, 18.sp, 22.sp)
    val profileBlockedTitle = omniTextStyle(FontWeight.SemiBold, 15.sp, 20.sp)
    val profileChip = omniTextStyle(FontWeight.SemiBold, 13.sp, 16.sp)
    val profileLabel = omniTextStyle(FontWeight.Normal, 12.sp, 16.sp)
    val profileLabelBold = omniTextStyle(FontWeight.Bold, 12.sp, 14.sp)
    val profilePostBody = omniTextStyle(FontWeight.Normal, 14.sp, 20.sp)
    val sectionHeader = omniTextStyle(FontWeight.Bold, 11.sp, 14.sp)
        .copy(letterSpacing = 0.6.sp)
    val settingsSectionTitle = omniTextStyle(FontWeight.SemiBold, 12.sp, 16.sp)
    val tripCardTitle = omniTextStyle(FontWeight.Bold, 20.sp, 26.sp)
    val tripSavedTitle = omniTextStyle(FontWeight.Bold, 13.5.sp, 17.sp)
    val tripSavedUser = omniTextStyle(FontWeight.SemiBold, 10.5.sp, 14.sp)
    val tripFilterChip = omniTextStyle(FontWeight.SemiBold, 12.5.sp, 16.sp)
    val tripMetaSmall = omniTextStyle(FontWeight.Normal, 10.sp, 14.sp)
}
