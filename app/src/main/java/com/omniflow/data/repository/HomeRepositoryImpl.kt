package com.omniflow.data.repository

import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.home.HomeCommunityPreviewModel
import com.omniflow.data.models.home.HomeDataModel
import com.omniflow.data.models.home.HomeFeaturedTripModel
import com.omniflow.data.models.home.HomeProfileModel
import com.omniflow.data.models.home.HomeSectionModel
import com.omniflow.data.models.home.HomeTripModel
import com.omniflow.data.remote.HomeService
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class HomeRepositoryImpl @Inject constructor(
    private val homeService: HomeService,
    private val apiCallExecutor: ApiCallExecutor,
) : HomeRepository {
    override suspend fun getHomeData(): ApiResult<HomeDataModel> = coroutineScope {
        val profileDeferred = async { apiCallExecutor.execute { homeService.getMyProfile() } }
        val tripsDeferred = async { apiCallExecutor.execute { homeService.getMyTrips() } }
        val featuredDeferred = async { apiCallExecutor.execute { homeService.getFeaturedTrips() } }
        val communityDeferred = async { apiCallExecutor.execute { homeService.getFeed() } }
        val unreadDeferred = async { apiCallExecutor.execute { homeService.getUnreadCount() } }

        val profileResult = profileDeferred.await()
        val tripsResult = tripsDeferred.await()
        val featuredResult = featuredDeferred.await()
        val communityResult = communityDeferred.await()
        val unreadResult = unreadDeferred.await()

        val profile = when (profileResult) {
            is ApiResult.Success -> profileResult.data
            is ApiResult.Error -> return@coroutineScope profileResult
            is ApiResult.Loading -> error("Loading is not expected from ApiCallExecutor")
        }

        val trips = when (tripsResult) {
            is ApiResult.Success -> tripsResult.data
            is ApiResult.Error -> return@coroutineScope tripsResult
            is ApiResult.Loading -> error("Loading is not expected from ApiCallExecutor")
        }

        ApiResult.Success(
            HomeDataModel(
                profile = HomeProfileModel(
                    id = profile.id,
                    username = profile.username,
                    profilePhotoUrl = profile.profilePhotoUrl,
                ),
                trips = trips.data.map { trip ->
                    HomeTripModel(
                        id = trip.id,
                        title = trip.title,
                        coverPhotoUrl = trip.coverPhotoUrl,
                        status = trip.status,
                        startDate = trip.startDate,
                        endDate = trip.endDate,
                        primaryCity = trip.destinations.minByOrNull { it.orderIndex }?.city,
                        primaryCountry = trip.destinations.minByOrNull { it.orderIndex }?.country,
                        isSaved = trip.isSaved == true,
                    )
                },
                featuredSection = featuredResult.toFeaturedSection(),
                communitySection = communityResult.toCommunitySection(),
                hasUnreadNotifications = (unreadResult as? ApiResult.Success)?.data?.let { it > 0 } == true,
            ),
        )
    }

    private fun ApiResult<List<com.omniflow.data.models.home.HomeFeaturedTripDto>>.toFeaturedSection():
        HomeSectionModel<HomeFeaturedTripModel> {
        return when (this) {
            is ApiResult.Success -> {
                if (data.isEmpty()) {
                    HomeSectionModel.Content(
                        listOf(
                            HomeFeaturedTripModel(
                                id = "mock-trip-1",
                                title = "Roma & Floransa Turu (Mock)",
                                ownerUsername = "alptravel (Mock)",
                                coverPhotoUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=320&h=240&fit=crop",
                                upvoteCount = 42,
                                isSaved = false,
                            ),
                            HomeFeaturedTripModel(
                                id = "mock-trip-2",
                                title = "Barselona'da 5 Gün (Mock)",
                                ownerUsername = "selintravel (Mock)",
                                coverPhotoUrl = "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?w=320&h=240&fit=crop",
                                upvoteCount = 28,
                                isSaved = false,
                            ),
                        ),
                    )
                } else {
                    HomeSectionModel.Content(
                        data.map { trip ->
                            HomeFeaturedTripModel(
                                id = trip.id,
                                title = trip.title,
                                ownerUsername = trip.ownerUsername,
                                coverPhotoUrl = trip.coverPhotoUrl,
                                upvoteCount = trip.upvoteCount,
                                isSaved = false,
                            )
                        },
                    )
                }
            }
            is ApiResult.Error -> {
                HomeSectionModel.Content(
                    listOf(
                        HomeFeaturedTripModel(
                            id = "mock-trip-err",
                            title = "Roma & Floransa (Mock - Hata Durumu)",
                            ownerUsername = "system (Mock)",
                            coverPhotoUrl = null,
                            upvoteCount = 0,
                            isSaved = false,
                        ),
                    ),
                )
            }
            is ApiResult.Loading -> error("Loading is not expected from ApiCallExecutor")
        }
    }

    private fun ApiResult<com.omniflow.data.models.home.HomeFeedResponseDto>.toCommunitySection():
        HomeSectionModel<HomeCommunityPreviewModel> {
        return when (this) {
            is ApiResult.Success -> {
                if (data.data.isEmpty()) {
                    HomeSectionModel.Content(
                        listOf(
                            HomeCommunityPreviewModel(
                                id = "mock-comm-1",
                                username = "selin.k (Mock)",
                                profilePhotoUrl = null,
                                content = "yeni bir gezi paylaştı (Mock)",
                                photoUrl = null,
                                createdAt = java.time.OffsetDateTime.now().minusHours(2).toString(),
                            ),
                            HomeCommunityPreviewModel(
                                id = "mock-comm-2",
                                username = "arda.t (Mock)",
                                profilePhotoUrl = null,
                                content = "bir ipucu paylaştı (Mock)",
                                photoUrl = null,
                                createdAt = java.time.OffsetDateTime.now().minusHours(1).toString(),
                            ),
                        ),
                    )
                } else {
                    HomeSectionModel.Content(
                        data.data.take(2).map { post ->
                            HomeCommunityPreviewModel(
                                id = post.id,
                                username = post.username,
                                profilePhotoUrl = post.profilePhotoUrl,
                                content = post.content,
                                photoUrl = post.photos.firstOrNull(),
                                createdAt = post.createdAt,
                            )
                        },
                    )
                }
            }
            is ApiResult.Error -> {
                HomeSectionModel.Content(
                    listOf(
                        HomeCommunityPreviewModel(
                            id = "mock-comm-err",
                            username = "system (Mock)",
                            profilePhotoUrl = null,
                            content = "Topluluk akışı yüklenemedi. (Mock - Hata Durumu)",
                            photoUrl = null,
                            createdAt = java.time.OffsetDateTime.now().toString(),
                        ),
                    ),
                )
            }
            is ApiResult.Loading -> error("Loading is not expected from ApiCallExecutor")
        }
    }
}
