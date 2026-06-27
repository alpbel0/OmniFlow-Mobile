package com.omniflow.ui.home

import com.omniflow.core.common.UiState
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.home.HomeCommunityPreviewModel
import com.omniflow.data.models.home.HomeDataModel
import com.omniflow.data.models.home.HomeFeaturedTripModel
import com.omniflow.data.models.home.HomeProfileModel
import com.omniflow.data.models.home.HomeSectionModel
import com.omniflow.data.models.home.HomeTripModel
import com.omniflow.data.repository.HomeRepository
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: HomeRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful load prioritizes active trip over upcoming and draft`() = runTest {
        coEvery { repository.getHomeData() } returns ApiResult.Success(
            createHomeData(
                trips = listOf(
                    trip(title = "Taslak Paris", status = "Draft", startOffsetDays = 12, endOffsetDays = 16),
                    trip(title = "Roma", status = "Published", startOffsetDays = 3, endOffsetDays = 7),
                    trip(title = "Bali", status = "Published", startOffsetDays = -1, endOffsetDays = 4),
                ),
            ),
        )

        val viewModel = HomeViewModel(repository)
        val state = viewModel.uiState.value.contentState as UiState.Success

        assertEquals("Bali", state.data.heroTrips.first().title)
    }

    @Test
    fun `successful load with no trips shows empty hero state`() = runTest {
        coEvery { repository.getHomeData() } returns ApiResult.Success(createHomeData(trips = emptyList()))

        val viewModel = HomeViewModel(repository)
        val state = viewModel.uiState.value.contentState as UiState.Success

        assertTrue(state.data.heroTrips.isEmpty())
    }

    @Test
    fun `repository error exposes full screen error state`() = runTest {
        coEvery { repository.getHomeData() } returns ApiResult.Error(
            message = UiText.DynamicString("server error"),
        )

        val viewModel = HomeViewModel(repository)

        assertTrue(viewModel.uiState.value.contentState is UiState.Error)
    }

    private fun createHomeData(trips: List<HomeTripModel>): HomeDataModel {
        return HomeDataModel(
            profile = HomeProfileModel(
                id = "user-id",
                username = "Yigit",
                profilePhotoUrl = null,
            ),
            trips = trips,
            featuredSection = HomeSectionModel.Content(
                listOf(
                    HomeFeaturedTripModel(
                        id = "featured-id",
                        title = "Roma & Floransa",
                        ownerUsername = "alptravel",
                        coverPhotoUrl = null,
                        upvoteCount = 48,
                        isSaved = false,
                    ),
                ),
            ),
            communitySection = HomeSectionModel.Content(
                listOf(
                    HomeCommunityPreviewModel(
                        id = "post-id",
                        username = "selin",
                        profilePhotoUrl = null,
                        content = "Yeni gezi paylasti",
                        photoUrl = null,
                        createdAt = "2026-06-26T10:00:00+03:00",
                    ),
                ),
            ),
            hasUnreadNotifications = true,
        )
    }

    private fun trip(
        title: String,
        status: String,
        startOffsetDays: Long,
        endOffsetDays: Long,
    ): HomeTripModel {
        val today = LocalDate.now()
        return HomeTripModel(
            id = title,
            title = title,
            coverPhotoUrl = null,
            status = status,
            startDate = today.plusDays(startOffsetDays).toString(),
            endDate = today.plusDays(endOffsetDays).toString(),
            primaryCity = title,
            primaryCountry = "Italy",
            isSaved = false,
        )
    }
}
