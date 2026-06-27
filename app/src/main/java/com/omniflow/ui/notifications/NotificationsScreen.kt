package com.omniflow.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import com.omniflow.R
import com.omniflow.core.common.UiState
import com.omniflow.core.common.asString
import com.omniflow.core.designsystem.theme.NotificationDimens
import com.omniflow.core.designsystem.theme.NotificationPalette
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.uicomponents.EmptyState
import com.omniflow.uicomponents.ErrorView
import com.omniflow.uicomponents.LoadingIndicator

@Composable
fun NotificationsScreen(
    uiState: NotificationsUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {},
    onFilterChange: (NotifFilter) -> Unit = {},
    onNotifClick: (NotificationUiItem) -> Unit = {},
    onNotifLongPress: () -> Unit = {},
    onToggleSelect: (String) -> Unit = {},
    onMarkReadSelected: () -> Unit = {},
    onSelectAll: () -> Unit = {},
    onExitSelectMode: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val d = NotificationDimens

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState.isSelectMode) {
                SelectModeBar(
                    selectedCount = uiState.selectedIds.size,
                    onExit = onExitSelectMode,
                    onMarkRead = onMarkReadSelected,
                    onSelectAll = onSelectAll,
                )
            } else {
                NormalTopBar(onBack = onBack)
            }

            FilterChipsRow(
                activeFilter = uiState.activeFilter,
                onFilterChange = onFilterChange,
            )

            when (val contentState = uiState.contentState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Empty -> {
                    EmptyState(
                        title = "Henüz bildirim yok",
                        description = "Yeni etkileşimler burada görünecek",
                    )
                }
                is UiState.Error -> {
                    ErrorView(
                        message = contentState.message.asString(),
                        onRetry = onRetry,
                    )
                }
                is UiState.Success -> {
                    if (contentState.data.isEmpty()) {
                        EmptyState(
                            title = "Bu kategoride bildirim yok",
                            description = "Farklı bir filtre deneyin",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = d.listBottomPadding),
                        ) {
                            items(contentState.data, key = { it.id }) { item ->
                                NotifRow(
                                    item = item,
                                    isSelectMode = uiState.isSelectMode,
                                    isSelected = item.id in uiState.selectedIds,
                                    onClick = {
                                        if (uiState.isSelectMode) onToggleSelect(item.id)
                                        else onNotifClick(item)
                                    },
                                    onLongPress = onNotifLongPress,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NormalTopBar(onBack: () -> Unit) {
    val d = NotificationDimens
    val tokens = OmniTokens

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(d.topBarHeight)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = tokens.dimens.hairline,
                color = NotificationPalette.topBarBorderColor,
                shape = RoundedCornerShape(OmniTokens.spacing.none),
            )
            .padding(horizontal = d.topBarHPadding),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(d.backButtonSize)
                .clip(CircleShape)
                .background(tokens.colors.fieldInputBackground)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Geri",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(d.backButtonIconSize),
            )
        }

        Text(
            text = "Bildirimler",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun SelectModeBar(
    selectedCount: Int,
    onExit: () -> Unit,
    onMarkRead: () -> Unit,
    onSelectAll: () -> Unit,
) {
    val d = NotificationDimens
    val s = OmniTokens.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(d.selectBarHeight)
            .background(NotificationPalette.darkBar)
            .padding(horizontal = d.topBarHPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.selectBarSpacing),
    ) {
        Box(
            modifier = Modifier
                .size(d.closeBtnSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .clickable(onClick = onExit),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "İptal",
                tint = Color.White,
                modifier = Modifier.size(d.closeIconSize),
            )
        }

        Text(
            text = "$selectedCount seçildi",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "Okundu",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            modifier = Modifier
                .clickable(onClick = onMarkRead)
                .padding(d.markReadPadding),
        )

        Box(
            modifier = Modifier
                .size(d.deleteBtnSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .clickable(onClick = { /* TODO: Backend delete endpoint pending */ }),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = "Sil",
                tint = Color.White,
                modifier = Modifier.size(d.deleteIconSize),
            )
        }

        Text(
            text = "Tümünü seç",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            modifier = Modifier
                .clickable(onClick = onSelectAll)
                .padding(d.markReadPadding),
        )
    }
}

@Composable
private fun FilterChipsRow(
    activeFilter: NotifFilter,
    onFilterChange: (NotifFilter) -> Unit,
) {
    val d = NotificationDimens

    val filters = listOf(
        NotifFilter.ALL to "Tümü",
        NotifFilter.SOCIAL to "Sosyal",
        NotifFilter.TRIP to "Trip",
        NotifFilter.REMINDER to "Hatırlatıcı",
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = d.filterRowVPadding),
        contentPadding = PaddingValues(horizontal = d.filterRowHPadding),
        horizontalArrangement = Arrangement.spacedBy(d.filterRowSpacing),
    ) {
        items(filters) { (filter, label) ->
            val isActive = filter == activeFilter
            Box(
                modifier = Modifier
                    .height(d.filterChipHeight)
                    .clip(RoundedCornerShape(d.filterChipRadius))
                    .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                    .border(
                        width = OmniTokens.dimens.hairline,
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(d.filterChipRadius),
                    )
                    .clickable { onFilterChange(filter) }
                    .padding(horizontal = d.filterChipHPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isActive) Color.White else MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotifRow(
    item: NotificationUiItem,
    isSelectMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
) {
    val d = NotificationDimens
    val tokens = OmniTokens

    val rowBg = when {
        isSelected -> NotificationPalette.selectedBg
        !item.isRead -> NotificationPalette.unreadBg
        else -> MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .combinedClickable(onClick = onClick, onLongClick = onLongPress),
    ) {
        if (!item.isRead && !isSelectMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(d.unreadIndicatorWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = d.rowHPadding, vertical = d.rowVPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.rowSpacing),
        ) {
            if (isSelectMode) {
                Box(
                    modifier = Modifier
                        .size(d.checkboxSize)
                        .clip(RoundedCornerShape(d.checkboxRadius))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .border(
                            width = d.checkboxBorderWidth,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(d.checkboxRadius),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(d.checkboxIconSize),
                        )
                    }
                }
            }

            NotifTypeIcon(type = item.type)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(d.textSpacing),
            ) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (!item.isRead) FontWeight.SemiBold else FontWeight.Normal,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            if (item.type == NotifTypeUi.FOLLOW && item.avatarInitial != null) {
                Box(
                    modifier = Modifier
                        .size(d.avatarSize)
                        .clip(CircleShape)
                        .background(NotificationPalette.followAvatarBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.avatarInitial,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            } else if (item.thumbColors != null) {
                Box(
                    modifier = Modifier
                        .size(d.thumbSize)
                        .clip(RoundedCornerShape(d.thumbRadius))
                        .background(Brush.linearGradient(colors = item.thumbColors)),
                )
            } else {
                Spacer(modifier = Modifier.size(d.thumbSize))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(d.dividerHeight)
                .background(NotificationPalette.rowDividerColor),
        )
    }
}

@Composable
private fun NotifTypeIcon(type: NotifTypeUi) {
    val d = NotificationDimens
    val p = NotificationPalette

    val (bgColor, iconRes) = when (type) {
        NotifTypeUi.FOLLOW -> MaterialTheme.colorScheme.primary to R.drawable.ic_person
        NotifTypeUi.COMMENT -> MaterialTheme.colorScheme.primary to R.drawable.ic_comment
        NotifTypeUi.UPVOTE -> OmniTokens.colors.fieldInputBackground to R.drawable.ic_upvote
        NotifTypeUi.FORK -> OmniTokens.colors.fieldInputBackground to R.drawable.ic_fork
        NotifTypeUi.REMINDER -> p.reminderIconBg to R.drawable.ic_calendar
        NotifTypeUi.LIKE -> p.likeIconBg to R.drawable.ic_heart
    }
    val iconTint = when (type) {
        NotifTypeUi.FOLLOW, NotifTypeUi.COMMENT -> Color.White
        NotifTypeUi.UPVOTE, NotifTypeUi.FORK -> MaterialTheme.colorScheme.secondary
        NotifTypeUi.REMINDER -> p.reminderIconTint
        NotifTypeUi.LIKE -> p.likeIconTint
    }

    Box(
        modifier = Modifier
            .size(d.typeIconContainer)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(d.typeIconSize),
        )
    }
}

// ─────────────────────────────────────────
// Preview Sample Data
// ─────────────────────────────────────────
private val sampleNotifications = listOf(
    NotificationUiItem(
        id = "1",
        type = NotifTypeUi.FOLLOW,
        text = "@ali seni takip etmeye başladı",
        timeAgo = "2 dk önce",
        isRead = false,
        avatarInitial = "A",
        avatarColors = NotificationPalette.followAvatarGradients[0],
        thumbColors = null,
    ),
    NotificationUiItem(
        id = "2",
        type = NotifTypeUi.COMMENT,
        text = "@selin yorumladı: \"Harika bir plan!\"",
        timeAgo = "5 dk önce",
        isRead = false,
        avatarInitial = null,
        avatarColors = null,
        thumbColors = NotificationPalette.notificationThumbGradients[0],
    ),
    NotificationUiItem(
        id = "3",
        type = NotifTypeUi.UPVOTE,
        text = "@mert gezini beğendi",
        timeAgo = "1 saat önce",
        isRead = true,
        avatarInitial = null,
        avatarColors = null,
        thumbColors = NotificationPalette.notificationThumbGradients[1],
    ),
    NotificationUiItem(
        id = "4",
        type = NotifTypeUi.FORK,
        text = "@can gezini kopyaladı",
        timeAgo = "3 saat önce",
        isRead = true,
        avatarInitial = null,
        avatarColors = null,
        thumbColors = NotificationPalette.notificationThumbGradients[2],
    ),
    NotificationUiItem(
        id = "5",
        type = NotifTypeUi.REMINDER,
        text = "Roma gezin 3 gün sonra başlıyor!",
        timeAgo = "Bugün, 09:00",
        isRead = true,
        avatarInitial = null,
        avatarColors = null,
        thumbColors = null,
    ),
    NotificationUiItem(
        id = "6",
        type = NotifTypeUi.LIKE,
        text = "@zeynep ve 3 kişi daha gezini beğendi",
        timeAgo = "Dün, 18:32",
        isRead = true,
        avatarInitial = null,
        avatarColors = null,
        thumbColors = NotificationPalette.notificationThumbGradients[3],
    ),
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewNotificationsNormal() {
    MaterialTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(
                contentState = UiState.Success(sampleNotifications),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewNotificationsSelectMode() {
    MaterialTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(
                contentState = UiState.Success(sampleNotifications),
                isSelectMode = true,
                selectedIds = setOf("1"),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewNotificationsEmpty() {
    MaterialTheme {
        NotificationsScreen(
            uiState = NotificationsUiState(contentState = UiState.Empty),
        )
    }
}
