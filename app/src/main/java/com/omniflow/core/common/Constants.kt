package com.omniflow.core.common

import com.omniflow.BuildConfig

object Constants {
    const val AppName = "OmniFlow"
    const val PlatformHeader = "X-Platform"
    const val MobilePlatform = "mobile"
    const val RefreshTokenPath = "api/account/refresh-token"
    val BaseUrl: String = BuildConfig.API_BASE_URL
}
