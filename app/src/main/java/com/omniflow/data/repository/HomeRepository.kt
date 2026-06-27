package com.omniflow.data.repository

import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.home.HomeDataModel

interface HomeRepository {
    suspend fun getHomeData(): ApiResult<HomeDataModel>
}
