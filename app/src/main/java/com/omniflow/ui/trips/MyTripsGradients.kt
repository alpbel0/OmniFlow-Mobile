package com.omniflow.ui.trips

import com.omniflow.core.designsystem.theme.TripsPalette
import kotlin.math.abs

fun gradientForId(id: String) =
    TripsPalette.gradientSets[abs(id.hashCode()) % TripsPalette.gradientSets.size]
