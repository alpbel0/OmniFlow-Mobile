package com.omniflow.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.omniflow.R
import com.omniflow.core.common.UiState
import com.omniflow.core.designsystem.theme.FollowListDimens
import com.omniflow.core.designsystem.theme.FollowListPalette
import com.omniflow.core.designsystem.theme.OmniTextStyles
import com.omniflow.core.designsystem.theme.ProfilePalette

// ─────────────────────────────────────────
// FollowListScreen — root composable
// ─────────────────────────────────────────
@Composable
fun FollowListScreen(
    uiState: FollowListUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {},
    onSearchChange: (String) -> Unit = {},
    onFollowTap: (FollowUserUiModel) -> Unit = {},
    onUnfollowTap: (FollowUserUiModel) -> Unit = {},
    onUserTap: (String) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val d = FollowListDimens
    val p = FollowListPalette

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(p.bgScreen)
    ) {
        when (val content = uiState.contentState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = p.primary)
                }
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_error),
                        contentDescription = null,
                        tint = p.textSecondary,
                        modifier = Modifier.size(d.errorIconSize),
                    )
                    Spacer(Modifier.height(d.errorSpacer16))
                    Text(
                        text = "Bir hata oluştu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = p.textPrimary,
                    )
                    Spacer(Modifier.height(d.errorSpacer8))
                    TextButton(onClick = onRetry) {
                        Text("Tekrar Dene", color = p.primary)
                    }
                }
            }

            is UiState.Success, is UiState.Empty -> {
                val users = (content as? UiState.Success)?.data ?: emptyList()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    FollowListTopBar(
                        mode = uiState.mode,
                        totalCount = uiState.totalCount,
                        onBack = onBack,
                    )

                    FollowSearchBar(
                        query = uiState.searchQuery,
                        hint = if (uiState.mode == FollowListMode.FOLLOWERS)
                            "Takipçilerde ara..." else "Takip edilenlerde ara...",
                        onQueryChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = d.searchBarHPaddingOuter, vertical = d.searchBarVPadding),
                    )

                    if (users.isEmpty()) {
                        FollowEmptyState(
                            mode = uiState.mode,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) {
                            items(users, key = { it.id }) { user ->
                                FollowUserRow(
                                    user = user,
                                    isFollowLoading = user.id in uiState.followLoadingIds,
                                    onFollowTap = { onFollowTap(user) },
                                    onUnfollowTap = { onUnfollowTap(user) },
                                    onRowTap = { onUserTap(user.username) },
                                )
                            }
                            item { Spacer(Modifier.height(d.listBottomSpacer)) }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────
@Composable
private fun FollowListTopBar(
    mode: FollowListMode,
    totalCount: Int,
    onBack: () -> Unit,
) {
    val d = FollowListDimens
    val p = FollowListPalette

    val title = when (mode) {
        FollowListMode.FOLLOWERS -> "Takipçiler · $totalCount"
        FollowListMode.FOLLOWING -> "Takip Edilenler · $totalCount"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(d.topBarHeight)
            .background(p.surface)
            .border(d.dividerHeight, p.bgInput),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = d.topBarHPadding)
                .size(d.backButtonSize)
                .clip(CircleShape)
                .background(p.bgInput)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Geri",
                tint = p.textPrimary,
                modifier = Modifier.size(d.backIconSize),
            )
        }

        Text(
            text = title,
            style = OmniTextStyles.profileHandle,
            color = p.textPrimary,
        )
    }
}

// ─────────────────────────────────────────
// Search Bar
// ─────────────────────────────────────────
@Composable
private fun FollowSearchBar(
    query: String,
    hint: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = FollowListDimens
    val p = FollowListPalette

    Row(
        modifier = modifier
            .height(d.searchBarHeight)
            .clip(RoundedCornerShape(d.searchBarRadius))
            .background(p.bgInput)
            .padding(horizontal = d.searchBarHPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.searchRowSpacing),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = p.textSecondary,
            modifier = Modifier.size(d.searchIconSize),
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleSmall.copy(color = p.textPrimary),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.titleSmall,
                        color = p.textSecondary,
                    )
                }
                inner()
            },
        )
        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(d.searchClearSize)
                    .clip(CircleShape)
                    .background(p.textSecondary.copy(alpha = p.clearAlpha))
                    .clickable { onQueryChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Temizle",
                    tint = p.textSecondary,
                    modifier = Modifier.size(d.searchClearIconSize),
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// User Row
// ─────────────────────────────────────────
@Composable
private fun FollowUserRow(
    user: FollowUserUiModel,
    isFollowLoading: Boolean,
    onFollowTap: () -> Unit,
    onUnfollowTap: () -> Unit,
    onRowTap: () -> Unit,
) {
    val d = FollowListDimens
    val p = FollowListPalette

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(d.userRowHeight)
                .background(p.surface)
                .clickable(onClick = onRowTap)
                .padding(horizontal = d.userRowHPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.userRowSpacing),
        ) {
            Box(
                modifier = Modifier
                    .size(d.userAvatarSize)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = user.avatarColors)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user.avatarInitial,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(d.userHandleKarmaSpacing),
            ) {
                Text(
                    text = user.handle,
                    style = OmniTextStyles.profileBlockedTitle,
                    color = p.textPrimary,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.userKarmaIconSpacing),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = p.starAmber,
                        modifier = Modifier.size(d.userStarIconSize),
                    )
                    Text(
                        text = "${user.karma} karma",
                        style = OmniTextStyles.profileLabel,
                        color = p.textSecondary,
                    )
                }
            }

            if (user.isFollowing) {
                Box(
                    modifier = Modifier
                        .width(d.followingButtonWidth)
                        .height(d.followButtonHeight)
                        .clip(RoundedCornerShape(d.followButtonRadius))
                        .border(d.followButtonBorder, p.border, RoundedCornerShape(d.followButtonRadius))
                        .background(p.surface)
                        .then(if (isFollowLoading) Modifier else Modifier.clickable(onClick = onUnfollowTap)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isFollowLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(d.loadingIndicatorSize),
                            strokeWidth = d.loadingIndicatorStroke,
                            color = p.textSecondary,
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(d.followButtonCheckSpacing),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                tint = p.textSecondary,
                                modifier = Modifier.size(d.followButtonCheckIconSize),
                            )
                            Text(
                                text = "Takip Ediyorsun",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = p.textSecondary,
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(d.followButtonWidth)
                        .height(d.followButtonHeight)
                        .clip(RoundedCornerShape(d.followButtonRadius))
                        .border(d.followButtonBorderBlue, p.primary, RoundedCornerShape(d.followButtonRadius))
                        .background(p.surface)
                        .then(if (isFollowLoading) Modifier else Modifier.clickable(onClick = onFollowTap)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isFollowLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(d.loadingIndicatorSize),
                            strokeWidth = d.loadingIndicatorStroke,
                            color = p.primary,
                        )
                    } else {
                        Text(
                            text = "Takip Et",
                            style = OmniTextStyles.profileLabel.copy(fontWeight = FontWeight.SemiBold),
                            color = p.primary,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(d.dividerHeight)
                .background(p.bgInput),
        )
    }
}

// ─────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────
@Composable
private fun FollowEmptyState(
    mode: FollowListMode,
    modifier: Modifier = Modifier,
) {
    val d = FollowListDimens
    val p = FollowListPalette

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(d.emptyIconContainer)
                .clip(CircleShape)
                .background(p.bgInput),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_community),
                contentDescription = null,
                tint = p.border,
                modifier = Modifier.size(d.emptyIconSize),
            )
        }

        Spacer(Modifier.height(d.emptySpacer16))

        Text(
            text = when (mode) {
                FollowListMode.FOLLOWERS -> "Henüz takipçin yok"
                FollowListMode.FOLLOWING -> "Henüz kimseyi takip etmiyorsun"
            },
            style = OmniTextStyles.profileBlockedTitle,
            color = p.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(d.emptySpacer8))

        Text(
            text = when (mode) {
                FollowListMode.FOLLOWERS -> "Gezi paylaş ve gezginlerle\nbağlantı kur!"
                FollowListMode.FOLLOWING -> "Keşfet sayfasından gezginleri bul."
            },
            style = MaterialTheme.typography.bodySmall,
            color = p.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = d.emptyLineHeight,
        )

        if (mode == FollowListMode.FOLLOWING) {
            Spacer(Modifier.height(d.emptySpacer20))
            Box(
                modifier = Modifier
                    .width(d.emptyCtaWidth)
                    .height(d.emptyCtaHeight)
                    .clip(RoundedCornerShape(d.emptyCtaRadius))
                    .background(p.primary)
                    .clickable { /* TODO: navigate to Explore */ },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Keşfet'e Git",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// Previews
// ─────────────────────────────────────────
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewFollowersList() {
    val uiState = FollowListUiState(
        contentState = UiState.Success(samplePreviewUsers),
        mode = FollowListMode.FOLLOWERS,
        totalCount = 284,
    )
    FollowListScreen(uiState = uiState)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewFollowingList() {
    val uiState = FollowListUiState(
        contentState = UiState.Success(samplePreviewUsers.take(3)),
        mode = FollowListMode.FOLLOWING,
        totalCount = 3,
    )
    FollowListScreen(uiState = uiState)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewFollowingEmpty() {
    val uiState = FollowListUiState(
        contentState = UiState.Success(emptyList()),
        mode = FollowListMode.FOLLOWING,
        totalCount = 0,
    )
    FollowListScreen(uiState = uiState)
}

private val samplePreviewUsers = listOf(
    FollowUserUiModel("1", "selin.k", "@selin.k", 2340, "S", ProfilePalette.postAvatarGradients[0], isFollowing = true),
    FollowUserUiModel("2", "alptravel", "@alptravel", 3840, "A", ProfilePalette.postAvatarGradients[1], isFollowing = false),
    FollowUserUiModel("3", "mert.y", "@mert.y", 890, "M", ProfilePalette.postAvatarGradients[2], isFollowing = true),
    FollowUserUiModel("4", "ece.world", "@ece.world", 1120, "E", ProfilePalette.postAvatarGradients[3], isFollowing = false),
    FollowUserUiModel("5", "can.exp", "@can.exp", 560, "C", ProfilePalette.postAvatarGradients[4], isFollowing = false),
    FollowUserUiModel("6", "zeynep.t", "@zeynep.t", 1780, "Z", ProfilePalette.postAvatarGradients[0], isFollowing = true),
)
