package com.omniflow.ui.trips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.omniflow.core.designsystem.theme.OmniTextStyles
import com.omniflow.core.designsystem.theme.TripsDimens
import com.omniflow.core.designsystem.theme.TripsPalette

@Composable
fun TripCard(
    title: String,
    dateLabel: String,
    metaItems: List<String>,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    topLeftBadge: (@Composable () -> Unit)? = null,
    topRightContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    palette: TripsPalette = TripsPalette,
    dimens: TripsDimens = TripsDimens,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.cardHeight)
            .clip(RoundedCornerShape(dimens.cardRadius))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.linearGradient(gradient)),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.7f),
                        ),
                        startY = 0f,
                    ),
                ),
        )

        topLeftBadge?.let {
            Box(modifier = Modifier.align(Alignment.TopStart).padding(dimens.cardBadgePadding)) { it() }
        }
        topRightContent?.let {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(dimens.cardBadgePadding)) { it() }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = dimens.cardContentHPadding,
                    end = dimens.cardContentHPadding,
                    bottom = dimens.cardContentBottomPadding,
                ),
        ) {
            Text(title, color = Color.White, style = OmniTextStyles.tripCardTitle)
            Spacer(Modifier.height(dimens.savedCardTitleSpacing))
            Text(
                dateLabel,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(dimens.statChipSpacing))
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.cardMetaSpacing)) {
                metaItems.forEach { meta ->
                    Text(
                        meta,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                    )
                }
            }
        }
    }
}

@Composable
fun PillBadge(
    text: String,
    containerColor: Color,
    contentColor: Color = Color.White,
    dimens: TripsDimens = TripsDimens,
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(dimens.pillBadgeRadius))
            .padding(horizontal = dimens.pillBadgeHPadding, vertical = dimens.pillBadgeVPadding),
    ) {
        Text(
            text,
            color = contentColor,
            style = OmniTextStyles.settingsSectionTitle,
        )
    }
}

@Composable
fun SavedTripCard(
    trip: SavedTrip,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    palette: TripsPalette = TripsPalette,
    dimens: TripsDimens = TripsDimens,
) {
    Box(
        modifier = modifier
            .height(dimens.savedCardHeight)
            .clip(RoundedCornerShape(dimens.savedCardRadius))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.linearGradient(trip.gradient)),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            palette.savedCardOverlayDark.copy(alpha = 0.95f),
                            palette.savedCardOverlayDark.copy(alpha = 0.22f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = dimens.savedCardContentPadding,
                    end = dimens.savedCardContentPadding,
                    bottom = dimens.savedCardContentPadding,
                ),
        ) {
            Text(
                trip.title,
                color = Color.White,
                style = OmniTextStyles.tripSavedTitle,
            )
            Spacer(Modifier.height(dimens.savedCardTitleSpacing))
            Text(
                "@${trip.username}",
                color = palette.primary,
                style = OmniTextStyles.tripSavedUser,
            )
            Spacer(Modifier.height(dimens.savedCardUserSpacing))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${trip.destinationCount} · ${trip.dayCount} gün",
                    color = Color.White.copy(alpha = 0.46f),
                    style = OmniTextStyles.tripMetaSmall,
                )
                Text(
                    "${trip.likeCount}",
                    color = Color.White.copy(alpha = 0.46f),
                    style = OmniTextStyles.tripMetaSmall,
                )
            }
        }
    }
}

@Composable
fun TripsEmptyState(
    emoji: String,
    message: String,
    buttonText: String? = null,
    onButtonClick: () -> Unit = {},
    palette: TripsPalette = TripsPalette,
    dimens: TripsDimens = TripsDimens,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.listHPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(emoji, style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(dimens.tabRowVPadding))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = palette.muted,
        )
        if (buttonText != null) {
            Spacer(modifier = Modifier.height(dimens.tabRowVPadding))
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = palette.primary),
            ) {
                Text(buttonText, color = Color.White)
            }
        }
    }
}

@Composable
fun DraftTripsTabContent(
    trips: List<DraftTrip>,
    onTripClick: (String) -> Unit = {},
    onCreateTrip: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    palette: TripsPalette = TripsPalette,
    dimens: TripsDimens = TripsDimens,
) {
    if (trips.isEmpty() && !isLoading) {
        TripsEmptyState(
            emoji = "🗺️",
            message = "İlk gezini planla",
            buttonText = "Gezi Oluştur",
            onButtonClick = onCreateTrip,
            palette = palette,
            dimens = dimens,
        )
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimens.listHPadding,
            vertical = dimens.listVPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(dimens.listSpacing),
    ) {
        items(trips) { trip ->
            TripCard(
                title = trip.title,
                dateLabel = trip.dateLabel,
                    metaItems = listOf(
                        "${trip.peopleCount} kişi",
                        "${trip.destinationCount} destinasyon",
                    ),
                    gradient = trip.gradient,
                    topLeftBadge = {
                    PillBadge("Taslak", palette.warning, dimens = dimens)
                },
                topRightContent = {
                    Box(
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.18f),
                                RoundedCornerShape(dimens.pillBadgeRadius),
                            )
                            .padding(
                                horizontal = dimens.progressBadgeHPadding,
                                vertical = dimens.progressBadgeVPadding,
                            ),
                    ) {
                        Text(
                            "%${trip.progressPercent} hazır",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                },
                onClick = { onTripClick(trip.id) },
                palette = palette,
                dimens = dimens,
            )
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(dimens.tabRowVPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimens.addButtonIconSize),
                        strokeWidth = dimens.actionButtonBorderWidth,
                    )
                }
            }
        }
    }
}

@Composable
fun PublishedTripsTabContent(
    trips: List<PublishedTrip>,
    summary: PublishSummary,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    palette: TripsPalette = TripsPalette,
    dimens: TripsDimens = TripsDimens,
) {
    if (trips.isEmpty()) {
        TripsEmptyState(
            emoji = "📭",
            message = "Yayınlanmış gezi yok",
            palette = palette,
            dimens = dimens,
        )
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimens.listHPadding,
            vertical = dimens.listVPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(dimens.listSpacing),
    ) {
        items(trips) { trip ->
            TripCard(
                title = trip.title,
                dateLabel = trip.dateLabel,
                metaItems = listOf(
                    "${trip.peopleCount} kişi",
                    "${trip.destinationCount} destinasyon",
                    "${trip.dayCount} gün",
                ),
                gradient = trip.gradient,
                topLeftBadge = {
                    when (trip.badge) {
                        PublishedBadge.UPCOMING -> PillBadge(
                            trip.daysLeftLabel ?: "",
                            palette.success,
                            dimens = dimens,
                        )
                        PublishedBadge.COMPLETED -> PillBadge(
                            "Tamamlandı",
                            palette.muted,
                            dimens = dimens,
                        )
                        PublishedBadge.ARCHIVED -> PillBadge(
                            "Arşiv",
                            palette.archiveGray,
                            dimens = dimens,
                        )
                    }
                },
                topRightContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(dimens.statChipSpacing)) {
                        StatChip("${trip.rating}", palette, dimens)
                        StatChip("${trip.forkCount}", palette, dimens)
                    }
                },
                palette = palette,
                dimens = dimens,
            )
        }

        item {
            PublishSummaryCard(summary, palette, dimens)
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(dimens.tabRowVPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimens.addButtonIconSize),
                        strokeWidth = dimens.actionButtonBorderWidth,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    text: String,
    palette: TripsPalette,
    dimens: TripsDimens,
) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(dimens.pillBadgeRadius))
            .padding(horizontal = dimens.statChipHPadding, vertical = dimens.statChipVPadding),
    ) {
        Text(
            text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun PublishSummaryCard(
    summary: PublishSummary,
    palette: TripsPalette,
    dimens: TripsDimens,
) {
    Card(
        shape = RoundedCornerShape(dimens.summaryCardRadius),
        colors = CardDefaults.cardColors(containerColor = palette.card),
        elevation = CardDefaults.cardElevation(dimens.summaryElevation),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            Modifier.padding(
                horizontal = dimens.summaryCardHPadding,
                vertical = dimens.summaryCardVPadding,
            ),
        ) {
            Text(
                "YAYIN ÖZETİ",
                color = palette.muted,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(dimens.summarySpacer))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryStat(summary.routeCount.toString(), "Rota", palette, dimens)
                SummaryStat(summary.viewCount.toString(), "Görüntülenme", palette, dimens)
                SummaryStat(summary.forkCount.toString(), "Çatallanma", palette, dimens)
                SummaryStat(summary.avgRating.toString(), "Ort. Puan", palette, dimens)
            }
        }
    }
}

@Composable
private fun SummaryStat(
    value: String,
    label: String,
    palette: TripsPalette,
    dimens: TripsDimens,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = palette.ink,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
            color = palette.muted,
        )
    }
}

@Composable
fun SavedTripsTabContent(
    trips: List<SavedTrip>,
    collections: List<String>,
    selectedCollection: String,
    onFilterSelected: (String) -> Unit,
    onAddCollection: () -> Unit = {},
    onExplore: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    palette: TripsPalette = TripsPalette,
    dimens: TripsDimens = TripsDimens,
) {
    if (trips.isEmpty() && collections.isNotEmpty()) {
        TripsEmptyState(
            emoji = "💾",
            message = "Kaydettiğin gezi yok",
            buttonText = "Keşfet",
            onButtonClick = onExplore,
            palette = palette,
            dimens = dimens,
        )
        return
    }
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = dimens.filterRowHPadding,
                    vertical = dimens.filterRowVPadding,
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(dimens.filterRowSpacing),
            ) {
                items(collections) { collection ->
                    val selected = collection == selectedCollection
                    FilterChipPill(
                        text = collection,
                        selected = selected,
                        onClick = { onFilterSelected(collection) },
                        palette = palette,
                        dimens = dimens,
                    )
                }
            }
            Spacer(Modifier.width(dimens.addButtonLeftSpacing))
            Box(
                modifier = Modifier
                    .size(dimens.addButtonSize)
                    .clip(CircleShape)
                    .background(palette.primary)
                    .clickable(onClick = onAddCollection),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Koleksiyona ekle",
                    tint = Color.White,
                    modifier = Modifier.size(dimens.addButtonIconSize),
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                horizontal = dimens.gridHPadding,
                vertical = dimens.gridVPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(dimens.gridSpacing),
            verticalArrangement = Arrangement.spacedBy(dimens.gridSpacing),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(trips.size) { index ->
                SavedTripCard(trip = trips[index], palette = palette, dimens = dimens)
            }
        }
    }
}

@Composable
private fun FilterChipPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    palette: TripsPalette,
    dimens: TripsDimens,
) {
    Box(
        modifier = Modifier
            .height(dimens.filterChipHeight)
            .clip(RoundedCornerShape(dimens.filterChipRadius))
            .background(if (selected) palette.primary else Color.Transparent)
            .then(
                if (!selected) {
                    Modifier.border(
                        dimens.filterChipBorderWidth,
                        palette.border,
                        RoundedCornerShape(dimens.filterChipRadius),
                    )
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = dimens.filterChipHPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) Color.White else palette.muted,
            style = OmniTextStyles.tripFilterChip,
        )
    }
}

@Composable
fun MyTripsScreen(
    uiState: MyTripsUiState = MyTripsUiState(),
    paddingValues: PaddingValues = PaddingValues(TripsDimens.zero),
    onTabSelected: (MyTripsTab) -> Unit = {},
    onTripClick: (String) -> Unit = {},
    onCreateTrip: () -> Unit = {},
    onExplore: () -> Unit = {},
    onFilterSelected: (String) -> Unit = {},
    onAddCollection: () -> Unit = {},
) {
    val p = TripsPalette
    val d = TripsDimens

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        containerColor = p.bgScreen,
        topBar = {
            Column {
                Text(
                    "My Trips",
                    color = p.ink,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .padding(start = d.titlePadStart, top = d.titlePadTop),
                )
                MyTripsTabRow(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected,
                    palette = p,
                    dimens = d,
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(paddingValues)) {
            when (uiState.selectedTab) {
                MyTripsTab.DRAFT -> DraftTripsTabContent(
                    trips = uiState.draftTrips,
                    onTripClick = onTripClick,
                    onCreateTrip = onCreateTrip,
                    isLoading = uiState.isDraftsLoading,
                    palette = p,
                    dimens = d,
                )
                MyTripsTab.PUBLISHED -> PublishedTripsTabContent(
                    trips = uiState.publishedTrips,
                    summary = uiState.publishSummary,
                    isLoading = uiState.isPublishedLoading,
                    palette = p,
                    dimens = d,
                )
                MyTripsTab.SAVED -> SavedTripsTabContent(
                    trips = uiState.savedTrips,
                    collections = uiState.collections,
                    selectedCollection = uiState.selectedCollection,
                    onFilterSelected = onFilterSelected,
                    onAddCollection = onAddCollection,
                    onExplore = onExplore,
                    isLoading = uiState.isSavedLoading,
                    palette = p,
                    dimens = d,
                )
            }
        }
    }
}

@Composable
private fun MyTripsTabRow(
    selectedTab: MyTripsTab,
    onTabSelected: (MyTripsTab) -> Unit,
    palette: TripsPalette,
    dimens: TripsDimens,
) {
    Row(
        modifier = Modifier.padding(
            horizontal = dimens.tabRowHPadding,
            vertical = dimens.tabRowVPadding,
        ),
        horizontalArrangement = Arrangement.spacedBy(dimens.tabRowSpacing),
    ) {
        MyTripsTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Box(
                modifier = Modifier
                    .height(dimens.tabHeight)
                    .clip(RoundedCornerShape(dimens.pillBadgeRadius))
                    .background(if (selected) palette.ink else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = dimens.tabHPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    tab.label,
                    color = if (selected) Color.White else palette.muted,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}
