package com.omniflow.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.omniflow.R
import com.omniflow.core.common.UiState
import com.omniflow.core.common.asString
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.core.designsystem.theme.ProfileDimens
import com.omniflow.core.designsystem.theme.ProfilePalette
import com.omniflow.uicomponents.EmptyState
import com.omniflow.uicomponents.ErrorView
import com.omniflow.uicomponents.LoadingIndicator

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {},
    onSettingsTap: () -> Unit = {},
    onFollowersTap: () -> Unit = {},
    onFollowingTap: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onTabChange: (ProfileTab) -> Unit = {},
    onTripTap: (String) -> Unit = {},
    onSaveEdit: () -> Unit = {},
    onBioChange: (String) -> Unit = {},
    onLocationChange: (String) -> Unit = {},
    onStyleToggle: (String) -> Unit = {},
    onChangePhoto: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val d = ProfileDimens

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileTopBar(
                variant = uiState.variant,
                onBack = onBack,
                onSettings = onSettingsTap,
                onSave = onSaveEdit,
            )

            when (uiState.variant) {
                ProfileVariant.EDIT -> {
                    EditProfileBody(
                        editBio = uiState.editBio,
                        editLocation = uiState.editLocation,
                        selectedStyles = uiState.editSelectedStyles,
                        handle = cachedHandle(uiState),
                        onBioChange = onBioChange,
                        onLocationChange = onLocationChange,
                        onStyleToggle = onStyleToggle,
                        onChangePhoto = onChangePhoto,
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = d.chipRowHPadding, vertical = d.chipRowVPadding),
                    )
                }
                else -> {
                    val contentState = uiState.contentState
                    when (contentState) {
                        is UiState.Loading -> LoadingIndicator()
                        is UiState.Error -> ErrorView(message = contentState.message.asString(), onRetry = onRetry)
                        is UiState.Empty -> EmptyState(title = "Henüz içerik yok", description = "İlk gezini oluştur")
                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(bottom = d.bottomPadding),
                            ) {
                                ProfileHeaderCard(
                                    profile = contentState.data,
                                    onFollowers = onFollowersTap,
                                    onFollowing = onFollowingTap,
                                    onEditProfile = onEditProfile,
                                    modifier = Modifier.padding(d.headerCardModifierPad),
                                )
                                ContentFilterChips(
                                    activeTab = uiState.activeTab,
                                    onTabChange = onTabChange,
                                )
                                if (contentState.data.trips.isEmpty() && contentState.data.posts.isEmpty()) {
                                    EmptyContentState(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(d.emptyPadding),
                                    )
                                } else {
                                    ContentGrid(
                                        profile = contentState.data,
                                        activeTab = uiState.activeTab,
                                        onTripTap = onTripTap,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun cachedHandle(uiState: ProfileUiState): String {
    return (uiState.contentState as? UiState.Success)?.data?.handle ?: "@kullanıcı"
}

@Composable
private fun ProfileTopBar(
    variant: ProfileVariant,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onSave: () -> Unit,
) {
    val d = ProfileDimens
    val tokens = OmniTokens

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(d.topBarHeight)
            .background(MaterialTheme.colorScheme.surface)
            .border(tokens.dimens.hairline, ProfilePalette.statsDividerColor),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = d.topBarHPadding)
                .size(d.backButtonSize)
                .clip(CircleShape)
                .background(tokens.colors.fieldInputBackground)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Geri",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(d.backButtonIconSize),
            )
        }

        val title = if (variant == ProfileVariant.EDIT) "Profili Düzenle" else "Profil"
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = d.topBarHPadding),
        ) {
            when (variant) {
                ProfileVariant.EDIT -> {
                    Text(
                        text = "Kaydet",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clickable(onClick = onSave)
                            .padding(tokens.spacing.xs),
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(d.settingsButtonSize)
                            .clip(CircleShape)
                            .background(tokens.colors.fieldInputBackground)
                            .clickable(onClick = onSettings),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "Ayarlar",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(d.settingsIconSize),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    profile: ProfileUiModel,
    onFollowers: () -> Unit,
    onFollowing: () -> Unit,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = ProfileDimens
    val tokens = OmniTokens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(d.headerCardShadow, RoundedCornerShape(d.headerCardRadius))
            .clip(RoundedCornerShape(d.headerCardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .padding(d.headerCardPadding),
        verticalArrangement = Arrangement.spacedBy(d.headerCardSpacing),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(d.rowSpacing),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(d.avatarSize)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(ProfilePalette.avatarGradient)),
                contentAlignment = Alignment.Center,
            ) {
                if (profile.profilePhotoUrl != null) {
                    AsyncImage(
                        model = profile.profilePhotoUrl,
                        contentDescription = profile.handle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        text = profile.avatarInitial,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(d.infoSpacing),
                modifier = Modifier.padding(top = d.infoTopPadding),
            ) {
                Text(
                    text = profile.handle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = ProfilePalette.starColor,
                        modifier = Modifier.size(d.starIconSize),
                    )
                    Text(
                        text = "${profile.karma} karma",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(d.statsRadius))
                .border(tokens.dimens.hairline, ProfilePalette.statsDividerColor, RoundedCornerShape(d.statsRadius)),
            verticalAlignment = Alignment.CenterVertically,
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Takipçi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            Box(
                modifier = Modifier
                    .width(d.statsDividerWidth)
                    .height(d.statsDividerHeight)
                    .background(ProfilePalette.statsDividerColor),
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Takip",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(d.editButtonHeight)
                .clip(RoundedCornerShape(d.editButtonRadius))
                .border(d.editButtonBorderWidth, MaterialTheme.colorScheme.primary, RoundedCornerShape(d.editButtonRadius))
                .clickable(onClick = onEditProfile),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Profili Düzenle",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ContentFilterChips(
    activeTab: ProfileTab,
    onTabChange: (ProfileTab) -> Unit,
) {
    val d = ProfileDimens
    val tokens = OmniTokens

    val tabs = listOf(
        ProfileTab.ALL to "Tümü",
        ProfileTab.TRIPS to "Geziler",
        ProfileTab.POSTS to "Paylaşımlar",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = d.chipRowHPadding, vertical = d.chipRowVPadding),
        horizontalArrangement = Arrangement.spacedBy(d.chipRowSpacing),
    ) {
        tabs.forEach { (tab, label) ->
            val isActive = tab == activeTab
            Box(
                modifier = Modifier
                    .height(d.chipHeight)
                    .clip(RoundedCornerShape(d.chipRadius))
                    .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                    .border(
                        d.chipBorderWidth,
                        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(d.chipRadius),
                    )
                    .clickable { onTabChange(tab) }
                    .padding(horizontal = d.chipHPadding),
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

@Composable
private fun ContentGrid(
    profile: ProfileUiModel,
    activeTab: ProfileTab,
    onTripTap: (String) -> Unit,
) {
    val d = ProfileDimens
    val showTrips = activeTab == ProfileTab.ALL || activeTab == ProfileTab.TRIPS
    val showPosts = activeTab == ProfileTab.ALL || activeTab == ProfileTab.POSTS

    Column(modifier = Modifier.padding(horizontal = d.contentHPadding)) {
        if (showTrips && profile.trips.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(d.contentSpacing),
                modifier = Modifier.fillMaxWidth(),
            ) {
                profile.trpsRow().forEach { trip ->
                    TripCardItem(
                        trip = trip,
                        onTap = { onTripTap(trip.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        if (showPosts && profile.posts.isNotEmpty()) {
            Spacer(Modifier.height(d.contentSpacing))
            profile.posts.forEach { post ->
                PostCardItem(post = post)
                Spacer(Modifier.height(d.contentSpacing))
            }
        }
    }
}

private fun ProfileUiModel.trpsRow(): List<ProfileTripUiModel> = trips.take(2)

@Composable
private fun TripCardItem(
    trip: ProfileTripUiModel,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = ProfileDimens

    Box(
        modifier = modifier
            .height(d.tripCardHeight)
            .clip(RoundedCornerShape(d.tripCardRadius))
            .background(Brush.linearGradient(colors = trip.gradientColors))
            .clickable(onClick = onTap),
    ) {
        if (trip.coverPhotoUrl != null) {
            AsyncImage(
                model = trip.coverPhotoUrl,
                contentDescription = trip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(d.tripCardPadding)
                .clip(RoundedCornerShape(OmniTokens.spacing.section))
                .background(Color.White.copy(alpha = 0.92f))
                .padding(horizontal = d.tripRatingPillHPad, vertical = d.tripRatingPillVPad),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.tripRatingPillSpacing),
        ) {
            Text(text = "\u2B50", style = MaterialTheme.typography.labelSmall)
            Text(
                text = "${trip.upvoteCount}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = trip.title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(d.tripCardPadding),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(d.tripCardPadding)
                .clip(RoundedCornerShape(d.tripTagRadius))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                .padding(horizontal = d.tripTagHPad, vertical = d.tripTagVPad),
        ) {
            Text(
                text = "gezi",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun PostCardItem(post: ProfilePostUiModel) {
    val d = ProfileDimens
    val tokens = OmniTokens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(d.postCardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(tokens.dimens.hairline, ProfilePalette.postCardBorderColor, RoundedCornerShape(d.postCardRadius))
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
                    text = post.handle.take(1).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
            Text(
                text = post.handle,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "\u00B7 ${post.timeAgo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Text(
            text = post.body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(d.postActionSpacing)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_upvote),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(d.postActionIconSize),
                )
                Text(
                    text = "${post.upvotes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(tokens.spacing.xs),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_comment),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(d.postActionIconSize),
                )
                Text(
                    text = "${post.comments}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@Composable
private fun EmptyContentState(modifier: Modifier = Modifier) {
    val d = ProfileDimens
    val tokens = OmniTokens

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(d.emptySpacing),
    ) {
        Box(
            modifier = Modifier
                .size(d.emptyIconContainer)
                .clip(CircleShape)
                .background(tokens.colors.fieldInputBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_trips),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(d.emptyIconSize),
            )
        }
        Text(
            text = "Henüz içerik yok",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "İlk gezini oluştur veya\nbir şeyler paylaş!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EditProfileBody(
    editBio: String,
    editLocation: String,
    selectedStyles: Set<String>,
    handle: String,
    onBioChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onStyleToggle: (String) -> Unit,
    onChangePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = ProfileDimens
    val tokens = OmniTokens
    val travelStyles = listOf("Macera", "Kültür", "Sahil", "Şehir", "Doğa", "Gastronomi")
    val bioMaxLength = 150

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(d.editBodyShadow, RoundedCornerShape(d.editBodyRadius))
            .clip(RoundedCornerShape(d.editBodyRadius))
            .background(MaterialTheme.colorScheme.surface)
            .padding(d.editBodyPadding),
        verticalArrangement = Arrangement.spacedBy(d.editBodySpacing),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(d.editFieldSpacing),
        ) {
            Box(
                modifier = Modifier
                    .size(d.editAvatarSize)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(ProfilePalette.avatarGradient)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = handle.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(d.editCameraSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(d.editCameraBorderWidth, MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(d.editCameraIconSize),
                    )
                }
            }
            Text(
                text = "\uD83D\uDCF7 Fotoğrafı Değiştir",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onChangePhoto),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(d.editFieldLabelSpacing)) {
            Text(
                text = "Kullanıcı adı",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(d.editFieldHeight)
                    .clip(RoundedCornerShape(d.editFieldRadius))
                    .background(ProfilePalette.locationFieldBg)
                    .padding(horizontal = d.editFieldHPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = handle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = "Kilitli",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(d.editLockIconSize),
                )
            }
            Text(
                text = "Kullanıcı adı değiştirilemez.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(d.editFieldLabelSpacing)) {
            Text(
                text = "Bio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            OutlinedTextField(
                value = editBio,
                onValueChange = { if (it.length <= bioMaxLength) onBioChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = d.bioMinHeight),
                placeholder = {
                    Text("Kendinden bahset...", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
                },
                shape = RoundedCornerShape(d.editFieldRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                maxLines = 4,
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Text(
                text = "${editBio.length}/$bioMaxLength",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.End),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(d.editFieldLabelSpacing)) {
            Text(
                text = "Konum",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(d.editFieldHeight)
                    .clip(RoundedCornerShape(d.editFieldRadius))
                    .border(d.editButtonBorderWidth, MaterialTheme.colorScheme.outline, RoundedCornerShape(d.editFieldRadius))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = d.editFieldHPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.editFieldSpacing),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(d.editLocationIconSize),
                )
                if (editLocation.isEmpty()) {
                    Text(
                        text = "Şehir, Ülke (ör. İstanbul, Türkiye)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                    )
                } else {
                    Text(
                        text = editLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(d.editStyleSectionSpacing)) {
            Text(
                text = "Seyahat Stili",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            val row1 = travelStyles.take(3)
            val row2 = travelStyles.drop(3)
            listOf(row1, row2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(d.editStyleRowSpacing)) {
                    row.forEach { style ->
                        val isSelected = style in selectedStyles
                        Box(
                            modifier = Modifier
                                .height(d.editStyleChipHeight)
                                .clip(RoundedCornerShape(d.editStyleChipRadius))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .border(
                                    tokens.dimens.hairline,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(d.editStyleChipRadius),
                                )
                                .clickable { onStyleToggle(style) }
                                .padding(horizontal = d.editStyleChipHPad),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = style,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// Preview Sample Data
// ─────────────────────────────────────────
private val sampleProfile = ProfileUiModel(
    handle = "@yigitalpbel",
    bio = "Seyahat tutkunu \uD83C\uDF0D",
    karma = 1240,
    followerCount = 142,
    followingCount = 89,
    avatarInitial = "Y",
    profilePhotoUrl = null,
    trips = listOf(
        ProfileTripUiModel("1", "Roma & Floransa Turu", null, 48, ProfilePalette.tripCardGradients[0]),
        ProfileTripUiModel("2", "Bali 2025", null, 28, ProfilePalette.tripCardGradients[1]),
    ),
    posts = listOf(
        ProfilePostUiModel(
            id = "1",
            handle = "@yigitalpbel",
            timeAgo = "2 saat önce",
            body = "Roma'da harika bir deneyimdi \uD83D\uDDFA Kesinlikle tavsiye ederim!",
            upvotes = 24,
            comments = 3,
            avatarColors = ProfilePalette.postAvatarGradients[0],
            profilePhotoUrl = null,
        ),
    ),
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewProfileWithContent() {
    MaterialTheme {
        ProfileScreen(
            uiState = ProfileUiState(
                contentState = UiState.Success(sampleProfile),
                variant = ProfileVariant.VIEW,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewProfileEmpty() {
    MaterialTheme {
        ProfileScreen(
            uiState = ProfileUiState(
                contentState = UiState.Success(sampleProfile.copy(trips = emptyList(), posts = emptyList())),
                variant = ProfileVariant.EMPTY,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewProfileEdit() {
    MaterialTheme {
        ProfileScreen(
            uiState = ProfileUiState(
                contentState = UiState.Success(sampleProfile),
                variant = ProfileVariant.EDIT,
                editBio = "Seyahat tutkunu \uD83C\uDF0D",
                editLocation = "İstanbul, Türkiye",
                editSelectedStyles = setOf("Macera", "Kültür"),
            ),
        )
    }
}
