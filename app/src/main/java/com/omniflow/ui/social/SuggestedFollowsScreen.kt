package com.omniflow.ui.social

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.omniflow.R
import com.omniflow.core.common.UiState
import com.omniflow.core.designsystem.theme.CommunityDimens
import com.omniflow.core.designsystem.theme.CommunityPalette
import com.omniflow.core.designsystem.theme.OmniTextStyles
import com.omniflow.core.designsystem.theme.ProfilePalette

// ─────────────────────────────────────────
// CommunityDiscoveryScreen — root composable
// ─────────────────────────────────────────
@Composable
fun CommunityDiscoveryScreen(
    uiState: CommunityUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {},
    onFollowSuggested: (SuggestedUserUiModel) -> Unit = {},
    onFollowContrib: (ContributorUiModel) -> Unit = {},
    onUserTap: (String) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val d = CommunityDimens
    val p = CommunityPalette

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(p.bgScreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            DiscoveryTopBar(onBack = onBack)

            if (uiState.suggestedContent is UiState.Error || uiState.contributorsContent is UiState.Error) {
                Column(
                    modifier = Modifier.fillMaxSize(),
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
                    TextButton(onClick = onRetry) {
                        Text("Tekrar Dene", color = p.primary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = d.scrollBottomPadding),
                ) {
                    // ── Section 1: Onerilenler ────
                    item {
                        SectionHeader(
                            title = "ÖNERİLENLER",
                            modifier = Modifier.padding(
                                start = d.sectionHeaderH, end = d.sectionHeaderH,
                                top = d.sectionHeaderTop, bottom = d.sectionHeaderBottom,
                            ),
                        )
                    }
                    item {
                        val suggestedUsers = (uiState.suggestedContent as? UiState.Success)?.data
                        if (suggestedUsers != null && suggestedUsers.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = d.sectionHeaderH),
                                horizontalArrangement = Arrangement.spacedBy(d.suggestedRowSpacing),
                            ) {
                                items(suggestedUsers, key = { it.id }) { user ->
                                    SuggestedUserCard(
                                        user = user,
                                        isFollowLoading = user.id in uiState.followLoadingIds,
                                        onFollow = { onFollowSuggested(user) },
                                        onTap = { onUserTap(user.username) },
                                    )
                                }
                            }
                        } else if (uiState.suggestedContent is UiState.Loading) {
                            Row(modifier = Modifier.padding(horizontal = d.sectionHeaderH)) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .width(d.suggestedCardWidth)
                                            .height(d.skeletonCardHeight)
                                            .padding(end = d.suggestedRowSpacing)
                                            .clip(RoundedCornerShape(d.suggestedCardRadius))
                                            .background(p.bgInput),
                                    )
                                }
                            }
                        }
                    }

                    // ── Section 2: En Cok Katkida Bulunanlar ────
                    item {
                        SectionHeader(
                            title = "EN ÇOK KATKIDA BULUNANLAR",
                            modifier = Modifier.padding(
                                start = d.sectionHeaderH, end = d.sectionHeaderH,
                                top = d.sectionHeaderTopSecond, bottom = d.sectionHeaderBottom,
                            ),
                        )
                    }
                    val contributorsUsers = (uiState.contributorsContent as? UiState.Success)?.data
                    if (contributorsUsers != null && contributorsUsers.isNotEmpty()) {
                        items(contributorsUsers, key = { it.id }) { contributor ->
                            ContributorRow(
                                contributor = contributor,
                                isFollowLoading = contributor.id in uiState.followLoadingIds,
                                onFollow = { onFollowContrib(contributor) },
                                onTap = { onUserTap(contributor.username) },
                            )
                        }
                    } else if (uiState.contributorsContent is UiState.Loading) {
                        repeat(5) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = d.sectionHeaderH, vertical = d.contribRowVPadding)
                                        .height(d.contribRowHeight)
                                        .clip(RoundedCornerShape(d.contribRowRadius))
                                        .background(p.bgInput),
                                )
                            }
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
private fun DiscoveryTopBar(onBack: () -> Unit) {
    val d = CommunityDimens
    val p = CommunityPalette

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(d.topBarHeight)
            .background(p.surface)
            .border(d.topBarBorder, p.bgInput),
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
            text = "Gezginleri Keşfet",
            style = OmniTextStyles.profileHandle,
            color = p.textPrimary,
        )
    }
}

// ─────────────────────────────────────────
// Section Header
// ─────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = OmniTextStyles.sectionHeader,
        color = CommunityPalette.textSecondary,
        modifier = modifier,
    )
}

// ─────────────────────────────────────────
// Suggested User Card
// ─────────────────────────────────────────
@Composable
private fun SuggestedUserCard(
    user: SuggestedUserUiModel,
    isFollowLoading: Boolean,
    onFollow: () -> Unit,
    onTap: () -> Unit,
) {
    val d = CommunityDimens
    val p = CommunityPalette

    Column(
        modifier = Modifier
            .width(d.suggestedCardWidth)
            .shadow(d.suggestedCardShadow, RoundedCornerShape(d.suggestedCardRadius))
            .clip(RoundedCornerShape(d.suggestedCardRadius))
            .background(p.surface)
            .clickable(onClick = onTap)
            .padding(horizontal = d.suggestedCardHPadding, vertical = d.suggestedCardVPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(d.suggestedCardSpacing),
    ) {
        Box(
            modifier = Modifier
                .size(d.suggestedAvatarSize)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = user.avatarColors)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user.avatarInitial,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }

        Text(
            text = user.handle,
            style = MaterialTheme.typography.titleSmall,
            color = p.textPrimary,
        )

        if (user.location.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.suggestedInfoSpacing),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = p.textSecondary,
                    modifier = Modifier.size(d.suggestedLocationIconSize),
                )
                Text(
                    text = user.location,
                    style = OmniTextStyles.profileLabel,
                    color = p.textSecondary,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.suggestedInfoSpacing),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                tint = p.starAmber,
                modifier = Modifier.size(d.suggestedStarIconSize),
            )
            Text(
                text = "${user.karma} karma",
                style = OmniTextStyles.profileLabel,
                color = p.textSecondary,
            )
        }

        Spacer(Modifier.height(d.suggestedSpacerH))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(d.suggestedFollowBtnHeight)
                .clip(RoundedCornerShape(d.suggestedFollowBtnRadius))
                .background(if (user.isFollowing) p.bgInput else p.primary)
                .border(
                    d.suggestedFollowBtnBorder,
                    if (user.isFollowing) p.border else p.primary,
                    RoundedCornerShape(d.suggestedFollowBtnRadius),
                )
                .then(if (isFollowLoading) Modifier else Modifier.clickable(onClick = onFollow)),
            contentAlignment = Alignment.Center,
        ) {
            if (isFollowLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(d.loadingIndicatorSize),
                    strokeWidth = d.loadingIndicatorStroke,
                    color = if (user.isFollowing) p.textSecondary else Color.White,
                )
            } else {
                Text(
                    text = if (user.isFollowing) "✓ Takip" else "Takip Et",
                    style = OmniTextStyles.profileChip.copy(fontWeight = FontWeight.Bold),
                    color = if (user.isFollowing) p.textSecondary else Color.White,
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// Top Contributor Row
// ─────────────────────────────────────────
@Composable
private fun ContributorRow(
    contributor: ContributorUiModel,
    isFollowLoading: Boolean,
    onFollow: () -> Unit,
    onTap: () -> Unit,
) {
    val d = CommunityDimens
    val p = CommunityPalette
    val isTopRank = contributor.rank == 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = d.contribRowHPadding, vertical = d.contribRowVPadding),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(d.contribRowHeight)
                .shadow(d.contribRowShadow, RoundedCornerShape(d.contribRowRadius))
                .clip(RoundedCornerShape(d.contribRowRadius))
                .background(p.surface)
                .clickable(onClick = onTap)
                .padding(horizontal = d.contribRowInnerHPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.contribRowSpacing),
        ) {
            Box(
                modifier = Modifier.width(d.contribRankWidth),
                contentAlignment = Alignment.Center,
            ) {
                when (contributor.rank) {
                    1 -> Text(text = "🥇", style = OmniTextStyles.heroTitle)
                    2 -> Text(text = "🥈", style = OmniTextStyles.heroTitle)
                    3 -> Text(text = "🥉", style = OmniTextStyles.heroTitle)
                    else -> Text(
                        text = "${contributor.rank}",
                        style = OmniTextStyles.profileBlockedTitle.copy(fontWeight = FontWeight.Bold),
                        color = p.textSecondary,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(d.contribAvatarSize)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = contributor.avatarColors)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = contributor.avatarInitial,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(d.contribInfoSpacing),
            ) {
                Text(
                    text = contributor.handle,
                    style = MaterialTheme.typography.titleSmall,
                    color = p.textPrimary,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.contribStatsSpacing),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = p.starAmber,
                        modifier = Modifier.size(d.contribStarIconSize),
                    )
                    Text(
                        text = "${contributor.karma} karma · ${contributor.tripCount} gezi",
                        style = OmniTextStyles.profileLabel,
                        color = p.textSecondary,
                    )
                }
            }

            if (contributor.isFollowing) {
                Box(
                    modifier = Modifier
                        .width(d.contribFollowBtnWidth)
                        .height(d.contribFollowBtnHeight)
                        .clip(RoundedCornerShape(d.contribFollowBtnRadius))
                        .border(d.contribFollowBtnBorder, p.border, RoundedCornerShape(d.contribFollowBtnRadius))
                        .then(if (isFollowLoading) Modifier else Modifier.clickable(onClick = onFollow)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isFollowLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(d.loadingIndicatorSize),
                            strokeWidth = d.loadingIndicatorStroke,
                            color = p.textSecondary,
                        )
                    } else {
                        Text(
                            text = "✓ Takip",
                            style = OmniTextStyles.profileLabel.copy(fontWeight = FontWeight.SemiBold),
                            color = p.textSecondary,
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(d.contribFollowBtnWidth)
                        .height(d.contribFollowBtnHeight)
                        .clip(RoundedCornerShape(d.contribFollowBtnRadius))
                        .border(d.contribFollowBtnBorderBlue, p.primary, RoundedCornerShape(d.contribFollowBtnRadius))
                        .then(if (isFollowLoading) Modifier else Modifier.clickable(onClick = onFollow)),
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

        if (isTopRank) {
            Box(
                modifier = Modifier
                    .width(d.contribGoldBarWidth)
                    .height(d.contribRowHeight)
                    .clip(RoundedCornerShape(topStart = d.contribRowRadius, bottomStart = d.contribRowRadius))
                    .background(p.gold)
                    .align(Alignment.CenterStart),
            )
        }
    }
}

// ─────────────────────────────────────────
// Previews
// ─────────────────────────────────────────
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewCommunityDiscovery() {
    val uiState = CommunityUiState(
        suggestedContent = UiState.Success(sampleSuggested),
        contributorsContent = UiState.Success(sampleContributors),
    )
    CommunityDiscoveryScreen(uiState = uiState)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewCommunityDiscoveryEmpty() {
    val uiState = CommunityUiState(
        suggestedContent = UiState.Success(emptyList()),
        contributorsContent = UiState.Success(emptyList()),
    )
    CommunityDiscoveryScreen(uiState = uiState)
}

private val sampleSuggested = listOf(
    SuggestedUserUiModel("mock-s1", "alptravel", "@alptravel", "Barcelona (Mock)", 3840, "A", ProfilePalette.postAvatarGradients[1]),
    SuggestedUserUiModel("mock-s2", "selin.k", "@selin.k", "İstanbul (Mock)", 2340, "S", ProfilePalette.postAvatarGradients[0], isFollowing = true),
    SuggestedUserUiModel("mock-s3", "ece.world", "@ece.world", "Tokyo (Mock)", 1890, "E", ProfilePalette.postAvatarGradients[3]),
    SuggestedUserUiModel("mock-s4", "can.exp", "@can.exp", "Roma (Mock)", 560, "C", ProfilePalette.postAvatarGradients[4]),
)

private val sampleContributors = listOf(
    ContributorUiModel(1, "mock-t1", "ceydagezgin", "@ceydagezgin", 12480, 34, "C", ProfilePalette.postAvatarGradients[2]),
    ContributorUiModel(2, "mock-t2", "alptravel", "@alptravel", 8920, 28, "A", ProfilePalette.postAvatarGradients[1]),
    ContributorUiModel(3, "mock-t3", "selin.k", "@selin.k", 6340, 19, "S", ProfilePalette.postAvatarGradients[0]),
    ContributorUiModel(4, "mock-t4", "mert.y", "@mert.y", 4210, 15, "M", ProfilePalette.postAvatarGradients[2]),
    ContributorUiModel(5, "mock-t5", "ece.world", "@ece.world", 3180, 11, "E", ProfilePalette.postAvatarGradients[3]),
)
