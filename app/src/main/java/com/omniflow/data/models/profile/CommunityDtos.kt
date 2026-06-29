package com.omniflow.data.models.profile

import kotlinx.serialization.Serializable

@Serializable
data class SuggestedFollowDto(
    val id: String,
    val username: String,
    val profilePhotoUrl: String? = null,
    val tripCount: Int = 0,
    val karmaScore: Int = 0,
    val isFollowing: Boolean = false,
    val suggestionReason: String = "",
)

@Serializable
data class TopContributorDto(
    val id: String,
    val username: String,
    val profilePhotoUrl: String? = null,
    val karmaScore: Int = 0,
    val tripCount: Int = 0,
)
