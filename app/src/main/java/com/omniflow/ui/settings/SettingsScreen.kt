package com.omniflow.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omniflow.core.designsystem.theme.SettingsDimens
import com.omniflow.core.designsystem.theme.SettingsPalette
import com.omniflow.core.designsystem.theme.OmniTextStyles
import com.omniflow.R

@Composable
fun SettingsScreen(
    groups: List<SettingGroup> = defaultSettingGroups,
    paddingValues: PaddingValues = PaddingValues(),
    isLoggingOut: Boolean = false,
    onBack: () -> Unit = {},
    onRowClick: (String) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val p = SettingsPalette
    val d = SettingsDimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(p.bgScreen)
    ) {
        SettingsTopBar(onBack = onBack, palette = p, dimens = d)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = d.contentBottomPadding)
        ) {
            Spacer(Modifier.height(d.contentTopSpacer))

            groups.forEach { group ->
                SettingGroupCard(
                    group = group,
                    onRowClick = onRowClick,
                    palette = p,
                    dimens = d,
                    modifier = Modifier
                        .padding(horizontal = d.groupCardHPadding)
                        .padding(bottom = d.groupCardBottomSpacing),
                )
            }

            LogoutButton(
                onClick = onLogout,
                isLoggingOut = isLoggingOut,
                palette = p,
                dimens = d,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = d.logoutHPadding)
                    .padding(top = d.logoutTopPadding, bottom = d.logoutBottomPadding),
            )
        }
    }
}

@Composable
private fun SettingsTopBar(
    onBack: () -> Unit,
    palette: SettingsPalette,
    dimens: SettingsDimens,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.topBarHeight)
            .background(palette.surface)
            .border(dimens.topBarBorderWidth, palette.bgInput),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = dimens.topBarHPadding)
                .size(dimens.backButtonSize)
                .clip(CircleShape)
                .background(palette.bgInput)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Geri",
                tint = palette.textPrimary,
                modifier = Modifier.size(dimens.backButtonIconSize),
            )
        }
        Text(
            text = "Ayarlar",
            style = OmniTextStyles.profileHandle,
            color = palette.textPrimary,
        )
    }
}

@Composable
private fun SettingGroupCard(
    group: SettingGroup,
    onRowClick: (String) -> Unit,
    palette: SettingsPalette,
    dimens: SettingsDimens,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = group.title,
            style = OmniTextStyles.settingsSectionTitle,
            color = palette.textSecond,
            modifier = Modifier
                .padding(start = dimens.groupTitlePadStart, bottom = dimens.groupTitlePadBottom),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(dimens.cardShadow, RoundedCornerShape(dimens.cardRadius))
                .clip(RoundedCornerShape(dimens.cardRadius))
                .background(palette.surface),
        ) {
            group.rows.forEachIndexed { index, row ->
                SettingRowItem(
                    row = row,
                    onClick = { onRowClick(row.id) },
                    palette = palette,
                    dimens = dimens,
                )
                if (index < group.rows.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimens.dividerHeight)
                            .padding(start = dimens.dividerPaddingStart)
                            .background(palette.bgInput),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRowItem(
    row: SettingRow,
    onClick: () -> Unit,
    palette: SettingsPalette,
    dimens: SettingsDimens,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.rowHeight)
            .clickable(onClick = onClick)
            .padding(horizontal = dimens.rowHPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.rowSpacing),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.iconContainerSize)
                .clip(RoundedCornerShape(dimens.iconContainerRadius))
                .background(row.iconBgColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(row.iconRes),
                contentDescription = null,
                tint = row.iconTintColor,
                modifier = Modifier.size(dimens.iconSize),
            )
        }

        Text(
            text = row.label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = palette.textPrimary,
            modifier = Modifier.weight(1f),
        )

        when (val type = row.type) {
            is SettingRowType.Navigate -> {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = palette.border,
                    modifier = Modifier.size(dimens.chevronSize),
                )
            }
            is SettingRowType.ValueLabel -> {
                Text(
                    text = type.value,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecond,
                )
            }
            is SettingRowType.DropdownHint -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.dropdownHintSpacing),
                ) {
                    Text(
                        text = type.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecond,
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_down),
                        contentDescription = null,
                        tint = palette.textSecond,
                        modifier = Modifier.size(dimens.dropdownChevronSize),
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit,
    isLoggingOut: Boolean,
    palette: SettingsPalette,
    dimens: SettingsDimens,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(dimens.logoutButtonHeight)
            .clip(RoundedCornerShape(dimens.logoutButtonRadius))
            .border(dimens.logoutBorderWidth, palette.dangerRed, RoundedCornerShape(dimens.logoutButtonRadius))
            .background(palette.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoggingOut) {
            CircularProgressIndicator(
                color = palette.dangerRed,
                modifier = Modifier.size(dimens.iconSize),
                strokeWidth = dimens.logoutBorderWidth,
            )
        } else {
            Text(
                text = "Çıkış Yap",
                style = MaterialTheme.typography.labelLarge,
                color = palette.dangerRed,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewSettingsScreen() {
    SettingsScreen()
}
