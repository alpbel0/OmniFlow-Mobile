package com.omniflow.ui.trips

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import com.omniflow.core.designsystem.theme.TripDetailPalette as P
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripStatusDto
import com.omniflow.data.local.MockCityCoordinates
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

private val trLocale = Locale("tr")

private const val HOTEL_NIGHTS_PER_DESTINATION = 2 // TODO: B0.9/gerçek TripDestination tarihleri gelince hesaplanacak
private const val MEKAN_ACTIVITIES_PER_DAY = 5 // TODO: Wizard Tempo alanı gelince (Slow≈3/Moderate≈5/Fast≈7) dinamikleşecek

fun TripModel.toTripDetailUiState(currentUserId: String?): TripDetailUiState {
    val dayList = buildTripDays()
    val cards = buildCategoryCards(dayList)
    val mockBudgetTotal = 1000.0 + (abs(id.hashCode()) % 4000)
    val mockBudgetSpent = mockBudgetTotal * ((abs(id.hashCode() * 31) % 80 + 10) / 100.0)
    return TripDetailUiState(
        tripId = id,
        title = title,
        status = when (status) {
            TripStatusDto.Draft -> "Taslak"
            TripStatusDto.Published -> "Yayında"
            TripStatusDto.Archived -> "Arşiv"
        },
        tripStatusEnum = status,
        coverPhotoUrl = coverPhotoUrl,
        dateRange = formatDateRange(startDate, endDate),
        country = (listOfNotNull(originCountry) + destinationList).joinToString(" → "),
        peopleCount = personCount,
        dayCount = computeDayCount(startDate, endDate),
        activityCount = dayList.size * 3,
        progressPercent = (abs(id.hashCode()) % 41) + 20,
        upvoteCount = upvoteCount,
        forkCount = forkCount,
        isUpvoted = isUpvoted,
        isSaved = isSaved,
        budgetTotal = mockBudgetTotal,
        budgetSpent = mockBudgetSpent,
        pins = destinationList.mapIndexed { i, city ->
            val (lat, lng) = MockCityCoordinates.lookup(city) ?: (null to null)
            MapPin(
                label = city,
                color = P.dayColors[i % P.dayColors.size],
                latitude = lat,
                longitude = lng,
            )
        },
        days = dayList,
        isOwner = ownerId == "mock-user" || (currentUserId != null && ownerId == currentUserId),
        categoryCards = cards,
        dayEntries = cards.flatMap { it.entries }.sortedWith(compareBy({ it.dayIndex }, { it.time })),
    )
}

private fun timeOf(hour: Int, minute: Int): String = "%02d:%02d".format(hour.coerceIn(0, 23), minute)

private val mekanSubcategories = listOf(
    "Food" to Icons.Default.Restaurant,
    "Museum" to Icons.Default.Museum,
    "Shopping" to Icons.Default.ShoppingBag,
    "Nature" to Icons.Default.Park,
)

/**
 * Flights=leg bazlı (origin + destinationList ardışık geçişleri), Hotels=gece bazlı
 * (destinasyon başına sabit varsayım), Mekan=Tempo bazlı (sabit varsayım) + PlaceCategory
 * alt grup — TRIP_DETAILS_PAGE.md → Timeline Bölümü / Review Modu. itemKey'ler backend'in
 * henüz döndürmediği gerçek TripDestination.Id yerine şehir adı bazlı (bkz. B0.13).
 */
private fun TripModel.buildCategoryCards(days: List<TripDay>): List<CategoryCard> {
    val dayCount = days.size.coerceAtLeast(1)
    val route = listOfNotNull(origin) + destinationList

    val flightEntries = route.zipWithNext().mapIndexed { index, (from, to) ->
        val key = "flight-leg:$from:$to"
        CategoryEntry(
            id = key,
            dayIndex = (index + 1).coerceAtMost(dayCount),
            title = "$from → $to",
            subtitle = "Uçuş/Ulaşım",
            icon = Icons.Default.Flight,
            time = timeOf(7 + index * 4, 30),
            itemKey = key,
            isConfirmed = false,
            category = EntryCategory.FLIGHT,
            hasLinkedEntry = index % 3 != 0,
            isLocked = index % 3 != 0,
            price = 120.0 + index * 45,
            durationLabel = "${1 + index % 3}s ${(index * 15) % 60}dk",
        )
    }

    val hotelEntries = destinationList.flatMapIndexed { destIndex, city ->
        (1..HOTEL_NIGHTS_PER_DESTINATION).map { night ->
            val key = "hotel-night:$city:$night"
            CategoryEntry(
                id = key,
                dayIndex = (destIndex + 1).coerceAtMost(dayCount),
                title = "$city, Gece $night",
                subtitle = city,
                icon = Icons.Default.Hotel,
                time = timeOf(14, 0),
                itemKey = key,
                isConfirmed = false,
                category = EntryCategory.HOTEL,
                hasLinkedEntry = night == 1,
                isLocked = night == 1,
                price = 80.0 + destIndex * 20,
                durationLabel = "$night gece",
            )
        }
    }

    val mekanExpected = dayCount * MEKAN_ACTIVITIES_PER_DAY
    val mekanEntries = (0 until mekanExpected).map { n ->
        val (subLabel, subIcon) = mekanSubcategories[n % mekanSubcategories.size]
        CategoryEntry(
            id = "mekan-$n",
            dayIndex = (n % dayCount) + 1,
            title = "$subLabel #${n / mekanSubcategories.size + 1}",
            subtitle = subLabel,
            icon = subIcon,
            time = timeOf(9 + (n % 6) * 2, 0),
            itemKey = null,
            isConfirmed = true,
            subgroupLabel = subLabel,
            category = EntryCategory.FOOD,
            hasLinkedEntry = true,
            isLocked = false,
        )
    }

    return listOf(
        CategoryCard(EntryCategory.FLIGHT, "Uçuş", Icons.Default.Flight, flightEntries, flightEntries.isNotEmpty(), flightEntries.size),
        CategoryCard(EntryCategory.HOTEL, "Otel", Icons.Default.Hotel, hotelEntries, hotelEntries.isNotEmpty(), hotelEntries.size),
        CategoryCard(EntryCategory.FOOD, "Mekan", Icons.Default.Restaurant, mekanEntries, mekanEntries.isNotEmpty(), mekanExpected),
    )
}

private fun TripModel.buildTripDays(): List<TripDay> {
    val startDate = startDate?.let { LocalDate.parse(it) }
    return destinationList.mapIndexed { index, city ->
        val dateLabel = if (startDate != null) {
            val d = startDate.plusDays((index * 2).toLong())
            val dow = d.dayOfWeek.getDisplayName(TextStyle.FULL, trLocale)
                .replaceFirstChar { it.uppercase() }
            val month = d.month.getDisplayName(TextStyle.SHORT, trLocale)
                .replaceFirstChar { it.uppercase() }
            "$dow, ${d.dayOfMonth} $month"
        } else "Gün ${index + 1}"
        TripDay(
            index = index + 1,
            label = dateLabel,
            activityCount = index + 3,
            accentColor = P.dayColors[index % P.dayColors.size],
            cityLabel = "Gün ${index + 1} — $city",
        )
    }
}

private fun formatDateRange(start: String?, end: String?): String {
    if (start == null) return "Tarih belirlenmedi"
    val s = LocalDate.parse(start)
    if (end == null) return "${s.dayOfMonth} ${s.month.getDisplayName(TextStyle.SHORT, trLocale).replaceFirstChar { it.uppercase() }}"
    val e = LocalDate.parse(end)
    val sm = s.month.getDisplayName(TextStyle.SHORT, trLocale).replaceFirstChar { it.uppercase() }
    val em = e.month.getDisplayName(TextStyle.SHORT, trLocale).replaceFirstChar { it.uppercase() }
    return if (s.month == e.month) "${s.dayOfMonth} – ${e.dayOfMonth} $sm"
    else "${s.dayOfMonth} $sm – ${e.dayOfMonth} $em"
}

private fun computeDayCount(startDate: String?, endDate: String?): Int {
    if (startDate == null || endDate == null) return 0
    return maxOf(1, ChronoUnit.DAYS.between(LocalDate.parse(startDate), LocalDate.parse(endDate)).toInt() + 1)
}
