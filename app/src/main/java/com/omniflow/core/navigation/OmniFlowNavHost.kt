package com.omniflow.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.omniflow.ui.auth.forgotpassword.ForgotPasswordScreen
import com.omniflow.ui.auth.login.LoginScreen
import com.omniflow.ui.auth.login.LOGIN_EMAIL_KEY
import com.omniflow.ui.auth.onboarding.OnboardingScreen
import com.omniflow.ui.auth.register.RegisterScreen
import com.omniflow.ui.auth.splash.SplashScreen
import com.omniflow.ui.auth.splash.SplashDestination
import com.omniflow.ui.auth.verifyemail.VerifyEmailScreen
import com.omniflow.ui.auth.verifyemail.VERIFY_EMAIL_KEY
import com.omniflow.ui.auth.verifyemail.VERIFY_EMAIL_SOURCE_KEY
import com.omniflow.ui.auth.verifyemail.VerifyEmailSource
import com.omniflow.ui.home.HomeScreen
import com.omniflow.ui.home.HomeViewModel
import com.omniflow.ui.notifications.NotificationsScreen
import com.omniflow.ui.notifications.NotificationsUiState
import com.omniflow.ui.notifications.NotificationsViewModel
import com.omniflow.ui.profile.ProfileScreen
import com.omniflow.ui.profile.ProfileUiState
import com.omniflow.ui.profile.ProfileViewModel
import com.omniflow.ui.profile.FollowListScreen
import com.omniflow.ui.profile.FollowListViewModel
import com.omniflow.ui.profile.PublicProfileScreen
import com.omniflow.ui.profile.PublicProfileViewModel
import com.omniflow.ui.trips.MyTripsScreen
import com.omniflow.ui.trips.MyTripsViewModel
import com.omniflow.ui.trips.TripDetailScreen
import com.omniflow.ui.trips.TripDetailViewModel
import com.omniflow.ui.social.CommunityDiscoveryScreen
import com.omniflow.ui.social.CommunityViewModel
import com.omniflow.ui.settings.SettingsScreen
import com.omniflow.ui.settings.SettingsViewModel
import com.omniflow.uicomponents.EmptyState

@Composable
fun OmniFlowNavHost() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavPill(
                    navController = navController,
                    onCreateTrip = {
                        // TODO: navigate to create trip wizard
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Splash.route,
        ) {
            composable(Routes.Splash.route) {
                SplashScreen(
                    paddingValues = innerPadding,
                    onDestination = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(Routes.Splash.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(Routes.Onboarding.route) {
                OnboardingScreen(
                    paddingValues = innerPadding,
                    onLoginClick = {
                        navController.navigate(Routes.Login.createRoute()) {
                            popUpTo(Routes.Onboarding.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(
                route = Routes.Login.route,
                arguments = listOf(
                    navArgument(LOGIN_EMAIL_KEY) {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                ),
            ) {
                LoginScreen(
                    paddingValues = innerPadding,
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.Register.route) },
                    onForgotPasswordClick = { navController.navigate(Routes.ForgotPassword.route) },
                    onVerifyEmailClick = { email ->
                        navController.navigateToVerifyEmail(email, VerifyEmailSource.LOGIN)
                    },
                )
            }
            composable(Routes.Register.route) {
                RegisterScreen(
                    paddingValues = innerPadding,
                    onRegisterSuccess = { email ->
                        navController.navigateToVerifyEmail(email, VerifyEmailSource.REGISTER)
                    },
                    onLoginClick = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.VerifyEmail.route,
                arguments = listOf(
                    navArgument(VERIFY_EMAIL_KEY) { type = NavType.StringType },
                    navArgument(VERIFY_EMAIL_SOURCE_KEY) { type = NavType.StringType },
                ),
            ) {
                VerifyEmailScreen(
                    paddingValues = innerPadding,
                    onNavigateHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateLogin = { email -> navController.navigateToPrefilledLogin(email) },
                )
            }
            composable(Routes.ForgotPassword.route) {
                ForgotPasswordScreen(
                    paddingValues = innerPadding,
                    onLoginClick = { navController.popBackStack() },
                )
            }
            composable(Routes.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onSearchClick = { navController.navigate(Routes.Explore.route) },
                    onNotifClick = { navController.navigate(Routes.Notifications.route) },
                    onProfileClick = { navController.navigate(Routes.Profile.route) },
                    onCommunityUserClick = { username ->
                        navController.navigate(Routes.PublicProfile.createRoute(username))
                    },
                    onTripClick = { /* TODO M3: navigate to TripDetail */ },
                    onInspirationClick = { /* TODO M4: navigate to DestinationDetail */ },
                    onCreateTrip = { /* TODO M3: navigate to trip wizard */ },
                    onRetry = { viewModel.retry() },
                    onRefresh = { viewModel.onRefresh() },
                )
            }
            composable(Routes.Trips.route) {
                val viewModel: MyTripsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                MyTripsScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onTabSelected = { viewModel.onTabSelected(it) },
                    onTripClick = { tripId ->
                        navController.navigate(Routes.TripDetail.createRoute(tripId))
                    },
                    onCreateTrip = { /* TODO M3: trip wizard */ },
                    onExplore = { navController.navigate(Routes.Explore.route) },
                    onFilterSelected = { viewModel.onFilterSelected(it) },
                    onAddCollection = { viewModel.onAddCollection() },
                )
            }
            composable(
                route = Routes.TripDetail.route,
                arguments = listOf(navArgument("tripId") { type = NavType.StringType }),
            ) {
                val viewModel: TripDetailViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                TripDetailScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onBack = { navController.popBackStack() },
                    onMapModeChange = { viewModel.onMapModeChange(it) },
                    onAction = { viewModel.onAction(it) },
                    onDayClick = { /* TODO: expanded day view */ },
                    onDaySelected = { viewModel.onDaySelected(it) },
                    onDisplayModeChange = { viewModel.onDisplayModeChanged(it) },
                    onEntryDetailClick = { viewModel.onViewEntryDetail(it) },
                    onDismissEntryDetail = { viewModel.onDismissEntryDetail() },
                    onEditClick = { /* TODO M3: trip edit screen */ },
                    onRequestMoveToDraft = { viewModel.onRequestMoveToDraft() },
                    onDismissMoveToDraftDialog = { viewModel.onDismissMoveToDraftDialog() },
                    onConfirmMoveToDraft = { viewModel.onConfirmMoveToDraft() },
                    onRequestDelete = { viewModel.onRequestDelete() },
                    onDismissDeleteDialog = { viewModel.onDismissDeleteDialog() },
                    onConfirmDelete = { viewModel.onConfirmDelete() },
                    onPaneResize = { d, m -> viewModel.onPaneResize(d, m) },
                    onLandscapePaneResize = { t, d -> viewModel.onLandscapePaneResize(t, d) },
                    onToggleChecklistItem = { viewModel.onToggleChecklistItem(it) },
                    onUnlockEntry = { viewModel.onUnlockEntry(it) },
                    onDeleteEntry = { viewModel.onDeleteEntry(it) },
                    onEditEntryClick = { /* TODO M3: entry edit screen */ },
                    onAddDetailClick = { /* TODO M3: add detail wizard */ },
                    onDismissLoginRequiredDialog = { viewModel.onDismissLoginRequiredDialog() },
                    onNavigateToLogin = { navController.navigate(Routes.Login.createRoute()) },
                    onDismissCollectionPicker = { viewModel.onDismissCollectionPicker() },
                    onCollectionSelected = { viewModel.onCollectionSelected(it) },
                )
            }
            composable(Routes.Explore.route) {
                FeaturePlaceholderScreen(name = "Explore", paddingValues = innerPadding)
            }
            composable(Routes.Community.route) {
                val viewModel: CommunityViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                CommunityDiscoveryScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onBack = { navController.popBackStack() },
                    onFollowSuggested = { viewModel.onFollowSuggested(it) },
                    onFollowContrib = { viewModel.onFollowContrib(it) },
                    onUserTap = { username ->
                        navController.navigate(Routes.PublicProfile.createRoute(username))
                    },
                    onRetry = { viewModel.retry() },
                )
            }
            composable(Routes.Social.route) {
                FeaturePlaceholderScreen(name = "Social", paddingValues = innerPadding)
            }
            composable(Routes.Notifications.route) {
                val viewModel: NotificationsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                NotificationsScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onBack = { navController.popBackStack() },
                    onFilterChange = { viewModel.onFilterChange(it) },
                    onNotifClick = { viewModel.onNotifClick(it) },
                    onNotifLongPress = { viewModel.onNotifLongPress() },
                    onToggleSelect = { viewModel.onToggleSelect(it) },
                    onMarkReadSelected = { viewModel.onMarkReadSelected() },
                    onSelectAll = { viewModel.onSelectAll() },
                    onExitSelectMode = { viewModel.onExitSelectMode() },
                    onRetry = { viewModel.retry() },
                )
            }
            composable(Routes.Profile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                ProfileScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onBack = { navController.popBackStack() },
                    onSettingsTap = { navController.navigate(Routes.Settings.route) },
                    onFollowersTap = {
                        val userId = (uiState.contentState as? com.omniflow.core.common.UiState.Success)?.data?.id
                        if (!userId.isNullOrEmpty()) {
                            navController.navigate(Routes.FollowList.createRoute(userId, "followers"))
                        }
                    },
                    onFollowingTap = {
                        val userId = (uiState.contentState as? com.omniflow.core.common.UiState.Success)?.data?.id
                        if (!userId.isNullOrEmpty()) {
                            navController.navigate(Routes.FollowList.createRoute(userId, "following"))
                        }
                    },
                    onEditProfile = { viewModel.onEditProfile() },
                    onTabChange = { viewModel.onTabChange(it) },
                    onTripTap = { /* TODO M3: TripDetail */ },
                    onSaveEdit = { viewModel.onSaveEdit() },
                    onBioChange = { viewModel.onBioChange(it) },
                    onLocationChange = { viewModel.onLocationChange(it) },
                    onStyleToggle = { viewModel.onStyleToggle(it) },
                    onChangePhoto = { viewModel.onChangePhoto() },
                    onRetry = { viewModel.retry() },
                )
            }
            composable(
                route = Routes.PublicProfile.route,
                arguments = listOf(
                    navArgument("username") { type = NavType.StringType },
                ),
            ) {
                val viewModel: PublicProfileViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                PublicProfileScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onBack = { navController.popBackStack() },
                    onMoreMenu = { viewModel.onMoreMenu() },
                    onFollowersTap = {
                        val userId = (uiState.contentState as? com.omniflow.core.common.UiState.Success)?.data?.userId
                        if (!userId.isNullOrEmpty()) {
                            navController.navigate(Routes.FollowList.createRoute(userId, "followers"))
                        }
                    },
                    onFollowingTap = {
                        val userId = (uiState.contentState as? com.omniflow.core.common.UiState.Success)?.data?.userId
                        if (!userId.isNullOrEmpty()) {
                            navController.navigate(Routes.FollowList.createRoute(userId, "following"))
                        }
                    },
                    onFollowTap = { viewModel.onFollow() },
                    onUnfollowTap = { viewModel.onUnfollow() },
                    onMessageTap = { /* TODO: Messaging module */ },
                    onUnblockTap = { viewModel.onUnblock() },
                    onTabChange = { tab -> viewModel.onTabChange(tab) },
                    onTripTap = { /* TODO M3: TripDetail */ },
                    onRetry = { viewModel.retry() },
                )
            }
            composable(
                route = Routes.FollowList.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("mode") { type = NavType.StringType },
                ),
            ) {
                val viewModel: FollowListViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                FollowListScreen(
                    uiState = uiState,
                    paddingValues = innerPadding,
                    onBack = { navController.popBackStack() },
                    onSearchChange = { viewModel.onSearchChange(it) },
                    onFollowTap = { viewModel.onFollow(it) },
                    onUnfollowTap = { viewModel.onUnfollow(it) },
                    onUserTap = { username ->
                        navController.navigate(Routes.PublicProfile.createRoute(username))
                    },
                    onRetry = { viewModel.retry() },
                )
            }
            composable(Routes.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(uiState.loggedOut) {
                    if (uiState.loggedOut) {
                        navController.navigate(Routes.Login.createRoute()) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                SettingsScreen(
                    groups = uiState.groups,
                    isLoggingOut = uiState.isLoggingOut,
                    paddingValues = innerPadding,
                    onBack = { navController.popBackStack() },
                    onRowClick = { id ->
                        // Sub-screen navigation — placeholder for now
                    },
                    onLogout = { viewModel.onLogout() },
                )
            }
        }
    }
}

@Composable
private fun FeaturePlaceholderScreen(
    name: String,
    paddingValues: PaddingValues,
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        EmptyState(
            title = "$name module",
            description = "This screen is ready for the next milestone.",
        )
    }
}

private val bottomBarRoutes = setOf(
    Routes.Home.route,
    Routes.Trips.route,
    Routes.Explore.route,
    Routes.Community.route,
    Routes.Profile.route,
)

private val SplashDestination.route: String
    get() = when (this) {
        SplashDestination.Home -> Routes.Home.route
        SplashDestination.Onboarding -> Routes.Onboarding.route
        SplashDestination.Login -> Routes.Login.createRoute()
    }

private fun NavHostController.navigateToVerifyEmail(
    email: String,
    source: VerifyEmailSource,
) {
    navigate(Routes.VerifyEmail.createRoute(email, source))
}

private fun NavHostController.navigateToPrefilledLogin(email: String) {
    navigate(Routes.Login.createRoute(email)) {
        popUpTo(Routes.Login.route) { inclusive = true }
        launchSingleTop = true
    }
}
