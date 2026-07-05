package com.omniflow.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.omniflow.R
import com.omniflow.core.common.UiState
import com.omniflow.core.designsystem.theme.OmniTextStyles
import com.omniflow.core.designsystem.theme.PublicProfileDimens
import com.omniflow.core.designsystem.theme.PublicProfilePalette
import com.omniflow.ui.profile.PublicProfileTab
import com.omniflow.ui.profile.PublicProfileUiModel
import com.omniflow.ui.profile.PublicProfileUiState
import com.omniflow.ui.profile.ProfileTripUiModel
import com.omniflow.ui.profile.ProfilePostUiModel

// ─────────────────────────────────────────
// Data Models
// ─────────────────────────────────────────
enum class PublicProfileVariant {
    NORMAL,
    FOLLOWING,
    BLOCKED
}

data class PublicProfile(
    val userId: String,
    val username: String,
    val handle: String,
    val bio: String,
    val location: String,
    val karma: Int,
    val followerCount: Int,
    val followingCount: Int,
    val avatarInitial: String,
    val avatarColors: List<Color>,
    val profilePhotoUrl: String?,
)

data class PublicTripCard(
    val id: String,
    val title: String,
    val rating: Float,
    val gradientColors: List<Color>,
)

data class PublicPost(
    val id: String,
    val handle: String,
    val timeAgo: String,
    val body: String,
    val upvotes: Int,
    val comments: Int,
    val avatarColors: List<Color>,
)

// ─────────────────────────────────────────
// PublicProfileScreen — root composable
// ─────────────────────────────────────────
@Composable
fun PublicProfileScreen(
    uiState: PublicProfileUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {},
    onMoreMenu: () -> Unit = {},
    onFollowersTap: () -> Unit = {},
    onFollowingTap: () -> Unit = {},
    onFollowTap: () -> Unit = {},
    onUnfollowTap: () -> Unit = {},
    onMessageTap: () -> Unit = {},
    onUnblockTap: () -> Unit = {},
    onTabChange: (PublicProfileTab) -> Unit = {},
    onTripTap: (String) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val d = PublicProfileDimens
    val p = PublicProfilePalette

    val variant = when {
        uiState.isBlockedByMe -> PublicProfileVariant.BLOCKED
        uiState.isFollowing -> PublicProfileVariant.FOLLOWING
        else -> PublicProfileVariant.NORMAL
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(p.bgScreen)
    ) {
        when (val content = uiState.contentState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                        color = p.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(d.errorSpacer8))
                    TextButton(onClick = onRetry) {
                        Text("Tekrar Dene", color = p.primary)
                    }
                }
            }

            is UiState.Success, is UiState.Empty -> {
                val model = (content as? UiState.Success)?.data
                val profile = model?.let {
                    PublicProfile(
                        userId = it.userId,
                        username = it.username,
                        handle = it.handle,
                        bio = it.bio,
                        location = it.location,
                        karma = it.karma,
                        followerCount = it.followerCount,
                        followingCount = it.followingCount,
                        avatarInitial = it.avatarInitial,
                        avatarColors = it.avatarColors,
                        profilePhotoUrl = it.profilePhotoUrl,
                    )
                } ?: defaultPublicProfile
                val trips = model?.trips?.map {
                    PublicTripCard(it.id, it.title, it.upvoteCount.toFloat(), it.gradientColors)
                } ?: defaultTrips
                val posts = model?.posts?.map {
                    PublicPost(
                        it.id,
                        it.handle,
                        it.timeAgo,
                        it.body,
                        it.upvotes,
                        it.comments,
                        it.avatarColors,
                    )
                } ?: defaultPosts

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    PublicProfileTopBar(
                        handle = profile.handle,
                        onBack = onBack,
                        onMoreMenu = onMoreMenu,
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = d.scrollBottomPadding),
                    ) {
                        when (variant) {
                            PublicProfileVariant.BLOCKED -> {
                                BlockedProfileContent(
                                    profile = profile,
                                    onUnblock = onUnblockTap,
                                )
                            }
                            else -> {
                                PublicProfileHeaderCard(
                                    variant = variant,
                                    profile = profile,
                                    onFollowers = onFollowersTap,
                                    onFollowing = onFollowingTap,
                                    onFollow = onFollowTap,
                                    onUnfollow = onUnfollowTap,
                                    onMessage = onMessageTap,
                                    modifier = Modifier.padding(
                                        start = d.chipRowHPadding,
                                        end = d.chipRowHPadding,
                                        top = d.headerCardModifierPad,
                                    ),
                                )

                                PublicContentFilterChips(
                                    activeTab = uiState.activeTab,
                                    onTabChange = { tab -> onTabChange(tab) },
                                )

                                PublicContentGrid(
                                    trips = trips,
                                    posts = posts,
                                    activeTab = uiState.activeTab,
                                    onTripTap = { onTripTap(it.id) },
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
private fun PublicProfileTopBar(
    handle: String,
    onBack: () -> Unit,
    onMoreMenu: () -> Unit,
) {
    val d = PublicProfileDimens
    val p = PublicProfilePalette

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(d.topBarHeight)
            .background(p.surface)
            .border(d.dividerWidth, p.bgInput),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = d.topBarHPadding)
                .size(d.topBarIconContainer)
                .clip(CircleShape)
                .background(p.bgInput)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Geri",
                tint = p.textPrimary,
                modifier = Modifier.size(d.topBarBackIconSize),
            )
        }

        Text(
            text = handle,
            style = OmniTextStyles.profileHandle,
            color = p.textPrimary,
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = d.topBarHPadding)
                .size(d.topBarIconContainer)
                .clip(CircleShape)
                .background(p.bgInput)
                .clickable(onClick = onMoreMenu),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_more_vertical),
                contentDescription = "Daha fazla",
                tint = p.textSecondary,
                modifier = Modifier.size(d.topBarMenuIconSize),
            )
        }
    }
}

// ─────────────────────────────────────────
// Profile Header Card
// ─────────────────────────────────────────
@Composable
private fun PublicProfileHeaderCard(
    variant: PublicProfileVariant,
    profile: PublicProfile,
    onFollowers: () -> Unit,
    onFollowing: () -> Unit,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = PublicProfileDimens
    val p = PublicProfilePalette

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(d.headerCardShadow, RoundedCornerShape(d.headerCardRadius))
            .clip(RoundedCornerShape(d.headerCardRadius))
            .background(p.surface)
            .padding(d.headerCardPadding),
        verticalArrangement = Arrangement.spacedBy(d.headerCardSpacing),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(d.infoRowSpacing),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(d.avatarSize)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = profile.avatarColors)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile.avatarInitial,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(d.infoSpacing),
                modifier = Modifier.padding(top = d.infoTopPadding),
            ) {
                Text(
                    text = profile.handle,
                    style = MaterialTheme.typography.titleMedium,
                    color = p.textPrimary,
                )
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = p.textSecondary,
                )
                if (profile.location.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(d.iconTextSpacing),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            tint = p.textSecondary,
                            modifier = Modifier.size(d.locationIconSize),
                        )
                        Text(
                            text = profile.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = p.textSecondary,
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.iconTextSpacing),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = p.starAmber,
                        modifier = Modifier.size(d.starIconSize),
                    )
                    Text(
                        text = "${profile.karma} karma",
                        style = MaterialTheme.typography.labelMedium,
                        color = p.textPrimary,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(d.statsRadius))
                .border(d.statsDividerWidth, p.bgInput, RoundedCornerShape(d.statsRadius)),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onFollowers)
                    .padding(vertical = d.statsVPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(d.statsSpacing),
            ) {
                Text(
                    text = "${profile.followerCount}",
                    style = OmniTextStyles.profileStat,
                    color = p.textPrimary,
                )
                Text(text = "Takipçi", style = OmniTextStyles.profileLabel, color = p.textSecondary)
            }
            Box(
                modifier = Modifier
                    .width(d.statsDividerWidth)
                    .height(d.statsDividerHeight)
                    .background(p.bgInput)
                    .align(Alignment.CenterVertically),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onFollowing)
                    .padding(vertical = d.statsVPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(d.statsSpacing),
            ) {
                Text(
                    text = "${profile.followingCount}",
                    style = OmniTextStyles.profileStat,
                    color = p.textPrimary,
                )
                Text(text = "Takip", style = OmniTextStyles.profileLabel, color = p.textSecondary)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(d.actionButtonRowSpacing)) {
            when (variant) {
                PublicProfileVariant.NORMAL -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(d.actionButtonHeight)
                            .clip(RoundedCornerShape(d.actionButtonRadius))
                            .background(p.primary)
                            .shadow(
                                d.actionButtonShadow,
                                RoundedCornerShape(d.actionButtonRadius),
                                ambientColor = p.primary.copy(d.followShadowAmbientAlpha),
                                spotColor = p.primary.copy(d.followShadowAmbientAlpha),
                            )
                            .clickable(onClick = onFollow),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Takip Et",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(d.actionButtonHeight)
                            .clip(RoundedCornerShape(d.actionButtonRadius))
                            .border(d.actionButtonBorderWidth, p.border, RoundedCornerShape(d.actionButtonRadius))
                            .background(p.surface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Mesaj",
                            style = MaterialTheme.typography.titleSmall,
                            color = p.textSecondary.copy(alpha = p.messageButtonAlpha),
                        )
                    }
                }

                PublicProfileVariant.FOLLOWING -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(d.actionButtonHeight)
                            .clip(RoundedCornerShape(d.actionButtonRadius))
                            .border(d.actionButtonBorderWidth, p.border, RoundedCornerShape(d.actionButtonRadius))
                            .background(p.surface)
                            .clickable(onClick = onUnfollow),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(d.actionCheckSpacing),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                tint = p.textPrimary,
                                modifier = Modifier.size(d.actionCheckIconSize),
                            )
                            Text(
                                text = "Takip Ediyorsun",
                                style = MaterialTheme.typography.titleSmall,
                                color = p.textPrimary,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(d.actionButtonHeight)
                            .clip(RoundedCornerShape(d.actionButtonRadius))
                            .border(d.actionButtonBorderWidth, p.border, RoundedCornerShape(d.actionButtonRadius))
                            .background(p.surface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Mesaj",
                            style = MaterialTheme.typography.titleSmall,
                            color = p.textSecondary.copy(alpha = p.messageButtonAlpha),
                        )
                    }
                }

                PublicProfileVariant.BLOCKED -> { /* handled in BlockedProfileContent */ }
            }
        }
    }
}

// ─────────────────────────────────────────
// Blocked Profile Content
// ─────────────────────────────────────────
@Composable
private fun BlockedProfileContent(
    profile: PublicProfile,
    onUnblock: () -> Unit,
) {
    val d = PublicProfileDimens
    val p = PublicProfilePalette

    Column(
        modifier = Modifier.padding(horizontal = d.chipRowHPadding, vertical = d.headerCardModifierPad),
        verticalArrangement = Arrangement.spacedBy(d.headerCardSpacing),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(d.headerCardShadow, RoundedCornerShape(d.headerCardRadius))
                .clip(RoundedCornerShape(d.headerCardRadius))
                .background(p.surface)
                .padding(d.headerCardPadding),
            verticalArrangement = Arrangement.spacedBy(d.headerCardSpacing),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(d.infoRowSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(d.avatarSize)
                        .clip(CircleShape)
                        .background(p.blockedAvatarBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = null,
                        tint = p.blockedAvatarTint,
                        modifier = Modifier.size(d.blockedPersonIconSize),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(d.blockedColumnSpacing)) {
                    Text(
                        text = profile.handle,
                        style = MaterialTheme.typography.titleMedium,
                        color = p.textPrimary,
                    )
                    Text(text = "—", style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(d.statsRadius))
                    .border(d.statsDividerWidth, p.bgInput, RoundedCornerShape(d.statsRadius))
                    .background(p.surface.copy(alpha = p.statsBgAlpha)),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = d.statsVPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(d.statsSpacing),
                ) {
                    Text(
                        text = "—",
                        style = OmniTextStyles.profileStat,
                        color = p.textPrimary.copy(alpha = p.blockedTextAlpha),
                    )
                    Text(
                        text = "Takipçi",
                        style = OmniTextStyles.profileLabel,
                        color = p.textSecondary.copy(p.blockedTextAlpha),
                    )
                }
                Box(
                    modifier = Modifier
                        .width(d.statsDividerWidth)
                        .height(d.statsDividerHeight)
                        .background(p.bgInput)
                        .align(Alignment.CenterVertically),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = d.statsVPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(d.statsSpacing),
                ) {
                    Text(
                        text = "—",
                        style = OmniTextStyles.profileStat,
                        color = p.textPrimary.copy(alpha = p.blockedTextAlpha),
                    )
                    Text(
                        text = "Takip",
                        style = OmniTextStyles.profileLabel,
                        color = p.textSecondary.copy(p.blockedTextAlpha),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(d.actionButtonHeight)
                    .clip(RoundedCornerShape(d.actionButtonRadius))
                    .border(d.actionButtonBorderWidth, p.dangerRed, RoundedCornerShape(d.actionButtonRadius))
                    .clickable(onClick = onUnblock),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Engeli Kaldır",
                    style = MaterialTheme.typography.titleSmall,
                    color = p.dangerRed,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = d.blockedMessageVPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(d.blockedMessageSpacing),
        ) {
            Box(
                modifier = Modifier
                    .size(d.blockedIconContainer)
                    .clip(CircleShape)
                    .background(p.bgInput),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_block),
                    contentDescription = null,
                    tint = p.border,
                    modifier = Modifier.size(d.blockedIconSize),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(d.blockedContentSpacing),
            ) {
                Text(
                    text = "Bu kullanıcıyı engellediniz",
                    style = OmniTextStyles.profileBlockedTitle,
                    color = p.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "İçerikleri görüntülenemiyor.",
                    style = MaterialTheme.typography.bodySmall,
                    color = p.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// Content Filter Chips
// ─────────────────────────────────────────
@Composable
private fun PublicContentFilterChips(
    activeTab: PublicProfileTab,
    onTabChange: (PublicProfileTab) -> Unit,
) {
    val d = PublicProfileDimens
    val p = PublicProfilePalette

    val tabs = listOf(
        PublicProfileTab.ALL to "Tümü",
        PublicProfileTab.TRIPS to "Geziler",
        PublicProfileTab.POSTS to "Paylaşımlar",
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = d.chipRowHPadding, vertical = d.chipRowVPadding),
        horizontalArrangement = Arrangement.spacedBy(d.chipRowSpacing),
    ) {
        items(tabs) { (tab, label) ->
            val isActive = tab == activeTab
            Box(
                modifier = Modifier
                    .height(d.chipHeight)
                    .clip(RoundedCornerShape(d.chipRadius))
                    .background(if (isActive) p.primary else p.surface)
                    .border(
                        d.chipBorderWidth,
                        if (isActive) p.primary else p.border,
                        RoundedCornerShape(d.chipRadius),
                    )
                    .clickable { onTabChange(tab) }
                    .padding(horizontal = d.chipHPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = OmniTextStyles.profileChip,
                    color = if (isActive) Color.White else p.textSecondary,
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// Content Grid
// ─────────────────────────────────────────
@Composable
private fun PublicContentGrid(
    trips: List<PublicTripCard>,
    posts: List<PublicPost>,
    activeTab: PublicProfileTab,
    onTripTap: (PublicTripCard) -> Unit,
) {
    val d = PublicProfileDimens

    val showTrips = activeTab == PublicProfileTab.ALL || activeTab == PublicProfileTab.TRIPS
    val showPosts = activeTab == PublicProfileTab.ALL || activeTab == PublicProfileTab.POSTS

    Column(modifier = Modifier.padding(horizontal = d.contentHPadding)) {
        if (showTrips && trips.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(d.contentSpacing),
                modifier = Modifier.fillMaxWidth(),
            ) {
                trips.forEach { trip ->
                    PublicTripCardItem(
                        trip = trip,
                        modifier = Modifier.weight(1f),
                        onTap = { onTripTap(trip) },
                    )
                }
            }
        }
        if (showPosts && posts.isNotEmpty()) {
            Spacer(Modifier.height(d.contentSpacing))
            posts.forEach { post ->
                PublicPostItem(post = post)
                Spacer(Modifier.height(d.contentSpacing))
            }
        }
    }
}

@Composable
private fun PublicTripCardItem(
    trip: PublicTripCard,
    modifier: Modifier = Modifier,
    onTap: () -> Unit,
) {
    val d = PublicProfileDimens
    val p = PublicProfilePalette

    Box(
        modifier = modifier
            .height(d.tripCardHeight)
            .clip(RoundedCornerShape(d.tripCardRadius))
            .background(Brush.linearGradient(colors = trip.gradientColors))
            .clickable(onClick = onTap),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(p.overlayBottomGradient),
                ),
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(d.tripCardPadding)
                .clip(RoundedCornerShape(d.tripRatingPillRadius))
                .background(p.ratingPillBg)
                .padding(horizontal = d.tripRatingPillHPad, vertical = d.tripRatingPillVPad),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.tripRatingPillSpacing),
        ) {
            Text(text = "⭐", style = OmniTextStyles.profileLabel)
            Text(
                text = trip.rating.toString(),
                style = OmniTextStyles.profileLabelBold,
                color = p.textPrimary,
            )
        }
        Text(
            text = trip.title,
            style = OmniTextStyles.profileLabel.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(d.tripCardPadding),
        )
    }
}

@Composable
private fun PublicPostItem(post: PublicPost) {
    val d = PublicProfileDimens
    val p = PublicProfilePalette

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(d.postCardRadius))
            .background(p.surface)
            .border(d.statsDividerWidth, p.bgInput, RoundedCornerShape(d.postCardRadius))
            .padding(d.postCardPadding),
        verticalArrangement = Arrangement.spacedBy(d.postCardSpacing),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.postHeaderSpacing),
        ) {
            Box(
                modifier = Modifier
                    .size(d.postAvatarSize)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(post.avatarColors)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = post.handle.removePrefix("@").take(1).uppercase(),
                    style = OmniTextStyles.profileLabelBold,
                    color = Color.White,
                )
            }
            Text(
                text = post.handle,
                style = OmniTextStyles.profileChip,
                color = p.textPrimary,
            )
            Text(text = "· ${post.timeAgo}", style = OmniTextStyles.profileLabel, color = p.textSecondary)
        }
        Text(text = post.body, style = OmniTextStyles.profilePostBody, color = p.textPrimary, lineHeight = d.bioLineHeight)
        Row(horizontalArrangement = Arrangement.spacedBy(d.postActionSpacing)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.postActionRowSpacing),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_upvote),
                    contentDescription = null,
                    tint = p.textSecondary,
                    modifier = Modifier.size(d.postActionIconSize),
                )
                Text(text = "${post.upvotes}", style = OmniTextStyles.profileLabel, color = p.textSecondary)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.postActionRowSpacing),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_comment),
                    contentDescription = null,
                    tint = p.textSecondary,
                    modifier = Modifier.size(d.postActionIconSize),
                )
                Text(text = "${post.comments}", style = OmniTextStyles.profileLabel, color = p.textSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────
// Sample Data (preview / fallback)
// ─────────────────────────────────────────
val defaultPublicProfile = PublicProfile(
    userId = "",
    username = "alptravel",
    handle = "@alptravel",
    bio = "Dünyayı keşfediyorum ✈️",
    location = "Barcelona, İspanya (Mock)",
    karma = 3840,
    followerCount = 284,
    followingCount = 147,
    avatarInitial = "A",
    avatarColors = PublicProfilePalette.defaultAvatarGradient,
    profilePhotoUrl = null,
)

val defaultTrips = listOf(
    PublicTripCard("1", "Barcelona Turu", 4.9f,
        PublicProfilePalette.defaultTripGradient1),
    PublicTripCard("2", "Madrid Kaçamağı", 4.7f,
        PublicProfilePalette.defaultTripGradient2),
)

val defaultPosts = listOf(
    PublicPost(
        id = "1",
        handle = "@alptravel",
        timeAgo = "1s",
        body = "Barcelona'nın en iyi tapas barları 🍷 Listemi paylaşıyorum!",
        upvotes = 47,
        comments = 9,
        avatarColors = PublicProfilePalette.defaultAvatarGradient,
    ),
)

// ─────────────────────────────────────────
// Previews
// ─────────────────────────────────────────
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewPublicProfileNormal() {
    val model = PublicProfileUiModel(
        userId = "1",
        username = "alptravel",
        handle = "@alptravel",
        bio = "Dünyayı keşfediyorum ✈️",
        location = "Barcelona, İspanya (Mock)",
        karma = 3840,
        followerCount = 284,
        followingCount = 147,
        avatarInitial = "A",
        avatarColors = PublicProfilePalette.defaultAvatarGradient,
        profilePhotoUrl = null,
        trips = listOf(
            ProfileTripUiModel(
                id = "1", title = "Barcelona Turu", coverPhotoUrl = null,
                upvoteCount = 49, gradientColors = PublicProfilePalette.defaultTripGradient1,
            ),
            ProfileTripUiModel(
                id = "2", title = "Madrid Kaçamağı", coverPhotoUrl = null,
                upvoteCount = 47, gradientColors = PublicProfilePalette.defaultTripGradient2,
            ),
        ),
        posts = listOf(
            ProfilePostUiModel(
                id = "1", handle = "@alptravel", timeAgo = "1s",
                body = "Barcelona'nın en iyi tapas barları 🍷 Listemi paylaşıyorum!",
                upvotes = 47, comments = 9,
                avatarColors = PublicProfilePalette.defaultAvatarGradient,
                profilePhotoUrl = null,
            ),
        ),
    )
    val uiState = PublicProfileUiState(
        contentState = UiState.Success(model),
        isFollowing = false,
        isBlockedByMe = false,
    )
    PublicProfileScreen(uiState = uiState)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewPublicProfileFollowing() {
    val model = PublicProfileUiModel(
        userId = "1",
        username = "alptravel",
        handle = "@alptravel",
        bio = "Dünyayı keşfediyorum ✈️",
        location = "Barcelona, İspanya (Mock)",
        karma = 3840,
        followerCount = 284,
        followingCount = 147,
        avatarInitial = "A",
        avatarColors = PublicProfilePalette.defaultAvatarGradient,
        profilePhotoUrl = null,
        trips = listOf(
            ProfileTripUiModel(
                id = "1", title = "Barcelona Turu", coverPhotoUrl = null,
                upvoteCount = 49, gradientColors = PublicProfilePalette.defaultTripGradient1,
            ),
            ProfileTripUiModel(
                id = "2", title = "Madrid Kaçamağı", coverPhotoUrl = null,
                upvoteCount = 47, gradientColors = PublicProfilePalette.defaultTripGradient2,
            ),
        ),
        posts = listOf(
            ProfilePostUiModel(
                id = "1", handle = "@alptravel", timeAgo = "1s",
                body = "Barcelona'nın en iyi tapas barları 🍷 Listemi paylaşıyorum!",
                upvotes = 47, comments = 9,
                avatarColors = PublicProfilePalette.defaultAvatarGradient,
                profilePhotoUrl = null,
            ),
        ),
    )
    val uiState = PublicProfileUiState(
        contentState = UiState.Success(model),
        isFollowing = true,
        isBlockedByMe = false,
    )
    PublicProfileScreen(uiState = uiState)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewPublicProfileBlocked() {
    val model = PublicProfileUiModel(
        userId = "1",
        username = "alptravel",
        handle = "@alptravel",
        bio = "Dünyayı keşfediyorum ✈️",
        location = "",
        karma = 3840,
        followerCount = 284,
        followingCount = 147,
        avatarInitial = "A",
        avatarColors = PublicProfilePalette.defaultAvatarGradient,
        profilePhotoUrl = null,
        trips = emptyList(),
        posts = emptyList(),
    )
    val uiState = PublicProfileUiState(
        contentState = UiState.Success(model),
        isFollowing = false,
        isBlockedByMe = true,
    )
    PublicProfileScreen(uiState = uiState)
}
