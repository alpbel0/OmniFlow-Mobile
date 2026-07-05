package com.omniflow.core.navigation

import com.omniflow.ui.auth.login.LOGIN_EMAIL_KEY
import com.omniflow.ui.auth.verifyemail.VERIFY_EMAIL_KEY
import com.omniflow.ui.auth.verifyemail.VERIFY_EMAIL_SOURCE_KEY
import com.omniflow.ui.auth.verifyemail.VerifyEmailSource
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Onboarding : Routes("onboarding")
    data object Login : Routes("login?$LOGIN_EMAIL_KEY={$LOGIN_EMAIL_KEY}") {
        fun createRoute(email: String = ""): String = "login?$LOGIN_EMAIL_KEY=${urlEncode(email)}"
    }
    data object Register : Routes("register")
    data object ForgotPassword : Routes("forgot_password")
    data object VerifyEmail : Routes(
        "verify_email/{$VERIFY_EMAIL_KEY}/{$VERIFY_EMAIL_SOURCE_KEY}",
    ) {
        fun createRoute(email: String, source: VerifyEmailSource): String =
            "verify_email/${urlEncode(email)}/${source.name}"
    }
    // Reserved for the future email-token flow where the user enters a new password.
    data object ResetPassword : Routes("reset_password")
    data object Home : Routes("home")
    data object Trips : Routes("trips")
    data object Explore : Routes("explore")
    data object Social : Routes("social")
    data object Community : Routes("community")
    data object Notifications : Routes("notifications")
    data object Profile : Routes("profile")
    data object PublicProfile : Routes("public_profile/{username}") {
        fun createRoute(username: String): String = "public_profile/${urlEncode(username)}"
    }
    data object FollowList : Routes("follow_list/{userId}/{mode}") {
        fun createRoute(userId: String, mode: String): String = "follow_list/$userId/$mode"
    }
    data object Settings : Routes("settings")
    data object TripDetail : Routes("trip_detail/{tripId}") {
        fun createRoute(tripId: String): String = "trip_detail/$tripId"
    }

    companion object {
        private fun urlEncode(value: String): String =
            URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20")
    }
}
