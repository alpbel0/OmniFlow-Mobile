package com.omniflow.ui.trips

import com.omniflow.data.models.trips.SavedTripModel
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripStatusDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

private val trLocale = Locale("tr")

fun TripModel.toDraftTrip(): DraftTrip = DraftTrip(
    id = id,
    title = title,
    dateLabel = formatDateRange(startDate, endDate),
    peopleCount = personCount,
    destinationCount = destinationCount,
    progressPercent = mockCompletionPercent(id),
    gradient = gradientForId(id),
)

fun TripModel.toPublishedTrip(): PublishedTrip {
    val now = LocalDate.now()
    val start = startDate?.let { LocalDate.parse(it) }
    val badge = when {
        status == TripStatusDto.Archived -> PublishedBadge.ARCHIVED
        start != null && now.isAfter(start) -> PublishedBadge.COMPLETED
        else -> PublishedBadge.UPCOMING
    }
    val daysLeft = if (badge == PublishedBadge.UPCOMING && start != null) {
        val d = ChronoUnit.DAYS.between(now, start).toInt()
        if (d > 0) "$d gün kaldı" else null
    } else null
    return PublishedTrip(
        id = id,
        title = title,
        dateLabel = formatDateRange(startDate, endDate),
        peopleCount = personCount,
        destinationCount = destinationCount,
        dayCount = computeDayCount(startDate, endDate),
        rating = roundToOneDecimal(popularityScore),
        forkCount = forkCount,
        badge = badge,
        daysLeftLabel = daysLeft,
        gradient = gradientForId(id),
    )
}

fun computePublishSummary(published: List<PublishedTrip>): PublishSummary {
    val nonArchived = published.filter { it.badge != PublishedBadge.ARCHIVED }
    if (nonArchived.isEmpty()) return PublishSummary(0, 0, 0, 0.0)
    return PublishSummary(
        routeCount = nonArchived.size,
        viewCount = 0,
        forkCount = nonArchived.sumOf { it.forkCount },
        avgRating = roundToOneDecimal(nonArchived.map { it.rating }.average()),
    )
}

fun SavedTripModel.toSavedTrip(): SavedTrip = SavedTrip(
    id = tripId,
    title = title,
    username = username,
    destinationCount = destinationCount,
    dayCount = dayCount,
    likeCount = if (upvoteCount >= 1000) "${upvoteCount / 1000}K" else upvoteCount.toString(),
    gradient = gradientForId(tripId),
)

private fun formatDateRange(startDate: String?, endDate: String?): String {
    if (startDate == null) return "Tarih belirlenmedi"
    val start = LocalDate.parse(startDate)
    if (endDate == null) return formatDate(start)
    val end = LocalDate.parse(endDate)
    return if (start.month == end.month && start.year == end.year) {
        "${start.dayOfMonth}-${end.dayOfMonth} ${start.month.getDisplayName(TextStyle.FULL, trLocale)} ${start.year}"
    } else {
        "${formatDate(start)} - ${formatDate(end)}"
    }
}

private fun formatDate(date: LocalDate): String =
    "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.FULL, trLocale)} ${date.year}"

private fun computeDayCount(startDate: String?, endDate: String?): Int {
    if (startDate == null || endDate == null) return 0
    val start = LocalDate.parse(startDate)
    val end = LocalDate.parse(endDate)
    return maxOf(1, ChronoUnit.DAYS.between(start, end).toInt() + 1)
}

fun mockCompletionPercent(id: String): Int = (abs(id.hashCode()) % 41) + 20

private fun roundToOneDecimal(value: Double): Double =
    (value * 10).roundToInt() / 10.0
