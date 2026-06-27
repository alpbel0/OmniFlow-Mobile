package com.omniflow.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
                    onTripClick = { /* TODO M3: navigate to TripDetail */ },
                    onInspirationClick = { /* TODO M4: navigate to DestinationDetail */ },
                    onCreateTrip = { /* TODO M3: navigate to trip wizard */ },
                    onRetry = { viewModel.retry() },
                    onRefresh = { viewModel.onRefresh() },
                )
            }
            composable(Routes.Trips.route) {
                FeaturePlaceholderScreen(name = "Trips", paddingValues = innerPadding)
            }
            composable(Routes.Explore.route) {
                FeaturePlaceholderScreen(name = "Explore", paddingValues = innerPadding)
            }
            composable(Routes.Community.route) {
                FeaturePlaceholderScreen(name = "Community", paddingValues = innerPadding)
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
                ProfileScreen(paddingValues = innerPadding)
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
