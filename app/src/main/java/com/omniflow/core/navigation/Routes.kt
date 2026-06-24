package com.omniflow.core.navigation

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Onboarding : Routes("onboarding")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object VerifyEmail : Routes("verify_email")
    data object ResetPassword : Routes("reset_password")
    data object Home : Routes("home")
    data object Trips : Routes("trips")
    data object Explore : Routes("explore")
    data object Social : Routes("social")
    data object Notifications : Routes("notifications")
    data object Profile : Routes("profile")
}

const val VERIFY_EMAIL_ADDRESS_KEY = "verify_email_address"
