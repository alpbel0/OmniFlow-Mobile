package com.omniflow.ui.settings

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.omniflow.R
import com.omniflow.core.designsystem.theme.SettingsPalette

sealed class SettingRowType {
    object Navigate : SettingRowType()
    data class ValueLabel(val value: String) : SettingRowType()
    data class DropdownHint(val value: String) : SettingRowType()
}

data class SettingRow(
    val id: String,
    val iconRes: Int,
    val label: String,
    val iconBgColor: Color,
    val iconTintColor: Color,
    val type: SettingRowType = SettingRowType.Navigate,
)

data class SettingGroup(
    val title: String,
    val rows: List<SettingRow>,
)

@Immutable
data class SettingsUiState(
    val groups: List<SettingGroup> = defaultSettingGroups,
    val isLoggingOut: Boolean = false,
    val loggedOut: Boolean = false,
)

val defaultSettingGroups = listOf(
    SettingGroup(
        title = "HESAP",
        rows = listOf(
            SettingRow("account_info", R.drawable.ic_person, "Hesap Bilgileri", SettingsPalette.bgAccount, SettingsPalette.tintAccount),
            SettingRow("change_pass", R.drawable.ic_lock, "Şifre Değiştir", SettingsPalette.bgLock, SettingsPalette.tintLock),
            SettingRow("email_prefs", R.drawable.ic_mail, "Email Tercihleri", SettingsPalette.bgMail, SettingsPalette.tintMail),
        ),
    ),
    SettingGroup(
        title = "UYGULAMA",
        rows = listOf(
            SettingRow("notifications", R.drawable.ic_bell, "Bildirim Tercihleri", SettingsPalette.bgBell, SettingsPalette.tintBell),
            SettingRow(
                "theme", R.drawable.ic_moon, "Tema", SettingsPalette.bgMoon, SettingsPalette.tintMoon,
                SettingRowType.DropdownHint("Sistem"),
            ),
            SettingRow(
                "language", R.drawable.ic_globe, "Dil", SettingsPalette.bgGlobe, SettingsPalette.tintGlobe,
                SettingRowType.ValueLabel("Türkçe"),
            ),
        ),
    ),
    SettingGroup(
        title = "GİZLİLİK & GÜVENLİK",
        rows = listOf(
            SettingRow("privacy", R.drawable.ic_shield, "Gizlilik Ayarları", SettingsPalette.bgShield, SettingsPalette.tintShield),
            SettingRow("blocked", R.drawable.ic_block, "Engellenen Kullanıcılar", SettingsPalette.bgBlock, SettingsPalette.tintBlock),
            SettingRow("data", R.drawable.ic_data, "Veri & Hesap", SettingsPalette.bgData, SettingsPalette.tintData),
        ),
    ),
    SettingGroup(
        title = "DESTEK",
        rows = listOf(
            SettingRow("help", R.drawable.ic_help, "Yardım Merkezi", SettingsPalette.bgHelp, SettingsPalette.tintHelp),
            SettingRow("feedback", R.drawable.ic_feedback, "Geri Bildirim Gönder", SettingsPalette.bgFeedback, SettingsPalette.tintFeedback),
            SettingRow(
                "about", R.drawable.ic_info, "Hakkında", SettingsPalette.bgInfo, SettingsPalette.tintInfo,
                SettingRowType.ValueLabel("Sürüm 1.0.0"),
            ),
        ),
    ),
)
