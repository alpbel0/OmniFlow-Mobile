package com.omniflow.core.designsystem.theme

import androidx.compose.ui.graphics.Color

object PublicProfilePalette {
    val bgScreen = Color(0xFFF5F7F8)
    val primary = Color(0xFF007BFF)
    val textPrimary = Color(0xFF102033)
    val textSecondary = Color(0xFF6F7F95)
    val surface = Color(0xFFFFFFFF)
    val border = Color(0xFFD9E2EC)
    val bgInput = Color(0xFFF0F4F8)
    val dangerRed = Color(0xFFFF3B30)
    val starAmber = Color(0xFFF59E0B)

    val blockedAvatarBg = Color(0xFFE8EEF5)
    val blockedAvatarTint = Color(0xFFB0BECA)
    val blockedTextAlpha = 0.3f
    val messageButtonAlpha = 0.5f
    val statsBgAlpha = 0.4f

    val ratingPillBg = Color.White.copy(alpha = 0.92f)
    val overlayBottomGradient = listOf(Color.Transparent, Color.Black.copy(alpha = 0.58f))

    val defaultAvatarGradient = listOf(Color(0xFF00695C), Color(0xFF26A69A))
    val defaultTripGradient1 = listOf(Color(0xFFBF360C), Color(0xFFFF7043))
    val defaultTripGradient2 = listOf(Color(0xFF880E4F), Color(0xFFE91E63))
}
