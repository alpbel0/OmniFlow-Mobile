package com.omniflow.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.omniflow.R
import com.omniflow.core.common.UiState
import com.omniflow.core.common.asString
import com.omniflow.core.designsystem.theme.HomeDimens
import com.omniflow.core.designsystem.theme.HomePalette
import com.omniflow.core.designsystem.theme.OmniTextStyles
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.uicomponents.EmptyState
import com.omniflow.uicomponents.ErrorView
import com.omniflow.uicomponents.LoadingIndicator

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onSearchClick: () -> Unit = {},
    onNotifClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCommunityUserClick: (String) -> Unit = {},
    onTripClick: (String) -> Unit = {},
    onInspirationClick: (String) -> Unit = {},
    onCreateTrip: () -> Unit = {},
    onRetry: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    val contentState = uiState.contentState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues),
    ) {
        when (contentState) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Empty -> {
                EmptyState(
                    title = "Henüz hiç gezi yok",
                    description = "İlk seyahatini planlamaya başla",
                )
            }
            is UiState.Error -> {
                ErrorView(
                    message = contentState.message.asString(),
                    onRetry = onRetry,
                )
            }
            is UiState.Success -> {
                HomeContent(
                    model = contentState.data,
                    onSearchClick = onSearchClick,
                    onNotifClick = onNotifClick,
                    onProfileClick = onProfileClick,
                    onCommunityUserClick = onCommunityUserClick,
                    onTripClick = onTripClick,
                    onInspirationClick = onInspirationClick,
                    onCreateTrip = onCreateTrip,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    model: HomeUiModel,
    onSearchClick: () -> Unit,
    onNotifClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCommunityUserClick: (String) -> Unit,
    onTripClick: (String) -> Unit,
    onInspirationClick: (String) -> Unit,
    onCreateTrip: () -> Unit,
) {
    val d = HomeDimens
    val s = OmniTokens.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(
            userName = model.userName,
            userInitial = model.userInitial,
            profilePhotoUrl = model.profilePhotoUrl,
            hasUnreadNotif = model.hasUnreadNotifications,
            onNotifClick = onNotifClick,
            onProfileClick = onProfileClick,
        )

        Spacer(Modifier.height(s.xs))

        SearchBar(
            modifier = Modifier.padding(horizontal = d.contentHPadding),
            onClick = onSearchClick,
        )

        Spacer(Modifier.height(s.base))

        val heroTrips = model.heroTrips
        if (heroTrips.isNotEmpty()) {
            UpcomingTripCard(
                trip = heroTrips.first(),
                modifier = Modifier.padding(horizontal = d.contentHPadding),
                onClick = { onTripClick(heroTrips.first().id) },
            )
        } else {
            EmptyStateHeroCard(
                onCreateTrip = onCreateTrip,
                modifier = Modifier.padding(horizontal = d.contentHPadding),
            )
        }

        Spacer(Modifier.height(d.sectionSpacingLarge))

        SectionHeader(
            title = "İlham Al",
            linkText = "Tümünü gör →",
            modifier = Modifier.padding(horizontal = d.contentHPadding),
        )
        Spacer(Modifier.height(d.sectionHeaderTopSpacing))
        DestinationRow(
            destinations = model.inspirationTrips,
            onItemClick = onInspirationClick,
            useGradientFallback = heroTrips.isEmpty(),
        )

        Spacer(Modifier.height(d.sectionSpacing))

        val featuredSection = model.featuredSection
        if (featuredSection is HomeSectionUiModel.Content && featuredSection.items.isNotEmpty()) {
            SectionHeader(
                title = "Öne Çıkan Geziler",
                modifier = Modifier.padding(horizontal = d.contentHPadding),
            )
            Spacer(Modifier.height(d.sectionHeaderTopSpacing))
            FeaturedTripsRow(
                trips = featuredSection.items,
                onTripClick = onTripClick,
                onFavoriteToggle = { /* TODO: save/unsave trip */ },
            )
        }

        Spacer(Modifier.height(d.sectionSpacing))

        val communitySection = model.communitySection
        if (communitySection is HomeSectionUiModel.Content && communitySection.items.isNotEmpty()) {
            SectionHeader(
                title = "Topluluktan",
                modifier = Modifier.padding(horizontal = d.contentHPadding),
            )
            Spacer(Modifier.height(d.sectionHeaderTopSpacing))
            CommunityCard(
                items = communitySection.items,
                modifier = Modifier.padding(horizontal = d.contentHPadding),
                onItemClick = onCommunityUserClick,
            )
        }
    }
}

@Composable
private fun TopBar(
    userName: String,
    userInitial: String,
    profilePhotoUrl: String?,
    hasUnreadNotif: Boolean,
    onNotifClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val d = HomeDimens
    val tokens = OmniTokens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(d.topBarHeight)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = d.topBarHPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.topBarSpacing),
        ) {
            Box(
                modifier = Modifier
                    .size(d.avatarSize)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(HomePalette.avatarGradientStart, HomePalette.avatarGradientEnd),
                        ),
                    )
                    .border(d.avatarBorderWidth, MaterialTheme.colorScheme.surface, CircleShape)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = userInitial,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "Merhaba, $userName 👋",
                style = OmniTextStyles.greeting,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Box(
            modifier = Modifier
                .size(d.notifIconSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(OmniTokens.dimens.hairline, tokens.colors.fieldBorder, CircleShape)
                .clickable(onClick = onNotifClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = "Bildirimler",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(d.notifIconInnerSize),
            )
            if (hasUnreadNotif) {
                Box(
                    modifier = Modifier
                        .size(d.notifBadgeSize)
                        .clip(CircleShape)
                        .background(tokens.colors.danger)
                        .border(d.avatarBorderWidth, MaterialTheme.colorScheme.background, CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = d.notifBadgeOffsetX, y = d.notifBadgeOffsetY),
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val d = HomeDimens
    val tokens = OmniTokens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(d.searchBarHeight)
            .clip(RoundedCornerShape(d.searchBarRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(OmniTokens.dimens.hairline, tokens.colors.fieldBorder, RoundedCornerShape(d.searchBarRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = d.searchBarHPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.topBarSpacing),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(d.searchIconSize),
        )
        Text(
            text = "Where can we take you?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_filter),
            contentDescription = "Filtrele",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(d.searchIconSize),
        )
    }
}

@Composable
private fun UpcomingTripCard(
    trip: HomeHeroTripUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val d = HomeDimens
    val tokens = OmniTokens
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        tokens.colors.brandGradientMid,
        tokens.colors.brandGradientEnd,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(d.heroCardHeight)
            .clip(RoundedCornerShape(d.heroCardRadius))
            .background(brush = Brush.linearGradient(colors = gradientColors))
            .clickable(onClick = onClick)
            .padding(d.heroCardPadding),
    ) {
        Box(
            modifier = Modifier
                .size(d.heroCircleSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
                .align(Alignment.TopEnd)
                .offset(x = d.heroCircleOffsetX, y = d.heroCircleOffsetY),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(d.heroStatusSpacing)) {
                Text(
                    text = trip.statusLine.uppercase(),
                    style = OmniTextStyles.statusLabel,
                    color = Color.White.copy(alpha = 0.7f),
                )
                Text(
                    text = trip.title,
                    style = OmniTextStyles.heroTitle,
                    color = Color.White,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(d.heroMetaSpacing)) {
                    Text(
                        text = "\uD83D\uDCCD ${trip.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                    Text(
                        text = "\uD83D\uDDD3 ${trip.dates}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.heroMetaSpacing),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(d.heroProgressBarHeight)
                        .clip(RoundedCornerShape(d.progressRadius))
                        .background(Color.White.copy(alpha = 0.2f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(trip.progressFraction)
                            .clip(RoundedCornerShape(d.progressRadius))
                            .background(Color.White),
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(d.heroChipRadius))
                        .background(Color.White)
                        .padding(horizontal = d.heroChipHPadding, vertical = d.heroChipVPadding),
                ) {
                    Text(
                        text = trip.chipText,
                        style = OmniTextStyles.captionTiny,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateHeroCard(
    onCreateTrip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = HomeDimens
    val tokens = OmniTokens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(d.heroCardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(d.emptyStateBorderWidth, tokens.colors.fieldBorder, RoundedCornerShape(d.heroCardRadius))
            .padding(d.emptyStateCardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OmniTokens.spacing.s),
    ) {
        Box(
            modifier = Modifier
                .size(d.emptyStateIconContainer)
                .clip(CircleShape)
                .background(tokens.colors.iconContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_compass),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(d.emptyStateIconSize),
            )
        }

        Text(
            text = "İlk gezini planla",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Nereye gitmek istiyorsun?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
        )

        Spacer(Modifier.height(OmniTokens.spacing.xs))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(d.emptyStateButtonHeight)
                .clip(RoundedCornerShape(d.emptyStateButtonRadius))
                .background(MaterialTheme.colorScheme.primary)
                .shadow(
                    elevation = d.emptyStateButtonElevation,
                    shape = RoundedCornerShape(d.emptyStateButtonRadius),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                )
                .clickable(onClick = onCreateTrip),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Gezi Oluştur",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    linkText: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (linkText != null) {
            Text(
                text = linkText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {},
            )
        }
    }
}

@Composable
private fun DestinationRow(
    destinations: List<HomeInspirationUiModel>,
    onItemClick: (String) -> Unit,
    useGradientFallback: Boolean,
) {
    val d = HomeDimens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = d.contentHPadding),
        horizontalArrangement = Arrangement.spacedBy(d.destinationRowSpacing),
    ) {
        destinations.forEachIndexed { index, dest ->
            DestinationCardItem(
                dest = dest,
                onClick = { onItemClick(dest.city) },
                useGradientFallback = useGradientFallback,
                gradientIndex = index,
            )
        }
    }
}

@Composable
private fun DestinationCardItem(
    dest: HomeInspirationUiModel,
    onClick: () -> Unit,
    useGradientFallback: Boolean,
    gradientIndex: Int,
) {
    val d = HomeDimens
    val gradientColors = HomePalette.emptyStateDestinationGradients[
        gradientIndex % HomePalette.emptyStateDestinationGradients.size
    ]

    Box(
        modifier = Modifier
            .width(d.destinationCardWidth)
            .height(d.destinationCardHeight)
            .clip(RoundedCornerShape(d.destinationCardRadius))
            .shadow(d.destinationCardShadow, RoundedCornerShape(d.destinationCardRadius))
            .clickable(onClick = onClick),
    ) {
        if (useGradientFallback) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = gradientColors)),
            )
        } else {
            AsyncImage(
                model = dest.imageUrl,
                contentDescription = dest.city,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                        ),
                    ),
            )
            Text(
                text = "\u2192",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(d.destinationArrowPadding),
            )
        }
        Text(
            text = dest.city,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(d.destinationCityPadding),
        )
    }
}

@Composable
private fun FeaturedTripsRow(
    trips: List<HomeFeaturedTripUiModel>,
    onTripClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
) {
    val d = HomeDimens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = d.contentHPadding),
        horizontalArrangement = Arrangement.spacedBy(d.featuredRowSpacing),
    ) {
        trips.forEachIndexed { index, trip ->
            FeaturedTripCard(
                trip = trip,
                index = index,
                onClick = { onTripClick(trip.id) },
                onFavoriteToggle = { onFavoriteToggle(trip.id) },
            )
        }
    }
}

@Composable
private fun FeaturedTripCard(
    trip: HomeFeaturedTripUiModel,
    index: Int,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit = {},
) {
    val d = HomeDimens
    val gradientColors = HomePalette.featuredTripGradients[index % HomePalette.featuredTripGradients.size]

    Column(
        modifier = Modifier
            .width(d.featuredCardWidth)
            .clip(RoundedCornerShape(d.featuredCardRadius))
            .shadow(d.featuredCardShadow, RoundedCornerShape(d.featuredCardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(d.featuredCardImageHeight),
        ) {
            if (trip.coverPhotoUrl != null) {
                AsyncImage(
                    model = trip.coverPhotoUrl,
                    contentDescription = trip.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(colors = gradientColors)),
                )
            }

            // Rating pill (sol üst)
            Row(
                modifier = Modifier
                    .padding(d.featuredRatingPillPad)
                    .clip(RoundedCornerShape(d.progressRadius))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(
                        horizontal = d.featuredRatingPillHPad,
                        vertical = d.featuredRatingPillVPad,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.featuredRatingPillSpacing),
            ) {
                Text(text = "\u2B50", style = OmniTextStyles.captionTiny)
                Text(
                    text = "${trip.upvoteCount}",
                    style = OmniTextStyles.captionTiny,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            // Kalp (favori) butonu (sağ üst)
            Box(
                modifier = Modifier
                    .size(d.favoriteButtonSize)
                    .align(Alignment.TopEnd)
                    .padding(
                        top = d.favoriteButtonTopPadding,
                        end = d.favoriteButtonEndPadding,
                    )
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(onClick = onFavoriteToggle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_heart),
                    contentDescription = "Favori",
                    tint = if (trip.isSaved) MaterialTheme.colorScheme.primary else Color.White,
                    modifier = Modifier.size(d.favoriteIconSize),
                )
            }
        }
        Column(
            modifier = Modifier.padding(
                horizontal = d.featuredCardBodyHPad,
                vertical = d.featuredCardBodyVPad,
            ),
            verticalArrangement = Arrangement.spacedBy(d.featuredBodySpacing),
        ) {
            Text(
                text = trip.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "by ${trip.author}",
                style = OmniTextStyles.bodyXSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun CommunityCard(
    items: List<HomeCommunityPreviewUiModel>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit = {},
) {
    val d = HomeDimens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(d.communityCardRadius))
            .shadow(d.communityCardShadow, RoundedCornerShape(d.communityCardRadius))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .padding(
                            start = d.communityDividerStartPad,
                            end = d.communityDividerEndPad,
                        )
                        .fillMaxWidth()
                        .height(d.communityDividerHeight)
                        .background(HomePalette.dividerColor),
                )
            }
            CommunityRow(item, onClick = { onItemClick(item.username) })
        }
    }
}

@Composable
private fun CommunityRow(
    item: HomeCommunityPreviewUiModel,
    onClick: () -> Unit,
) {
    val d = HomeDimens
    val initials = item.username.take(2).uppercase()
    val avatarColors = HomePalette.communityAvatarGradients[
        item.username.hashCode().mod(HomePalette.communityAvatarGradients.size)
    ]
    val thumbColors = HomePalette.communityThumbGradients[
        item.username.hashCode().mod(HomePalette.communityThumbGradients.size)
    ]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = d.communityRowHPad, vertical = d.communityRowVPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.communityRowSpacing),
    ) {
        Box(
            modifier = Modifier
                .size(d.communityAvatarSize)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = avatarColors)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                style = OmniTextStyles.captionTiny,
                color = Color.White,
            )
        }

        Text(
            text = "@${item.username} bir gezi paylaştı · ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = item.timeAgo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
        )

        Box(
            modifier = Modifier
                .size(d.communityThumbSize)
                .clip(RoundedCornerShape(d.communityThumbRadius))
                .background(Brush.linearGradient(colors = thumbColors)),
        )
    }
}

// ─────────────────────────────────────────
// Preview Sample Data
// ─────────────────────────────────────────
private val sampleHomeUiModel = HomeUiModel(
    userName = "Yiğit",
    userInitial = "Y",
    profilePhotoUrl = null,
    hasUnreadNotifications = true,
    heroTrips = listOf(
        HomeHeroTripUiModel(
            id = "1",
            title = "Roma & Floransa",
            city = "İtalya",
            dates = "15 Tem-22 Tem",
            statusLine = "8 gün sonra başlıyor",
            chipText = "8 gün kaldı",
            coverPhotoUrl = null,
            progressFraction = 0.30f,
        ),
    ),
    inspirationTrips = listOf(
        HomeInspirationUiModel("Roma", "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=220&h=280&fit=crop"),
        HomeInspirationUiModel("Bali", "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=220&h=280&fit=crop"),
        HomeInspirationUiModel("Tokyo", "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=220&h=280&fit=crop"),
        HomeInspirationUiModel("Barselona", "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?w=220&h=280&fit=crop"),
    ),
    featuredSection = HomeSectionUiModel.Content(
        listOf(
            HomeFeaturedTripUiModel("1", "Roma & Floransa Turu", "@alptravel", null, 128, false),
            HomeFeaturedTripUiModel("2", "Barselona'da 5 Gün", "@selintravel", null, 96, true),
        ),
    ),
    communitySection = HomeSectionUiModel.Content(
        listOf(
            HomeCommunityPreviewUiModel("1", "selin.k", null, "yeni bir gezi paylaştı", null, "2s"),
            HomeCommunityPreviewUiModel("2", "arda.t", null, "bir ipucu paylaştı", null, "1s"),
        ),
    ),
)

private val sampleEmptyHomeUiModel = HomeUiModel(
    userName = "Yiğit",
    userInitial = "Y",
    profilePhotoUrl = null,
    hasUnreadNotifications = false,
    heroTrips = emptyList(),
    inspirationTrips = sampleHomeUiModel.inspirationTrips,
    featuredSection = HomeSectionUiModel.Hidden,
    communitySection = HomeSectionUiModel.Hidden,
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewHomeScreenVariantA() {
    MaterialTheme {
        HomeContent(
            model = sampleHomeUiModel,
            onSearchClick = {},
            onNotifClick = {},
            onProfileClick = {},
            onCommunityUserClick = {},
            onTripClick = {},
            onInspirationClick = {},
            onCreateTrip = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewHomeScreenVariantB() {
    MaterialTheme {
        HomeContent(
            model = sampleEmptyHomeUiModel,
            onSearchClick = {},
            onNotifClick = {},
            onProfileClick = {},
            onCommunityUserClick = {},
            onTripClick = {},
            onInspirationClick = {},
            onCreateTrip = {},
        )
    }
}
