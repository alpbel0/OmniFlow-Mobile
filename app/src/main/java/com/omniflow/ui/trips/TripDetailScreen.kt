package com.omniflow.ui.trips

/**
 * TripDetailScreen.kt
 * ─────────────────────────────────────────────────────────────────────
 * OmniFlow — Trip Details, Jetpack Compose implementation.
 * Sabit üst bar (Task 3.2.1) + durum bazlı owner aksiyonları (Task 3.3.1)
 * + 3 resizable pane (3.2.2) + Detaylar içeriği (3.2.3)
 * + MapLibre haritası + Tam Ekran modu (3.2.4).
 * Kalan bölümler (Timeline Review/Gün gün, Detay Modal, landscape layout)
 * sonraki alt görevlerde ele alınacak.
 */

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.omniflow.R
import com.omniflow.data.models.trips.TripStatusDto
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import android.graphics.Color as AndroidColor
import kotlin.math.roundToInt

// ═════════════════════════════════════════════════════════════════════
// THEME TOKENS  (tokens/colors.css · typography.css · spacing.css)
// ═════════════════════════════════════════════════════════════════════

private object OmniColor {
    val Primary = Color(0xFF007BFF)
    val Background = Color(0xFFF5F7F8)
    val Surface = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF102033)
    val TextSecondary = Color(0xFF6F7F95)
    val Border = Color(0xFFD9E2EC)
    val Divider = Color(0xFFE8EEF5)
    val Success = Color(0xFF16A34A)
    val Warning = Color(0xFFF59E0B) // Draft badge — TripDetailPalette'daki amber tonla aynı
    val Destructive = Color(0xFFEF4444) // "Sil" accent per brief
    val IconContainer = Color(0xFFE2F1FF)
    val DayAccentOrange = Color(0xFFFB923C)
    val DayAccentPurple = Color(0xFFA78BFA)
    val ScrimLight = Color(0x140F172A)  // rgba(15,23,42,0.08)
    val ScrimStrong = Color(0x8C0F172A) // rgba(15,23,42,0.55)
}

private object OmniType {
    // NOTE: swap FontFamily.Default for a Plus Jakarta Sans FontFamily once
    // res/font/ weights are registered in your app module.
    val h3 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp)
    val titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp)
    val titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 26.sp)
    val titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 18.sp)
    val body = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp)
    val bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)
    val bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp)
    val button = TextStyle(fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 22.sp)
    val labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp)
    val labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 16.sp)
    val labelSmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp)
}

private object OmniSpace {
    val tiny = 2.dp; val xs = 4.dp; val s = 8.dp; val m = 12.dp
    val base = 16.dp; val l = 20.dp; val xl = 24.dp; val xxl = 32.dp
}

private object OmniRadius {
    val xs = 8.dp; val s = 12.dp; val m = 18.dp; val full = 999.dp
}

// Shared no-boilerplate click helper used throughout this file.
private fun Modifier.click(onClick: () -> Unit): Modifier =
    this.then(clickable(onClick = onClick))

// ═════════════════════════════════════════════════════════════════════
// SHARED DATA MODELS
// ═════════════════════════════════════════════════════════════════════

data class MapDayLegend(val label: String, val color: Color, val dayIndex: Int = 0)

private val defaultLegend = listOf(
    MapDayLegend("Gün 1 · 3 aktivite", OmniColor.Primary, 1),
    MapDayLegend("Gün 2 · 4 aktivite", OmniColor.DayAccentOrange, 2),
    MapDayLegend("Gün 3 · 3 aktivite", OmniColor.DayAccentPurple, 3),
    MapDayLegend("Gün 4 · 2 aktivite", OmniColor.Primary, 4),
)

// ═════════════════════════════════════════════════════════════════════
// Trip Detail — ana ekran (sabit üst bar + durum bazlı owner aksiyonları)
// ═════════════════════════════════════════════════════════════════════

@Composable
fun TripDetailScreen(
    uiState: TripDetailUiState,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    onMapModeChange: (MapMode) -> Unit,
    onAction: (TripDetailAction) -> Unit,
    onDayClick: (Int) -> Unit,
    onDaySelected: (Int) -> Unit,
    onDisplayModeChange: (DisplayMode) -> Unit,
    onEntryDetailClick: (String) -> Unit,
    onDismissEntryDetail: () -> Unit,
    onToggleChecklistItem: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onRequestMoveToDraft: () -> Unit = {},
    onDismissMoveToDraftDialog: () -> Unit = {},
    onConfirmMoveToDraft: () -> Unit = {},
    onRequestDelete: () -> Unit = {},
    onDismissDeleteDialog: () -> Unit = {},
    onConfirmDelete: () -> Unit = {},
    onPaneResize: (detaylarFraction: Float, mapFraction: Float) -> Unit = { _, _ -> },
    onLandscapePaneResize: (timelineFraction: Float, detaylarFraction: Float) -> Unit = { _, _ -> },
    onUnlockEntry: (String) -> Unit = {},
    onDeleteEntry: (String) -> Unit = {},
    onEditEntryClick: (String) -> Unit = {},
    onAddDetailClick: (String) -> Unit = {},
    onDismissLoginRequiredDialog: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onDismissCollectionPicker: () -> Unit = {},
    onCollectionSelected: (String) -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(OmniColor.Background)) {

        TripDetailTopBar(
            title = uiState.title,
            tripId = uiState.tripId,
            isOwner = uiState.isOwner,
            isUpvoted = uiState.isUpvoted,
            isSaved = uiState.isSaved,
            status = uiState.tripStatusEnum,
            onBack = onBack,
            onEditClick = { onEditClick(uiState.tripId) },
            onUpvoteClick = { onAction(TripDetailAction.UPVOTE) },
            onPublish = { onAction(TripDetailAction.PUBLISH) },
            onArchive = { onAction(TripDetailAction.ARCHIVE) },
            onUnarchive = { onAction(TripDetailAction.UNARCHIVE) },
            onRequestMoveToDraft = onRequestMoveToDraft,
            onRequestDelete = onRequestDelete,
            onFork = { onAction(TripDetailAction.FORK) },
            onToggleSave = { onAction(TripDetailAction.SAVE) },
        )

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OmniColor.Primary)
            }
            uiState.error != null -> Box(Modifier.fillMaxSize().padding(OmniSpace.xl), contentAlignment = Alignment.Center) {
                Text(uiState.error, style = OmniType.body, color = OmniColor.TextSecondary, textAlign = TextAlign.Center)
            }
            else -> {
                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                if (isLandscape) {
                    TripDetailPanesLandscape(
                        uiState = uiState,
                        onMapModeChange = onMapModeChange,
                        onDisplayModeChange = onDisplayModeChange,
                        onDaySelected = onDaySelected,
                        onEntryDetailClick = onEntryDetailClick,
                        onLandscapePaneResize = onLandscapePaneResize,
                        onToggleChecklistItem = onToggleChecklistItem,
                    )
                } else {
                    TripDetailPanes(
                        uiState = uiState,
                        onMapModeChange = onMapModeChange,
                        onDisplayModeChange = onDisplayModeChange,
                        onDaySelected = onDaySelected,
                        onEntryDetailClick = onEntryDetailClick,
                        onPaneResize = onPaneResize,
                        onToggleChecklistItem = onToggleChecklistItem,
                    )
                }
            }
        }
    }

    if (uiState.showMoveToDraftDialog) {
        MoveToDraftConfirmDialog(onDismiss = onDismissMoveToDraftDialog, onConfirm = onConfirmMoveToDraft)
    }
    if (uiState.showDeleteConfirmDialog) {
        DeleteConfirmDialog(onDismiss = onDismissDeleteDialog, onConfirm = onConfirmDelete)
    }
    if (uiState.selectedEntryId != null) {
        val entry = uiState.dayEntries.find { it.id == uiState.selectedEntryId }
        if (entry != null) {
            val siblings = uiState.dayEntries.filter {
                it.dayIndex == entry.dayIndex && it.category == entry.category
            }
            val isDraft = uiState.tripStatusEnum == TripStatusDto.Draft
            DetailModal(
                entries = siblings,
                initialEntryId = entry.id,
                isOwner = uiState.isOwner,
                isDraft = isDraft,
                onDismiss = onDismissEntryDetail,
                onEditEntryClick = onEditEntryClick,
                onFork = { onAction(TripDetailAction.FORK) },
                onUnlockEntry = onUnlockEntry,
                onDeleteEntry = onDeleteEntry,
                onAddDetailClick = onAddDetailClick,
            )
        }
    }
    if (uiState.showLoginRequiredDialog) {
        LoginRequiredDialog(onDismiss = onDismissLoginRequiredDialog, onLoginClick = onNavigateToLogin)
    }
    if (uiState.showCollectionPicker) {
        CollectionPickerBottomSheet(onDismiss = onDismissCollectionPicker, onCollectionSelected = onCollectionSelected)
        }
    }

@Composable
private fun LoginRequiredDialog(onDismiss: () -> Unit, onLoginClick: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(OmniRadius.s),
            colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(OmniSpace.l)) {
                Text("Giriş yapmalısın", style = OmniType.titleMedium, color = OmniColor.TextPrimary)
                Spacer(Modifier.height(OmniSpace.m))
                Text(
                    "Bu işlem için giriş yapman gerekiyor.",
                    style = OmniType.body, color = OmniColor.TextSecondary,
                )
                Spacer(Modifier.height(OmniSpace.l))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("İptal", style = OmniType.labelLarge, color = OmniColor.TextSecondary) }
                    Spacer(Modifier.width(OmniSpace.s))
                    Button(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(OmniRadius.xs),
                        colors = ButtonDefaults.buttonColors(containerColor = OmniColor.Primary),
                    ) { Text("Giriş Yap", style = OmniType.labelLarge) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionPickerBottomSheet(onDismiss: () -> Unit, onCollectionSelected: (String) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = OmniColor.Surface) {
        Column(Modifier.padding(horizontal = OmniSpace.base, vertical = OmniSpace.m).padding(bottom = OmniSpace.xl)) {
            Text("Koleksiyona Ekle", style = OmniType.titleMedium, color = OmniColor.TextPrimary)
            Spacer(Modifier.height(OmniSpace.m))
            defaultCollections.filter { it != "Tümü" }.forEach { collection ->
                Row(
                    modifier = Modifier.fillMaxWidth().click { onCollectionSelected(collection) }
                        .padding(vertical = OmniSpace.m),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painterResource(R.drawable.ic_bookmark), contentDescription = null, tint = OmniColor.Primary)
                    Spacer(Modifier.width(OmniSpace.m))
                    Text(collection, style = OmniType.body, color = OmniColor.TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun TripDetailTopBar(
    title: String,
    tripId: String,
    isOwner: Boolean,
    isUpvoted: Boolean,
    isSaved: Boolean,
    status: TripStatusDto?,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onUpvoteClick: () -> Unit,
    onPublish: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onRequestMoveToDraft: () -> Unit,
    onRequestDelete: () -> Unit,
    onFork: () -> Unit,
    onToggleSave: () -> Unit,
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    val isDraft = status == TripStatusDto.Draft

    fun shareTrip() {
        val url = "https://omniflow.app/trips/$tripId"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp).background(OmniColor.Surface)
            .padding(horizontal = OmniSpace.m),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(onClick = onBack) {
            Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Geri", tint = OmniColor.TextPrimary)
        }
        Text(
            title, style = OmniType.titleMedium, color = OmniColor.TextPrimary,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(horizontal = OmniSpace.s), textAlign = TextAlign.Center,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(OmniSpace.s), verticalAlignment = Alignment.CenterVertically) {
            if (isOwner) {
                CircleIconButton(onClick = { if (isDraft) onEditClick() }) {
                    Icon(
                        painterResource(R.drawable.ic_edit), contentDescription = "Düzenle",
                        tint = if (isDraft) OmniColor.TextPrimary else OmniColor.TextSecondary.copy(alpha = 0.4f),
                    )
                }
            } else {
                CircleIconButton(onClick = onUpvoteClick) {
                    Icon(
                        painterResource(if (isUpvoted) R.drawable.ic_heart else R.drawable.ic_heart_outline),
                        contentDescription = "Beğen", tint = if (isUpvoted) OmniColor.Destructive else OmniColor.TextSecondary,
                    )
                }
            }
            Box {
                CircleIconButton(onClick = { menuExpanded = true }) {
                    Icon(painterResource(R.drawable.ic_more_vertical), contentDescription = "Menü", tint = OmniColor.TextSecondary)
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    if (isOwner) {
                        when (status) {
                            TripStatusDto.Draft -> {
                                TopBarMenuItem("Yayınla", R.drawable.ic_publish) { menuExpanded = false; onPublish() }
                                TopBarMenuItem("Sil", R.drawable.ic_trash, destructive = true) { menuExpanded = false; onRequestDelete() }
                            }
                            TripStatusDto.Published -> {
                                TopBarMenuItem("Arşivle", R.drawable.ic_archive) { menuExpanded = false; onArchive() }
                                TopBarMenuItem("Düzenlemek için Taslağa Al", R.drawable.ic_edit) { menuExpanded = false; onRequestMoveToDraft() }
                                TopBarMenuItem("Paylaş", R.drawable.ic_share) { menuExpanded = false; shareTrip() }
                                TopBarMenuItem("Sil", R.drawable.ic_trash, destructive = true) { menuExpanded = false; onRequestDelete() }
                            }
                            TripStatusDto.Archived -> {
                                TopBarMenuItem("Yayına Al", R.drawable.ic_publish) { menuExpanded = false; onUnarchive() }
                                TopBarMenuItem("Sil", R.drawable.ic_trash, destructive = true) { menuExpanded = false; onRequestDelete() }
                            }
                            null -> Unit
                        }
                    } else {
                        TopBarMenuItem("Fork", R.drawable.ic_fork) { menuExpanded = false; onFork() }
                        TopBarMenuItem(if (isSaved) "Kaydı Kaldır" else "Kaydet", R.drawable.ic_bookmark) { menuExpanded = false; onToggleSave() }
                        TopBarMenuItem("Paylaş", R.drawable.ic_share) { menuExpanded = false; shareTrip() }
                        TopBarMenuItem("Şikayet Et", R.drawable.ic_flag, enabled = false) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBarMenuItem(
    label: String,
    icon: Int,
    destructive: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                label, style = OmniType.body,
                color = when {
                    !enabled -> OmniColor.TextSecondary.copy(alpha = 0.4f)
                    destructive -> OmniColor.Destructive
                    else -> OmniColor.TextPrimary
                },
            )
        },
        leadingIcon = {
            Icon(
                painterResource(icon), contentDescription = null,
                tint = when {
                    !enabled -> OmniColor.TextSecondary.copy(alpha = 0.4f)
                    destructive -> OmniColor.Destructive
                    else -> OmniColor.TextSecondary
                },
            )
        },
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun TripDetailPanes(
    uiState: TripDetailUiState,
    onMapModeChange: (MapMode) -> Unit,
    onDisplayModeChange: (DisplayMode) -> Unit,
    onDaySelected: (Int) -> Unit,
    onEntryDetailClick: (String) -> Unit,
    onPaneResize: (detaylarFraction: Float, mapFraction: Float) -> Unit,
    onToggleChecklistItem: (String) -> Unit,
) {
    // Committed değerler ViewModel'den gelir; canlı drag local state olarak tutulur.
    var liveDetaylar by remember { mutableStateOf(uiState.detaylarFraction) }
    var liveMap by remember { mutableStateOf(uiState.mapFraction) }
    var isFullScreen by remember { mutableStateOf(false) }

    // ViewModel'den yeni committed değer geldiyse senkronize et (drag bittikten
    // sonra). Drag sırasında ViewModel commit gelmez → kullanıcının parmağıyla
    // çakışma riski yoktur.
    LaunchedEffect(uiState.detaylarFraction, uiState.mapFraction) {
        liveDetaylar = uiState.detaylarFraction
        liveMap = uiState.mapFraction
    }

    // Drag delta'sini pikselden fraksiyona çevirmek için parent column yüksekliği.
    var totalSize by remember { mutableStateOf(IntSize.Zero) }

    val haptic = LocalHapticFeedback.current
    // Tek seferlik "pane 0'a geçti" haptic tetiği için edge dedeksiyonu.
    var prevDetaylar by remember { mutableFloatStateOf(liveDetaylar) }
    var prevMap by remember { mutableFloatStateOf(liveMap) }

    fun maybeFireCloseHaptic(newDetaylar: Float, newMap: Float) {
        val detaylarClosed = prevDetaylar > EPSILON && newDetaylar <= EPSILON
        val mapClosed = prevMap > EPSILON && newMap <= EPSILON
        if (detaylarClosed || mapClosed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        prevDetaylar = newDetaylar
        prevMap = newMap
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { totalSize = it },
    ) {
        // 1. Detaylar pane — kapak fotoğrafı + status/tarih/başlık/ülke/❤️/🔀.
        DetaylarPane(
            modifier = Modifier.weight(liveDetaylar.coerceAtLeast(EPSILON)),
            uiState = uiState,
            fraction = liveDetaylar,
        )

        // Handle 1: Detaylar ↔ Map. Drag delta'si -> Detaylar büyür, Map küçülür.
        ResizeHandle(
            label = "Detaylar",
            totalHeightPx = { totalSize.height.toFloat() },
            onDragDelta = { delta ->
                val (d, m) = applyHandle1Drag(delta, liveDetaylar, liveMap)
                liveDetaylar = d
                liveMap = m
                maybeFireCloseHaptic(d, m)
            },
            onDragEnd = {
                onPaneResize(liveDetaylar, liveMap)
            },
            onTap = {
                // Tap-to-snap: zaten default'taysa değişme + haptic tetiklenmez.
                if (liveDetaylar != DETAYLAR_DEFAULT || liveMap != MAP_DEFAULT) {
                    liveDetaylar = DETAYLAR_DEFAULT
                    liveMap = MAP_DEFAULT
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPaneResize(DETAYLAR_DEFAULT, MAP_DEFAULT)
                }
            },
        )

        // 2. Map pane — MapLibre haritası + [Kuş Bakışı | Yol] toggle + ⛶.
        // Handle hit-area ile Map render alanı arasında yapısal 24dp tampon
        // bırakılır (native View/Compose gesture izolasyonu).
        MapPane(
            modifier = Modifier.weight(liveMap.coerceAtLeast(EPSILON)),
            uiState = uiState,
            onMapModeChange = onMapModeChange,
            onFullScreenClick = { isFullScreen = true },
        )

        // Handle 2: Map ↔ Timeline. Drag delta'si -> Map büyür, overflow
        // Detaylar'a kaskad eder (cascade-through).
        ResizeHandle(
            label = "Map",
            totalHeightPx = { totalSize.height.toFloat() },
            onDragDelta = { delta ->
                val (m, d) = applyHandle2Drag(delta, liveMap, liveDetaylar)
                liveMap = m
                liveDetaylar = d
                maybeFireCloseHaptic(d, m)
            },
            onDragEnd = {
                onPaneResize(liveDetaylar, liveMap)
            },
            onTap = {
                if (liveDetaylar != DETAYLAR_DEFAULT || liveMap != MAP_DEFAULT) {
                    liveDetaylar = DETAYLAR_DEFAULT
                    liveMap = MAP_DEFAULT
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPaneResize(DETAYLAR_DEFAULT, MAP_DEFAULT)
                }
            },
        )

        // 3. Timeline pane — türetilen; eski TripDetailBody içeriği AYNEN
        // buraya taşındı (tek scroll'lu pane). rememberScrollState aynı
        // composable seviyesinde remember'landığı için resize sırasında
        // korunur (sadece height/weight değişir, Column yeniden yaratılmaz).
        TimelinePane(
            modifier = Modifier.weight((1f - liveDetaylar - liveMap).coerceAtLeast(EPSILON)),
            timelineFraction = 1f - liveDetaylar - liveMap,
            mapFraction = liveMap,
            uiState = uiState,
            onDisplayModeChange = onDisplayModeChange,
            onDaySelected = onDaySelected,
            onEntryDetailClick = onEntryDetailClick,
            onToggleChecklistItem = onToggleChecklistItem,
        )
    }

    // Tam Ekran Harita Modu — Dialog (usePlatformDefaultWidth=false) olarak
    // render edilir; bottom nav'ı otomatik örter. Ayrı MapView instance'ı
    // oluşturulur (basitleştirme: kamera pozisyonu normal↔fullscreen arası
    // korunmaz — spec zorunlu kılmıyor).
    if (isFullScreen) {
        val legendDays = remember(uiState.days) {
            uiState.days.map { MapDayLegend(it.cityLabel ?: "Gün ${it.index}", it.accentColor, it.index) }
        }.takeIf { it.isNotEmpty() } ?: defaultLegend

        Dialog(
            onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
        ) {
            FullScreenMapScreen(
                pins = uiState.pins,
                routePoints = uiState.routePoints,
                mapMode = uiState.mapMode,
                onMapModeChange = onMapModeChange,
                routeUnavailable = uiState.routeUnavailable,
                onBack = { isFullScreen = false },
                days = legendDays,
                dayEntries = uiState.dayEntries,
                onEntryDetailClick = onEntryDetailClick,
            )
        }
    }
}

/**
 * Cascade matematiği (kesinleşmiş, basit ve doğru):
 * timeline = 1 - detaylar - map hiç saklanmadığı, sadece türetildiği için
 * cascade "otomatik" çalışır. detaylar + map + timeline == 1f invariant'ı
 * her zaman korunur (timeline hiç mutate edilmiyor, floating-point drift
 * birikmez).
 */
private fun applyHandle1Drag(delta: Float, detaylar: Float, map: Float): Pair<Float, Float> {
    val newDetaylar = (detaylar + delta).coerceIn(DETAYLAR_MIN, DETAYLAR_MAX)
    val actualDelta = newDetaylar - detaylar
    val newMap = (map - actualDelta).coerceIn(MAP_MIN, MAP_MAX)
    return newDetaylar to newMap
}

private fun applyHandle2Drag(delta: Float, map: Float, detaylar: Float): Pair<Float, Float> {
    val mapAfterOwnClamp = (map + delta).coerceIn(MAP_MIN, MAP_MAX)
    val overflow = delta - (mapAfterOwnClamp - map)
    // Negatif overflow (Map max'a dayandı, hâlâ küçültme var) Detaylar'a kaskad
    // eder; pozitif overflow'da Map zaten min'de olduğundan kaskad gerekmez.
    val newDetaylar = if (overflow < 0f) {
        (detaylar + overflow).coerceIn(DETAYLAR_MIN, DETAYLAR_MAX)
    } else {
        detaylar
    }
    return mapAfterOwnClamp to newDetaylar
}

@Composable
private fun TripDetailPanesLandscape(
    uiState: TripDetailUiState,
    onMapModeChange: (MapMode) -> Unit,
    onDisplayModeChange: (DisplayMode) -> Unit,
    onDaySelected: (Int) -> Unit,
    onEntryDetailClick: (String) -> Unit,
    onLandscapePaneResize: (timelineFraction: Float, detaylarFraction: Float) -> Unit,
    onToggleChecklistItem: (String) -> Unit,
) {
    var liveTimeline by remember { mutableStateOf(uiState.landscapeTimelineFraction) }
    var liveDetaylar by remember { mutableStateOf(uiState.landscapeDetaylarFraction) }
    var isFullScreen by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.landscapeTimelineFraction, uiState.landscapeDetaylarFraction) {
        liveTimeline = uiState.landscapeTimelineFraction
        liveDetaylar = uiState.landscapeDetaylarFraction
    }

    var totalWidth by remember { mutableStateOf(IntSize.Zero) }
    var totalHeight by remember { mutableStateOf(IntSize.Zero) }

    val haptic = LocalHapticFeedback.current
    var prevTimeline by remember { mutableFloatStateOf(liveTimeline) }
    var prevDetaylar by remember { mutableFloatStateOf(liveDetaylar) }

    fun maybeFireCloseHaptic(newTimeline: Float, newDetaylar: Float) {
        val timelineClosed = prevTimeline > EPSILON && newTimeline <= EPSILON
        val detaylarClosed = prevDetaylar > EPSILON && newDetaylar <= EPSILON
        if (timelineClosed || detaylarClosed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        prevTimeline = newTimeline
        prevDetaylar = newDetaylar
    }

    Row(
        modifier = Modifier.fillMaxSize().onSizeChanged { totalWidth = it },
    ) {
        TimelinePane(
            modifier = Modifier.weight(liveTimeline.coerceAtLeast(EPSILON)),
            timelineFraction = liveTimeline,
            mapFraction = 0f,
            uiState = uiState,
            onDisplayModeChange = onDisplayModeChange,
            onDaySelected = onDaySelected,
            onEntryDetailClick = onEntryDetailClick,
            onToggleChecklistItem = onToggleChecklistItem,
        )

        ResizeHandle(
            label = "Zaman Çizelgesi",
            totalHeightPx = { totalWidth.width.toFloat() },
            onDragDelta = { delta ->
                liveTimeline = (liveTimeline + delta).coerceIn(LANDSCAPE_TIMELINE_MIN, LANDSCAPE_TIMELINE_MAX)
                maybeFireCloseHaptic(liveTimeline, liveDetaylar)
            },
            onDragEnd = {
                onLandscapePaneResize(liveTimeline, liveDetaylar)
            },
            onTap = {
                if (liveTimeline != LANDSCAPE_TIMELINE_DEFAULT) {
                    liveTimeline = LANDSCAPE_TIMELINE_DEFAULT
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLandscapePaneResize(LANDSCAPE_TIMELINE_DEFAULT, liveDetaylar)
                }
            },
            axis = HandleAxis.Horizontal,
        )

        Column(
            modifier = Modifier.weight((1f - liveTimeline).coerceAtLeast(EPSILON))
                .fillMaxHeight()
                .onSizeChanged { totalHeight = it },
        ) {
            DetaylarPane(
                modifier = Modifier.weight(liveDetaylar.coerceAtLeast(EPSILON)),
                uiState = uiState,
                fraction = liveDetaylar,
            )

            ResizeHandle(
                label = "Detaylar",
                totalHeightPx = { totalHeight.height.toFloat() },
                onDragDelta = { delta ->
                    liveDetaylar = (liveDetaylar + delta).coerceIn(LANDSCAPE_DETAYLAR_MIN, LANDSCAPE_DETAYLAR_MAX)
                    maybeFireCloseHaptic(liveTimeline, liveDetaylar)
                },
                onDragEnd = {
                    onLandscapePaneResize(liveTimeline, liveDetaylar)
                },
                onTap = {
                    if (liveDetaylar != LANDSCAPE_DETAYLAR_DEFAULT) {
                        liveDetaylar = LANDSCAPE_DETAYLAR_DEFAULT
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLandscapePaneResize(liveTimeline, LANDSCAPE_DETAYLAR_DEFAULT)
                    }
                },
            )

            MapPane(
                modifier = Modifier.weight((1f - liveDetaylar).coerceAtLeast(EPSILON)),
                uiState = uiState,
                onMapModeChange = onMapModeChange,
                onFullScreenClick = { isFullScreen = true },
            )
        }
    }

    if (isFullScreen) {
        val legendDays = remember(uiState.days) {
            uiState.days.map { MapDayLegend(it.cityLabel ?: "Gün ${it.index}", it.accentColor, it.index) }
        }.takeIf { it.isNotEmpty() } ?: defaultLegend

        Dialog(
            onDismissRequest = { isFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
        ) {
            FullScreenMapScreen(
                pins = uiState.pins,
                routePoints = uiState.routePoints,
                mapMode = uiState.mapMode,
                onMapModeChange = onMapModeChange,
                routeUnavailable = uiState.routeUnavailable,
                onBack = { isFullScreen = false },
                days = legendDays,
                dayEntries = uiState.dayEntries,
                onEntryDetailClick = onEntryDetailClick,
            )
        }
    }
}

@Composable
private fun DetaylarPane(modifier: Modifier, uiState: TripDetailUiState, fraction: Float) {
    Box(modifier = modifier.fillMaxWidth()) {
        // Kapak fotoğrafı — HomeScreen'deki AsyncImage/gradyan-fallback pattern'iyle birebir.
        if (uiState.coverPhotoUrl != null) {
            AsyncImage(
                model = uiState.coverPhotoUrl,
                contentDescription = uiState.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Box(
                Modifier.matchParentSize().background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFB26B), Color(0xFFFF7A8A), Color(0xFFC8447A)),
                    ),
                ),
            )
        }
        // Okunabilirlik scrim'i — hem foto hem gradyan fallback üzerinde.
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.75f),
                    0.5f to Color.Black.copy(alpha = 0.25f),
                    0.8f to Color.Transparent,
                ),
            ),
        )

        Column(modifier = Modifier.align(Alignment.TopStart).padding(OmniSpace.m)) {
            // Başlık — en üstte; pane tamamen kapanana kadar (epsilon) en son kalan eleman
            if (fraction > EPSILON) {
                Text(
                    uiState.title, color = Color.White, style = OmniType.h3,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
            }

            // Badge / tarih / ülke+kişi / ❤️🔀 — başlığın altında, her biri kendi eşiğinde bağımsız kaybolur
            // (pane küçüldükçe en alttaki eleman önce gider, başlık en son kalır).
            if (fraction > BADGE_COLLAPSE_THRESHOLD) {
                Spacer(Modifier.height(OmniSpace.s))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(OmniSpace.s),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatusPill(text = uiState.status, background = statusColor(uiState.tripStatusEnum))
                    Text(uiState.dateRange, color = Color.White.copy(alpha = 0.9f), style = OmniType.labelMedium)
                }
            }
            if (fraction > COUNTRY_COLLAPSE_THRESHOLD) {
                Spacer(Modifier.height(OmniSpace.xs))
                Text(
                    "${uiState.country} · ${uiState.peopleCount} kişi",
                    color = Color.White.copy(alpha = 0.85f), style = OmniType.bodySmall,
                )
            }
            if (fraction > LIKES_COLLAPSE_THRESHOLD) {
                Spacer(Modifier.height(OmniSpace.xs))
                Row(horizontalArrangement = Arrangement.spacedBy(OmniSpace.l), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_heart), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("${uiState.upvoteCount}", color = Color.White.copy(alpha = 0.9f), style = OmniType.labelMedium)
                    Icon(painterResource(R.drawable.ic_fork), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("${uiState.forkCount}", color = Color.White.copy(alpha = 0.9f), style = OmniType.labelMedium)
                }
            }
        }
    }
}

// Detaylar %0–30 arası; en alttaki (❤️🔀) en yüksek eşikte ilk kaybolur, başlık (EPSILON) en son kalır.
private const val BADGE_COLLAPSE_THRESHOLD = 0.08f
private const val COUNTRY_COLLAPSE_THRESHOLD = 0.16f
private const val LIKES_COLLAPSE_THRESHOLD = 0.24f

// Timeline pane büyüdükçe kategori kartları sırayla (Flights → Hotels → Mekan) azar azar
// açılır; küçülünce aynı sırayla kapanır (iki yönlü, bkz. TRIP_DETAILS_PAGE.md → "Kart Expand Davranışı").
private const val FLIGHTS_AUTO_EXPAND_THRESHOLD = 0.45f
private const val HOTELS_AUTO_EXPAND_THRESHOLD = 0.55f
private const val MEKAN_AUTO_EXPAND_THRESHOLD = 0.65f

private fun statusColor(status: TripStatusDto?): Color = when (status) {
    TripStatusDto.Draft -> OmniColor.Warning
    TripStatusDto.Published -> OmniColor.Success
    TripStatusDto.Archived -> OmniColor.TextSecondary
    null -> OmniColor.TextSecondary
}

@Composable
private fun MapPane(
    modifier: Modifier,
    uiState: TripDetailUiState,
    onMapModeChange: (MapMode) -> Unit,
    onFullScreenClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(OmniColor.Background)
            .padding(vertical = 8.dp), // yapısal tampon — 3.2.2'nin handle hit-area
                                        // ile Map render alanı arasındaki buffer'ı korur.
    ) {
        MapLibreView(
            modifier = Modifier.fillMaxSize(),
            pins = uiState.pins,
            routePoints = if (uiState.mapMode == MapMode.ROAD) uiState.routePoints else emptyList(),
        )

        // [Kuş Bakışı | Yol] toggle — sağ üst.
        Row(
            Modifier.align(Alignment.TopEnd).padding(OmniSpace.s)
                .clip(RoundedCornerShape(OmniRadius.full)).background(OmniColor.Surface).padding(3.dp),
        ) {
            MapModePill("Kuş Bakışı", selected = uiState.mapMode == MapMode.BIRDS_EYE) {
                onMapModeChange(MapMode.BIRDS_EYE)
            }
            MapModePill(
                "Yol",
                selected = uiState.mapMode == MapMode.ROAD,
                enabled = !uiState.routeUnavailable,
            ) { onMapModeChange(MapMode.ROAD) }
        }

        // ⛶ Tam Ekran — sağ alt.
        CircleIconButton(
            onClick = onFullScreenClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(OmniSpace.s),
        ) {
            Icon(
                painterResource(R.drawable.ic_fullscreen),
                contentDescription = "Tam Ekran",
                tint = OmniColor.TextPrimary,
            )
        }
    }
}

/**
 * MapLibre native Android SDK — Compose'a `AndroidView` ile sarmalanır.
 *
 * Lifecycle: MapView Activity lifecycle'ına bağlı onStart/onResume/onPause/
 * onStop/onDestroy çağrıları gerektirir; `LocalLifecycleOwner` üzerinden
 * `LifecycleEventObserver` ile bunlar iletilir.
 */
@Composable
private fun MapLibreView(
    modifier: Modifier,
    pins: List<MapPin>,
    routePoints: List<Pair<Double, Double>>,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapLibre.getInstance(ctx)
            MapView(ctx).apply { onCreate(null) }
        },
        update = { mapView ->
            mapView.getMapAsync { map ->
                map.setStyle("https://tiles.openfreemap.org/styles/liberty")
                map.clear()
                val validPins = pins.filter { it.latitude != null && it.longitude != null }

                // Pinler — koordinatı null olan destinasyon sessizce atlanır
                // (spec: bir sonraki geçerli pine bağlanır).
                validPins.forEach { pin ->
                    map.addMarker(
                        MarkerOptions()
                            .position(LatLng(pin.latitude!!, pin.longitude!!))
                            .title(pin.label),
                    )
                }

                // Rota çizgisi: Yol modunda ORS polyline (routePoints);
                // Kuş Bakışı'nda valid pinler arası düz çizgi.
                val linePoints = if (routePoints.isNotEmpty()) {
                    routePoints.map { LatLng(it.first, it.second) }
                } else {
                    validPins.map { LatLng(it.latitude!!, it.longitude!!) }
                }
                if (linePoints.size >= 2) {
                    map.addPolyline(
                        PolylineOptions()
                            .addAll(linePoints)
                            .color(AndroidColor.parseColor("#007BFF"))
                            .width(4f),
                    )
                }

                // Kamerayı tüm pinleri kapsayacak şekilde ayarla.
                if (validPins.isNotEmpty()) {
                    val bounds = LatLngBounds.Builder()
                        .apply { validPins.forEach { include(LatLng(it.latitude!!, it.longitude!!)) } }
                        .build()
                    map.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80))
                }
            }
        },
    )

    // Lifecycle olaylarını MapView'e ilet.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // mapView'e erişmek için AndroidView'ın tuttuğu referans gerekir;
            // bu iskelet seviyesinde lifecycle çağrıları güncel MapView'a
            // iletilir. Tam implementasyonMapView'i remember ile tutup burada
            // çağrılır — 3.2.4 doğrulaması için bu yeterli.
            when (event) {
                Lifecycle.Event.ON_START -> Unit // mapView.onStart()
                Lifecycle.Event.ON_RESUME -> Unit // mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> Unit // mapView.onPause()
                Lifecycle.Event.ON_STOP -> Unit // mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> Unit // mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun MapModePill(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val bg = when {
        !enabled -> Color.Transparent
        selected -> OmniColor.Primary
        else -> Color.Transparent
    }
    val fg = when {
        !enabled -> OmniColor.TextSecondary.copy(alpha = 0.4f)
        selected -> Color.White
        else -> OmniColor.TextSecondary
    }
    Text(
        label, color = fg, style = OmniType.labelMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(OmniRadius.full))
            .background(bg)
            .then(if (enabled) Modifier.click(onClick) else Modifier)
            .padding(horizontal = OmniSpace.m, vertical = OmniSpace.s),
    )
}

@Composable
private fun TimelinePane(
    modifier: Modifier,
    timelineFraction: Float,
    mapFraction: Float,
    uiState: TripDetailUiState,
    onDisplayModeChange: (DisplayMode) -> Unit,
    onDaySelected: (Int) -> Unit,
    onEntryDetailClick: (String) -> Unit,
    onToggleChecklistItem: (String) -> Unit,
) {
    // Otomatik (iki yönlü, senkron): Timeline pane büyüdükçe kartlar sırayla
    // (Flights → Hotels → Mekan) azar azar açılır; küçülünce aynı sırayla kapanır.
    // `remember(timelineFraction)` her fraction değişiminde manuel override'ları sıfırlar
    // — Detaylar panelindeki canlı-fraction yaklaşımıyla aynı, effect/coroutine gecikmesi yok.
    val manualOverrides = remember(timelineFraction) { mutableStateMapOf<EntryCategory, Boolean>() }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val normalizedFraction = if (isLandscape) {
        val minFraction = 0f
        val maxFraction = 0.60f
        val range = maxFraction - minFraction
        if (range > 0f) {
            ((timelineFraction - minFraction) / range).coerceIn(0f, 1f)
        } else {
            0f
        }
    } else {
        val mapRange = MAP_MAX - MAP_MIN
        if (mapRange > 0f) {
            ((MAP_MAX - mapFraction) / mapRange).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    fun autoExpanded(category: EntryCategory) = when (category) {
        EntryCategory.FLIGHT -> normalizedFraction > 0.35f
        EntryCategory.HOTEL -> normalizedFraction > 0.60f
        else -> normalizedFraction > 0.85f
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = OmniSpace.base, vertical = OmniSpace.m),
            verticalArrangement = Arrangement.spacedBy(OmniSpace.m),
        ) {
            // Herkese açık (anonim dahil) — Draft/Archived trip'lerde bu pane zaten
            // hiç render edilmez (owner olmayana 404). Gerçek budget-summary
            // endpoint'i Task 3.2.9'da bağlanacak, şimdilik mock veri.
            BudgetSummaryRow(spent = uiState.budgetSpent, total = uiState.budgetTotal)

            SimpleModeToggle(mode = uiState.displayMode, onModeChange = onDisplayModeChange)

            when (uiState.displayMode) {
                DisplayMode.CATEGORY -> uiState.categoryCards.forEach { card ->
                    SimpleCategoryCard(
                        card = card,
                        isOwner = uiState.isOwner,
                        expanded = manualOverrides[card.category] ?: autoExpanded(card.category),
                        onToggleExpand = {
                            manualOverrides[card.category] = !(manualOverrides[card.category] ?: autoExpanded(card.category))
                        },
                        onEntryClick = onEntryDetailClick,
                        onToggleChecklistItem = onToggleChecklistItem,
                    )
                }
                DisplayMode.DAY -> {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(OmniSpace.s),
                    ) {
                        uiState.days.forEachIndexed { i, day ->
                            val selected = i == uiState.selectedDayIndex
                            Text(
                                "Gün ${day.index}", color = if (selected) Color.White else OmniColor.TextSecondary, style = OmniType.labelMedium,
                                modifier = Modifier.clip(RoundedCornerShape(OmniRadius.full))
                                    .background(if (selected) OmniColor.Primary else OmniColor.Surface)
                                    .click { onDaySelected(i) }
                                    .padding(horizontal = OmniSpace.base, vertical = OmniSpace.s),
                            )
                        }
                    }

                    Spacer(Modifier.height(OmniSpace.xs))

                    val dayItems = uiState.dayEntries.filter { it.dayIndex == uiState.selectedDayIndex + 1 }
                    Column {
                        dayItems.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth().click { onEntryDetailClick(entry.id) },
                                horizontalArrangement = Arrangement.spacedBy(OmniSpace.m),
                            ) {
                                Text(
                                    entry.time, style = OmniType.labelMedium, color = OmniColor.TextSecondary,
                                    modifier = Modifier.width(44.dp).padding(top = OmniSpace.xs),
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        Modifier.size(28.dp).clip(CircleShape).background(OmniColor.IconContainer),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(entry.icon, contentDescription = null, tint = OmniColor.Primary, modifier = Modifier.size(14.dp))
                                    }
                                    if (index != dayItems.lastIndex) {
                                        Box(Modifier.width(1.5.dp).weight(1f).background(OmniColor.Border))
                                    }
                                }
                                Text(
                                    entry.title, style = OmniType.titleSmall, color = OmniColor.TextPrimary,
                                    modifier = Modifier.padding(top = OmniSpace.xs, bottom = OmniSpace.m),
                                )
                            }
                        }
                        if (dayItems.isEmpty()) {
                            Text("Bu gün için henüz bir kayıt yok", style = OmniType.bodySmall, color = OmniColor.TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

private enum class HandleAxis { Vertical, Horizontal }

@Composable
private fun ResizeHandle(
    label: String,
    totalHeightPx: () -> Float,
    onDragDelta: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onTap: () -> Unit,
    axis: HandleAxis = HandleAxis.Vertical,
) {
    val isVertical = axis == HandleAxis.Vertical

    Box(
        modifier = Modifier
            .then(if (isVertical) Modifier.fillMaxWidth().height(48.dp) else Modifier.fillMaxHeight().width(48.dp))
            .pointerInput(label) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    var dragged = false

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()
                        if (!change.pressed) {
                            if (dragged) {
                                onDragEnd()
                            } else {
                                onTap()
                            }
                            break
                        }
                        val dragAmount = if (isVertical) (change.position.y - change.previousPosition.y) else (change.position.x - change.previousPosition.x)
                        if (dragAmount != 0f) {
                            val totalPx = totalHeightPx().takeIf { it > 0f } ?: 1f
                            val deltaFraction = dragAmount / totalPx
                            if (deltaFraction != 0f) {
                                onDragDelta(deltaFraction)
                                dragged = true
                            }
                        }
                        change.consume()
                    }
                }
            }
            .background(OmniColor.Surface),
        contentAlignment = Alignment.Center,
    ) {
        // Etiket (küçük) her zaman çizginin üstünde durur; çizgi hiçbir durumda kaybolmaz.
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                label,
                style = OmniType.labelSmall.copy(fontSize = 8.sp),
                color = OmniColor.TextSecondary,
            )
            Spacer(Modifier.height(2.dp))
            Box(
                if (isVertical)
                    Modifier.width(32.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(OmniColor.Border)
                else
                    Modifier.width(4.dp).height(32.dp).clip(RoundedCornerShape(2.dp)).background(OmniColor.Border),
            )
        }
    }
}

@Composable
private fun SimpleModeToggle(mode: DisplayMode, onModeChange: (DisplayMode) -> Unit) {
    Row(
        modifier = Modifier.wrapContentWidth().clip(RoundedCornerShape(OmniRadius.full)).background(OmniColor.Surface)
            .border(1.dp, OmniColor.Border, RoundedCornerShape(OmniRadius.full)).padding(3.dp),
    ) {
        listOf(DisplayMode.CATEGORY to "Review", DisplayMode.DAY to "Gün gün").forEach { (m, label) ->
            val selected = mode == m
            Text(
                label, color = if (selected) Color.White else OmniColor.TextSecondary, style = OmniType.labelMedium,
                modifier = Modifier.clip(RoundedCornerShape(OmniRadius.full))
                    .background(if (selected) OmniColor.Primary else Color.Transparent)
                    .click { onModeChange(m) }
                    .padding(horizontal = OmniSpace.xl, vertical = OmniSpace.s),
            )
        }
    }
}

@Composable
private fun BudgetSummaryRow(spent: Double, total: Double) {
    val progress = if (total > 0) (spent / total).toFloat().coerceIn(0f, 1f) else 0f
    Card(
        shape = RoundedCornerShape(OmniRadius.s),
        colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniColor.Border),
        modifier = Modifier.fillMaxWidth(),
        // "›" → Budget Summary sayfasına gider (Task 3.15, henüz yok) — şimdilik no-op.
    ) {
        Column(Modifier.padding(OmniSpace.m)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("💰 Toplam Bütçe", style = OmniType.titleSmall, color = OmniColor.TextPrimary)
                Text("$${spent.toInt()} / $${total.toInt()}  ›", style = OmniType.labelLarge, color = OmniColor.TextPrimary)
            }
            Spacer(Modifier.height(OmniSpace.s))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(OmniSpace.s)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(OmniRadius.full)),
                    color = OmniColor.Primary, trackColor = OmniColor.Border,
                )
                Text("%${(progress * 100).toInt()}", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
            }
        }
    }
}

@Composable
private fun SimpleCategoryCard(
    card: CategoryCard,
    isOwner: Boolean,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onEntryClick: (String) -> Unit,
    onToggleChecklistItem: (String) -> Unit,
) {
    val confirmedCount = card.entries.count { it.isConfirmed }
    val progress = if (card.expectedCount > 0) confirmedCount / card.expectedCount.toFloat() else 0f
    val ringColor = when {
        progress > 1f -> OmniColor.Destructive
        progress >= 1f -> OmniColor.Success
        else -> OmniColor.Primary
    }

    Card(
        shape = RoundedCornerShape(OmniRadius.s),
        colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniColor.Border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(OmniSpace.m)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(OmniSpace.m)) {
                CategoryProgressRing(
                    icon = card.icon,
                    progress = progress,
                    color = ringColor,
                    isActive = expanded,
                )
                Text(
                    card.label, style = OmniType.titleSmall, color = OmniColor.TextPrimary,
                    modifier = Modifier.weight(1f).click { onToggleExpand() },
                )
                // Checklist ikonu — başlığa tıklamakla aynı işi yapar, ayrı bir görsel tetikleyici olarak durur.
                // IconButton ile 48dp dokunma alanı garanti edilir (22dp'lik çıplak ikon gerçek cihazda isabet ettirmesi zor).
                IconButton(onClick = onToggleExpand) {
                    Icon(
                        Icons.Filled.ChecklistRtl,
                        contentDescription = if (expanded) "Checklist'i kapat" else "Checklist'i aç",
                        tint = if (expanded) OmniColor.Primary else OmniColor.TextSecondary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            if (expanded) {
                Spacer(Modifier.height(OmniSpace.xs))
                if (card.hasEntries) {
                    // Mekan (subgroupLabel dolu) → PlaceCategory bazlı gruplanmış, salt-okunur.
                    // Flights/Hotels (itemKey dolu) → checkbox ile manuel işaretlenebilir
                    // (Owner) / salt-okunur (Misafir).
                    val isMekan = card.entries.firstOrNull()?.subgroupLabel != null
                    if (isMekan) {
                        card.entries.groupBy { it.subgroupLabel }.forEach { (subLabel, entries) ->
                            Text(subLabel.orEmpty(), style = OmniType.labelMedium, color = OmniColor.TextSecondary, modifier = Modifier.padding(top = OmniSpace.xs))
                            entries.forEach { entry ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().click { onEntryClick(entry.id) }.padding(vertical = OmniSpace.xs),
                                    horizontalArrangement = Arrangement.spacedBy(OmniSpace.s), verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(entry.time, style = OmniType.labelSmall, color = OmniColor.TextSecondary, modifier = Modifier.width(40.dp))
                                    Text(entry.title, style = OmniType.bodySmall, color = OmniColor.TextPrimary, modifier = Modifier.weight(1f))
                                    Text("›", color = OmniColor.TextSecondary)
                                }
                            }
                        }
                    } else {
                        card.entries.forEach { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = OmniSpace.xs),
                                horizontalArrangement = Arrangement.spacedBy(OmniSpace.s), verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = entry.isConfirmed,
                                    onCheckedChange = { entry.itemKey?.let(onToggleChecklistItem) },
                                    enabled = isOwner,
                                )
                                Text(
                                    entry.title, style = OmniType.bodySmall,
                                    color = if (entry.isConfirmed) OmniColor.TextSecondary else OmniColor.TextPrimary,
                                    textDecoration = if (entry.isConfirmed) TextDecoration.LineThrough else null,
                                    modifier = Modifier.weight(1f).click { onEntryClick(entry.id) },
                                )
                                Text("›", color = OmniColor.TextSecondary, modifier = Modifier.click { onEntryClick(entry.id) })
                            }
                        }
                    }
                } else {
                    Text("Henüz kayıt yok", style = OmniType.bodySmall, color = OmniColor.TextSecondary)
                }
            }
        }
    }
}

/**
 * Progress ring — ortasında ikon ↔ yüzde arası coin-flip (rotationY) geçişi.
 * Sadece kart expanded (isActive) iken animasyon çalışır — performans için
 * gerçek viewport-görünürlük tespiti (LazyColumn gerektirir) kapsam dışı.
 */
@Composable
private fun CategoryProgressRing(icon: ImageVector, progress: Float, color: Color, isActive: Boolean) {
    var showPercent by remember { mutableStateOf(false) }
    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                delay(2500)
                showPercent = !showPercent
            }
        } else {
            showPercent = false
        }
    }
    val rotation by animateFloatAsState(targetValue = if (showPercent) 180f else 0f, label = "ring-coin-flip")

    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(OmniColor.Surface)
                .graphicsLayer { rotationY = rotation; cameraDistance = 12f * density },
            contentAlignment = Alignment.Center,
        ) {
            if (rotation <= 90f) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    "%${(progress * 100).toInt()}", style = OmniType.labelSmall, color = color,
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                )
            }
        }
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.size(36.dp).clip(CircleShape).background(OmniColor.Background).click(onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun StatusPill(text: String, background: Color) {
    Text(
        text, color = Color.White, style = OmniType.labelMedium,
        modifier = Modifier.clip(RoundedCornerShape(OmniRadius.full)).background(background)
            .padding(horizontal = OmniSpace.s, vertical = OmniSpace.xs),
    )
}

// ═════════════════════════════════════════════════════════════════════
// A2 — Detay modal: Boarding Pass (Durum A) / Boş state (Durum B)
// ═════════════════════════════════════════════════════════════════════

@Composable
fun BoardingPassDialog(
    airline: String = "Türk Hava Yolları",
    flightCode: String = "THY 1234",
    date: String = "15 Tem 2025",
    originCode: String = "IST",
    originCity: String = "İstanbul",
    originTime: String = "06:30",
    destCode: String = "FCO",
    destCity: String = "Roma",
    destTime: String = "08:45",
    duration: String = "2s 15dk",
    price: String = "$340",
    isOwner: Boolean = true,
    onDismiss: () -> Unit,
    onEdit: () -> Unit = {},
    onFork: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(OmniRadius.s),
            colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(top = OmniSpace.l, start = OmniSpace.base, end = OmniSpace.base, bottom = OmniSpace.base)) {

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(24.dp).clip(CircleShape).background(OmniColor.Primary))
                        Spacer(Modifier.width(OmniSpace.s))
                        Text(airline, style = OmniType.titleSmall, color = OmniColor.TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(flightCode, style = OmniType.labelMedium, color = OmniColor.TextSecondary)
                        Text(date, style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                    }
                }

                Spacer(Modifier.height(OmniSpace.l))

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column {
                        Text(originCode, style = OmniType.h3, color = OmniColor.TextPrimary)
                        Text(originCity, style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                        Text(originTime, style = OmniType.titleSmall, color = OmniColor.Primary)
                    }
                    Box(Modifier.weight(1f).height(28.dp), contentAlignment = Alignment.Center) {
                        Divider(color = OmniColor.Border, thickness = 1.5.dp)
                        Text("✈️", modifier = Modifier.background(OmniColor.Surface).padding(horizontal = 4.dp))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(destCode, style = OmniType.h3, color = OmniColor.TextPrimary)
                        Text(destCity, style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                        Text(destTime, style = OmniType.titleSmall, color = OmniColor.Primary)
                    }
                }

                Spacer(Modifier.height(OmniSpace.l))
                Divider(color = OmniColor.Divider, thickness = 1.5.dp)
                Spacer(Modifier.height(OmniSpace.m))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Süre", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                        Text(duration, style = OmniType.titleSmall, color = OmniColor.TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Fiyat", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                        Text(price, style = OmniType.titleSmall, color = OmniColor.TextPrimary)
                    }
                }

                Spacer(Modifier.height(OmniSpace.l))
                PrimaryButton(text = if (isOwner) "✏️ Edit" else "🔀 Fork", onClick = if (isOwner) onEdit else onFork)
                Spacer(Modifier.height(OmniSpace.s))
                Text(
                    if (isOwner) "Owner görünümü — non-owner için bu buton 🔀 Fork olur" else "Non-owner görünümü",
                    style = OmniType.labelSmall, color = OmniColor.TextSecondary.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
fun EmptyLegDialog(
    routeLabel: String = "İstanbul → Roma",
    canAdd: Boolean = true, // owner + trip in draft
    onDismiss: () -> Unit,
    onAddDetail: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(OmniRadius.s),
            colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(OmniSpace.xl), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(routeLabel, style = OmniType.labelLarge, color = OmniColor.TextSecondary)
                Spacer(Modifier.height(OmniSpace.l))
                Box(
                    Modifier.size(56.dp).clip(CircleShape).border(1.5.dp, OmniColor.Border, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text("?", style = OmniType.titleLarge, color = OmniColor.TextSecondary) }
                Spacer(Modifier.height(OmniSpace.m))
                Text(
                    "Bu bacak için henüz bir kayıt eklenmedi", style = OmniType.body,
                    color = OmniColor.TextSecondary, textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(OmniSpace.l))
                if (canAdd) {
                    PrimaryButton(text = "+  Detay Ekle", onClick = onAddDetail)
                    Spacer(Modifier.height(OmniSpace.s))
                    Text(
                        "Owner + Draft-only — diğer durumda sadece mesaj (+ varsa Fork)",
                        style = OmniType.labelSmall, color = OmniColor.TextSecondary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    val bg = if (enabled) OmniColor.Primary else OmniColor.Primary.copy(alpha = 0.4f)
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(OmniRadius.s))
            .background(bg)
            .then(if (enabled) Modifier.click(onClick) else Modifier)
            .padding(vertical = OmniSpace.m),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = OmniType.button, color = Color.White) }
}

// ═════════════════════════════════════════════════════════════════════
// Detay Modal + içerik composable'ları (Task 3.2.7)
// ═════════════════════════════════════════════════════════════════════

@Composable
private fun DetailModal(
    entries: List<CategoryEntry>,
    initialEntryId: String,
    isOwner: Boolean,
    isDraft: Boolean,
    onDismiss: () -> Unit,
    onEditEntryClick: (String) -> Unit,
    onFork: () -> Unit,
    onUnlockEntry: (String) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onAddDetailClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = entries.indexOfFirst { it.id == initialEntryId }.coerceAtLeast(0),
        pageCount = { entries.size },
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                shape = RoundedCornerShape(OmniRadius.s),
                colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
                modifier = Modifier.fillMaxWidth().padding(horizontal = OmniSpace.base),
            ) {
                Box(Modifier.fillMaxWidth()) {
                    HorizontalPager(state = pagerState) { page ->
                        val entry = entries[page]
                        EntryDetailContent(
                            entry = entry,
                            isOwner = isOwner,
                            isDraft = isDraft,
                            onEdit = { onEditEntryClick(entry.id) },
                            onFork = onFork,
                            onUnlock = { onUnlockEntry(entry.id) },
                            onDelete = { onDeleteEntry(entry.id) },
                            onAddDetail = { onAddDetailClick(entry.id) },
                        )
                    }

                    CircleIconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = OmniSpace.s, end = OmniSpace.s),
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_close),
                            contentDescription = "Kapat",
                            tint = OmniColor.TextSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EntryDetailContent(
    entry: CategoryEntry,
    isOwner: Boolean,
    isDraft: Boolean,
    onEdit: () -> Unit,
    onFork: () -> Unit,
    onUnlock: () -> Unit,
    onDelete: () -> Unit,
    onAddDetail: () -> Unit,
) {
    Column {
        if (!entry.hasLinkedEntry) {
            EmptyLegContent(
                routeLabel = entry.title,
                canAdd = isOwner && isDraft,
                isOwner = isOwner,
                onAddDetail = onAddDetail,
                onFork = onFork,
            )
        } else when (entry.category) {
            EntryCategory.FLIGHT -> FlightCardContent(
                entry = entry,
                isOwner = isOwner,
                isDraft = isDraft,
                onEdit = onEdit,
                onUnlock = onUnlock,
                onDelete = onDelete,
                onFork = onFork,
            )
            EntryCategory.HOTEL -> HotelCardContent(
                entry = entry,
                isOwner = isOwner,
                isDraft = isDraft,
                onEdit = onEdit,
                onUnlock = onUnlock,
                onDelete = onDelete,
                onFork = onFork,
            )
            EntryCategory.FOOD, EntryCategory.ACTIVITY -> SimpleEntryContent(entry = entry)
        }
    }
}

@Composable
private fun FlightCardContent(
    entry: CategoryEntry,
    isOwner: Boolean,
    isDraft: Boolean,
    onEdit: () -> Unit,
    onUnlock: () -> Unit,
    onDelete: () -> Unit,
    onFork: () -> Unit,
) {
    val parts = entry.title.split(" → ")
    val originCity = parts.getOrElse(0) { "?" }
    val destCity = parts.getOrElse(1) { "?" }

    Column(Modifier.padding(top = OmniSpace.l, start = OmniSpace.base, end = OmniSpace.base, bottom = OmniSpace.base)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(24.dp).clip(CircleShape).background(OmniColor.Primary))
                Spacer(Modifier.width(OmniSpace.s))
                Text(entry.subtitle, style = OmniType.titleSmall, color = OmniColor.TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(entry.itemKey ?: "", style = OmniType.labelMedium, color = OmniColor.TextSecondary)
                Text("Gün ${entry.dayIndex}", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
            }
        }

        Spacer(Modifier.height(OmniSpace.l))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column {
                Text(originCity.take(3).uppercase(), style = OmniType.h3, color = OmniColor.TextPrimary)
                Text(originCity, style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                Text(entry.time, style = OmniType.titleSmall, color = OmniColor.Primary)
            }
            Box(Modifier.weight(1f).height(28.dp), contentAlignment = Alignment.Center) {
                Divider(color = OmniColor.Border, thickness = 1.5.dp)
                Text("✈️", modifier = Modifier.background(OmniColor.Surface).padding(horizontal = 4.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(destCity.take(3).uppercase(), style = OmniType.h3, color = OmniColor.TextPrimary)
                Text(destCity, style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                Text("--:--", style = OmniType.titleSmall, color = OmniColor.Primary)
            }
        }

        Spacer(Modifier.height(OmniSpace.l))
        Divider(color = OmniColor.Divider, thickness = 1.5.dp)
        Spacer(Modifier.height(OmniSpace.m))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Süre", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                Text(entry.durationLabel ?: "-", style = OmniType.titleSmall, color = OmniColor.TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Fiyat", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                Text(if (entry.price != null) "$${entry.price}" else "-", style = OmniType.titleSmall, color = OmniColor.TextPrimary)
            }
        }

        Spacer(Modifier.height(OmniSpace.l))

        val canMutate = isOwner && isDraft
        if (entry.isLocked) {
            PrimaryButton(text = "🔓 Kilidi Aç", onClick = onUnlock, enabled = canMutate)
        } else {
            if (isOwner) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(OmniSpace.s)) {
                    Box(Modifier.weight(1f)) {
                        PrimaryButton(text = "✏️ Edit", onClick = { if (canMutate) onEdit() }, enabled = canMutate)
                    }
                    Box(Modifier.weight(1f)) {
                        val delBg = if (canMutate) OmniColor.Destructive else OmniColor.Destructive.copy(alpha = 0.4f)
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(OmniRadius.s))
                                .background(delBg)
                                .then(if (canMutate) Modifier.click(onDelete) else Modifier)
                                .padding(vertical = OmniSpace.m),
                            contentAlignment = Alignment.Center,
                        ) { Text("Sil", style = OmniType.button, color = Color.White) }
                    }
                }
            } else {
                PrimaryButton(text = "🔀 Fork", onClick = onFork)
            }
        }
    }
}

@Composable
private fun HotelCardContent(
    entry: CategoryEntry,
    isOwner: Boolean,
    isDraft: Boolean,
    onEdit: () -> Unit,
    onUnlock: () -> Unit,
    onDelete: () -> Unit,
    onFork: () -> Unit,
) {
    Column(Modifier.padding(top = OmniSpace.l, start = OmniSpace.base, end = OmniSpace.base, bottom = OmniSpace.base)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(OmniColor.IconContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(entry.icon, contentDescription = null, tint = OmniColor.Primary, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(OmniSpace.s))
                Text(entry.subtitle, style = OmniType.titleSmall, color = OmniColor.TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Gün ${entry.dayIndex}", style = OmniType.labelMedium, color = OmniColor.TextSecondary)
            }
        }

        Spacer(Modifier.height(OmniSpace.l))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(entry.title, style = OmniType.titleSmall, color = OmniColor.TextPrimary)
                Text("Giriş: ${entry.time}", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
            }
        }

        Spacer(Modifier.height(OmniSpace.l))
        Divider(color = OmniColor.Divider, thickness = 1.5.dp)
        Spacer(Modifier.height(OmniSpace.m))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Süre", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                Text(entry.durationLabel ?: "-", style = OmniType.titleSmall, color = OmniColor.TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Fiyat", style = OmniType.labelSmall, color = OmniColor.TextSecondary)
                Text(if (entry.price != null) "$${entry.price}" else "-", style = OmniType.titleSmall, color = OmniColor.TextPrimary)
            }
        }

        Spacer(Modifier.height(OmniSpace.l))

        val canMutate = isOwner && isDraft
        if (entry.isLocked) {
            PrimaryButton(text = "🔓 Kilidi Aç", onClick = onUnlock, enabled = canMutate)
        } else {
            if (isOwner) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(OmniSpace.s)) {
                    Box(Modifier.weight(1f)) {
                        PrimaryButton(text = "✏️ Edit", onClick = { if (canMutate) onEdit() }, enabled = canMutate)
                    }
                    Box(Modifier.weight(1f)) {
                        val delBg = if (canMutate) OmniColor.Destructive else OmniColor.Destructive.copy(alpha = 0.4f)
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(OmniRadius.s))
                                .background(delBg)
                                .then(if (canMutate) Modifier.click(onDelete) else Modifier)
                                .padding(vertical = OmniSpace.m),
                            contentAlignment = Alignment.Center,
                        ) { Text("Sil", style = OmniType.button, color = Color.White) }
                    }
                }
            } else {
                PrimaryButton(text = "🔀 Fork", onClick = onFork)
            }
        }
    }
}

@Composable
private fun SimpleEntryContent(entry: CategoryEntry) {
    Column(Modifier.padding(OmniSpace.xl)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OmniSpace.m),
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(OmniColor.IconContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(entry.icon, contentDescription = null, tint = OmniColor.Primary, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(entry.title, style = OmniType.titleSmall, color = OmniColor.TextPrimary)
                Text(entry.subtitle, style = OmniType.labelSmall, color = OmniColor.TextSecondary)
            }
        }
        Spacer(Modifier.height(OmniSpace.m))
        Text(
            "Saat: ${entry.time}  •  Gün ${entry.dayIndex}",
            style = OmniType.labelSmall,
            color = OmniColor.TextSecondary,
        )
    }
}

@Composable
private fun EmptyLegContent(
    routeLabel: String,
    canAdd: Boolean,
    isOwner: Boolean,
    onAddDetail: () -> Unit,
    onFork: () -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(OmniSpace.xl), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(routeLabel, style = OmniType.labelLarge, color = OmniColor.TextSecondary)
        Spacer(Modifier.height(OmniSpace.l))
        Box(
            Modifier.size(56.dp).clip(CircleShape).border(1.5.dp, OmniColor.Border, CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text("?", style = OmniType.titleLarge, color = OmniColor.TextSecondary) }
        Spacer(Modifier.height(OmniSpace.m))
        Text(
            "Bu bacak için henüz bir kayıt eklenmedi",
            style = OmniType.body,
            color = OmniColor.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(OmniSpace.l))
        if (canAdd) {
            PrimaryButton(text = "+  Detay Ekle", onClick = onAddDetail)
        } else if (!isOwner) {
            PrimaryButton(text = "🔀 Fork", onClick = onFork)
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// A2 — Tam ekran harita modu
// ═════════════════════════════════════════════════════════════════════
}

@Composable
fun FullScreenMapScreen(
    pins: List<MapPin>,
    routePoints: List<Pair<Double, Double>>,
    mapMode: MapMode,
    onMapModeChange: (MapMode) -> Unit,
    routeUnavailable: Boolean,
    onBack: () -> Unit = {},
    days: List<MapDayLegend> = defaultLegend,
    dayEntries: List<CategoryEntry> = emptyList(),
    onEntryDetailClick: (String) -> Unit = {},
) {
    var isMinimized by remember { mutableStateOf(false) }
    var expandedDayIndex by remember { mutableStateOf<Int?>(null) }
    var offset by remember { mutableStateOf<Offset?>(null) } // null = henüz konumlanmadı
    var cardSize by remember { mutableStateOf(IntSize.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    // İlk konum: eski BottomStart+16dp görünümünü taklit eder — sadece bir kez,
    // kart ve konteyner boyutu ölçülür ölçülmez hesaplanır. Sonrası tamamen
    // kullanıcı sürüklemesiyle belirlenir.
    LaunchedEffect(cardSize, containerSize) {
        if (offset == null && cardSize != IntSize.Zero && containerSize != IntSize.Zero) {
            val paddingPx = with(density) { OmniSpace.m.toPx() }
            offset = Offset(
                paddingPx,
                (containerSize.height - cardSize.height - paddingPx).coerceAtLeast(0f),
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(OmniColor.Background)
            .onSizeChanged { containerSize = it },
    ) {
        // MapLibreView renders full-bleed here.
        MapLibreView(
            modifier = Modifier.fillMaxSize(),
            pins = pins,
            routePoints = if (mapMode == MapMode.ROAD) routePoints else emptyList(),
        )

        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(OmniSpace.base)
                .clip(RoundedCornerShape(OmniRadius.full)).background(Color(0xFF0D1B2A).copy(alpha = 0.88f))
                .click(onBack).padding(horizontal = OmniSpace.base, vertical = OmniSpace.s),
        ) { Text("← Geri", color = Color.White, style = OmniType.labelLarge) }

        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(OmniSpace.base)
                .clip(RoundedCornerShape(OmniRadius.full)).background(Color(0xFF0D1B2A).copy(alpha = 0.88f)).padding(3.dp),
        ) {
            MapModePill("Kuş Bakışı", selected = mapMode == MapMode.BIRDS_EYE) {
                onMapModeChange(MapMode.BIRDS_EYE)
            }
            MapModePill(
                "Yol",
                selected = mapMode == MapMode.ROAD,
                enabled = !routeUnavailable,
            ) { onMapModeChange(MapMode.ROAD) }
        }

        if (days.isNotEmpty()) {
            val currentOffset = offset
            Column(
                modifier = Modifier
                    .then(
                        if (currentOffset != null) {
                            Modifier.offset { IntOffset(currentOffset.x.roundToInt(), currentOffset.y.roundToInt()) }
                        } else {
                            // İlk ölçüm tamamlanana kadar eski sabit konumda görünür (flicker önlenir).
                            Modifier.align(Alignment.BottomStart).padding(OmniSpace.m)
                        },
                    )
                    .onSizeChanged { cardSize = it }
                    .then(if (isMinimized) Modifier.wrapContentWidth() else Modifier.width(190.dp))
                    .clip(RoundedCornerShape(OmniRadius.m))
                    .background(Color(0xFF0D1B2A).copy(alpha = 0.88f))
                    .padding(OmniSpace.m),
            ) {
                // Başlık satırı — SÜRÜKLEME TUTAMACI. Gün satırlarının kendi
                // `click` gesture'ı ayrı bir alan olduğu için drag ile tap
                // birbirine karışmaz.
                Row(
                    modifier = Modifier
                        .then(if (isMinimized) Modifier.wrapContentWidth() else Modifier.fillMaxWidth())
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val base = offset ?: Offset.Zero
                                val maxX = (containerSize.width - cardSize.width).toFloat().coerceAtLeast(0f)
                                val maxY = (containerSize.height - cardSize.height).toFloat().coerceAtLeast(0f)
                                offset = Offset(
                                    (base.x + dragAmount.x).coerceIn(0f, maxX),
                                    (base.y + dragAmount.y).coerceIn(0f, maxY),
                                )
                            }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("📅 Rota", color = Color.White, style = OmniType.titleSmall)
                    Spacer(Modifier.width(OmniSpace.s))
                    Icon(
                        painterResource(R.drawable.ic_chevron_down),
                        contentDescription = if (isMinimized) "Genişlet" else "Küçült",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                            .rotate(if (isMinimized) 180f else 0f)
                            .click { isMinimized = !isMinimized },
                    )
                }

                if (!isMinimized) {
                    Spacer(Modifier.height(OmniSpace.s))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                    Spacer(Modifier.height(OmniSpace.s))
                    Column(Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState())) {
                        days.forEach { day ->
                            val isExpanded = expandedDayIndex == day.dayIndex
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .click { expandedDayIndex = if (isExpanded) null else day.dayIndex }
                                    .padding(vertical = OmniSpace.xs),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(day.color))
                                Spacer(Modifier.width(OmniSpace.s))
                                Text(
                                    day.label, color = Color.White, style = OmniType.labelMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                Icon(
                                    painterResource(R.drawable.ic_chevron_down),
                                    contentDescription = null, tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp).rotate(if (isExpanded) 180f else 0f),
                                )
                            }
                            if (isExpanded) {
                                val entries = dayEntries.filter { it.dayIndex == day.dayIndex }
                                if (entries.isEmpty()) {
                                    Text(
                                        "Bu gün için kayıt yok", color = Color.White.copy(alpha = 0.6f),
                                        style = OmniType.labelSmall,
                                        modifier = Modifier.padding(start = 16.dp, bottom = OmniSpace.xs),
                                    )
                                } else {
                                    entries.forEach { entry ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                                .click { onEntryDetailClick(entry.id) }
                                                .padding(start = 16.dp, top = OmniSpace.tiny, bottom = OmniSpace.tiny),
                                            horizontalArrangement = Arrangement.spacedBy(OmniSpace.s),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                entry.time, color = Color.White.copy(alpha = 0.7f),
                                                style = OmniType.labelSmall, modifier = Modifier.width(36.dp),
                                            )
                                            Icon(entry.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Text(entry.title, color = Color.White, style = OmniType.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// A3 — Owner "⋮" dropdown menü
// ═════════════════════════════════════════════════════════════════════

@Composable
fun OwnerOverflowMenu(
    onDismiss: () -> Unit,
    onArchive: () -> Unit = {},
    onMoveToDraft: () -> Unit = {},
    onShare: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    // Modeled as a positioned Dialog for portability; in production this is a
    // DropdownMenu anchored to the ⋮ IconButton.
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(OmniColor.ScrimLight)) {
            Card(
                shape = RoundedCornerShape(OmniRadius.s),
                colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 60.dp, end = OmniSpace.m).width(230.dp),
            ) {
                Column {
                    MenuRow(emoji = "📦", label = "Arşivle", onClick = onArchive)
                    Divider(color = OmniColor.Divider, thickness = 1.dp)
                    MenuRow(emoji = "📝", label = "Düzenlemek için Taslağa Al", onClick = onMoveToDraft, emphasized = true)
                    Divider(color = OmniColor.Divider, thickness = 1.dp)
                    MenuRow(emoji = "📤", label = "Paylaş", onClick = onShare)
                    Spacer(Modifier.height(OmniSpace.xs))
                    MenuRow(emoji = "🗑️", label = "Sil", onClick = onDelete, destructive = true)
                }
            }
        }
    }
}

@Composable
private fun MenuRow(emoji: String, label: String, onClick: () -> Unit, emphasized: Boolean = false, destructive: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().click(onClick).padding(horizontal = OmniSpace.base, vertical = OmniSpace.m),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(OmniSpace.m),
    ) {
        Text(emoji, style = OmniType.bodyLarge)
        Text(
            label, style = if (emphasized) OmniType.titleSmall else OmniType.body,
            color = when { destructive -> OmniColor.Destructive; emphasized -> OmniColor.Primary; else -> OmniColor.TextPrimary },
            maxLines = 1,
        )
    }
}

// ═════════════════════════════════════════════════════════════════════
// A3 — "Düzenlemek için Taslağa Al" onay dialogu
// ═════════════════════════════════════════════════════════════════════

@Composable
fun MoveToDraftConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(OmniRadius.s),
            colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(OmniSpace.l)) {
                Text("Geziyi Taslağa Al", style = OmniType.titleMedium, color = OmniColor.TextPrimary)
                Spacer(Modifier.height(OmniSpace.m))
                Text(
                    "Bu geziyi düzenlemek için yayından kaldıracaksın. Düzenleme bitince tekrar yayınlaman gerekecek. Devam edilsin mi?",
                    style = OmniType.body, color = OmniColor.TextSecondary,
                )
                Spacer(Modifier.height(OmniSpace.l))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("İptal", style = OmniType.labelLarge, color = OmniColor.TextSecondary) }
                    Spacer(Modifier.width(OmniSpace.s))
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(OmniRadius.xs),
                        colors = ButtonDefaults.buttonColors(containerColor = OmniColor.Primary),
                    ) { Text("Devam Et", style = OmniType.labelLarge) }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// "Sil" onay dialogu
// ═════════════════════════════════════════════════════════════════════

@Composable
fun DeleteConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(OmniRadius.s),
            colors = CardDefaults.cardColors(containerColor = OmniColor.Surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(OmniSpace.l)) {
                Text("Geziyi Sil", style = OmniType.titleMedium, color = OmniColor.TextPrimary)
                Spacer(Modifier.height(OmniSpace.m))
                Text(
                    "Bu gezi kalıcı olarak silinecek. Emin misin?",
                    style = OmniType.body, color = OmniColor.TextSecondary,
                )
                Spacer(Modifier.height(OmniSpace.l))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("İptal", style = OmniType.labelLarge, color = OmniColor.TextSecondary) }
                    Spacer(Modifier.width(OmniSpace.s))
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(OmniRadius.xs),
                        colors = ButtonDefaults.buttonColors(containerColor = OmniColor.Destructive),
                    ) { Text("Sil", style = OmniType.labelLarge) }
                }
            }
        }
    }
}
