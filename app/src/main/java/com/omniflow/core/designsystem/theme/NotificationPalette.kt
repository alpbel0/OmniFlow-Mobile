package com.omniflow.core.designsystem.theme

import androidx.compose.ui.graphics.Color

object NotificationPalette {
    val unreadBg = Color(0xFFEEF5FF)
    val selectedBg = Color(0xFFD6E8FF)
    val topBarBorderColor = Color(0xFFF0F4F8)
    val rowDividerColor = Color(0xFFF0F4F8)
    val darkBar = Color(0xFF102033)

    val reminderIconBg = Color(0xFFFFF8E1)
    val reminderIconTint = Color(0xFFF59E0B)
    val likeIconBg = Color(0xFFFFF0F0)
    val likeIconTint = Color(0xFFEF4444)

    val followAvatarBg = Color(0xFFE2F1FF)

    val notificationThumbGradients = listOf(
        listOf(Color(0xFF1565C0), Color(0xFF42A5F5)),
        listOf(Color(0xFF4A148C), Color(0xFFAB47BC)),
        listOf(Color(0xFF0D3B6E), Color(0xFF1976D2)),
        listOf(Color(0xFF004D40), Color(0xFF26A69A)),
        listOf(Color(0xFFBF360C), Color(0xFFFF8A65)),
    )

    val followAvatarGradients = listOf(
        listOf(Color(0xFF007BFF), Color(0xFF4DB3FF)),
        listOf(Color(0xFF7B1FA2), Color(0xFFCE93D8)),
        listOf(Color(0xFFE65100), Color(0xFFFFB74D)),
        listOf(Color(0xFF2E7D32), Color(0xFF81C784)),
        listOf(Color(0xFFC62828), Color(0xFFEF9A9A)),
    )
}
