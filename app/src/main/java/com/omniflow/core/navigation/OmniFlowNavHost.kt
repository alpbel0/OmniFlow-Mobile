package com.omniflow.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.omniflow.uicomponents.EmptyState
import com.omniflow.ui.auth.login.LoginScreen
import com.omniflow.ui.auth.onboarding.OnboardingScreen
import com.omniflow.ui.auth.register.RegisterScreen
import com.omniflow.ui.auth.resetpassword.ResetPasswordScreen
import com.omniflow.ui.auth.splash.SplashScreen
import com.omniflow.ui.auth.splash.SplashDestination
import com.omniflow.ui.auth.verifyemail.VerifyEmailScreen
import com.omniflow.ui.home.HomeScreen
import com.omniflow.ui.notifications.NotificationsScreen
import com.omniflow.ui.profile.ProfileScreen

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
                BottomNavBar(navController = navController)
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
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Onboarding.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(Routes.Login.route) {
                LoginScreen(
                    paddingValues = innerPadding,
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Splash.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.Register.route) },
                    onForgotPasswordClick = { navController.navigate(Routes.ResetPassword.route) },
                )
            }
            composable(Routes.Register.route) {
                RegisterScreen(
                    paddingValues = innerPadding,
                    onRegisterSuccess = { navController.navigate(Routes.VerifyEmail.route) },
                    onLoginClick = { navController.navigate(Routes.Login.route) },
                )
            }
            composable(Routes.VerifyEmail.route) {
                VerifyEmailScreen(
                    paddingValues = innerPadding,
                    onContinue = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.ResetPassword.route) {
                ResetPasswordScreen(
                    paddingValues = innerPadding,
                    onContinue = { navController.popBackStack() },
                )
            }
            composable(Routes.Home.route) {
                HomeScreen(paddingValues = innerPadding)
            }
            composable(Routes.Trips.route) {
                FeaturePlaceholderScreen(name = "Trips", paddingValues = innerPadding)
            }
            composable(Routes.Explore.route) {
                FeaturePlaceholderScreen(name = "Explore", paddingValues = innerPadding)
            }
            composable(Routes.Social.route) {
                FeaturePlaceholderScreen(name = "Social", paddingValues = innerPadding)
            }
            composable(Routes.Notifications.route) {
                NotificationsScreen(paddingValues = innerPadding)
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
    Routes.Social.route,
    Routes.Notifications.route,
    Routes.Profile.route,
)

private val SplashDestination.route: String
    get() = when (this) {
        SplashDestination.Home -> Routes.Home.route
        SplashDestination.Onboarding -> Routes.Onboarding.route
        SplashDestination.Login -> Routes.Login.route
    }
