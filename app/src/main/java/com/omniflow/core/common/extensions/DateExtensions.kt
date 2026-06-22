package com.omniflow.core.common.extensions

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val displayDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr", "TR"))

fun LocalDate.toDisplayDate(): String = format(displayDateFormatter)

fun Instant.toDisplayDate(zoneId: ZoneId = ZoneId.systemDefault()): String =
    atZone(zoneId).toLocalDate().toDisplayDate()
