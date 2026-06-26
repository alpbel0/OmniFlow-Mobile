package com.omniflow.core.network

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class ApiCallExecutor @Inject constructor(
    private val errorParser: ErrorParser,
) {
    suspend fun <T> execute(block: suspend () -> T): ApiResult<T> {
        return try {
            ApiResult.Success(block())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: HttpException) {
            val parsedError = errorParser.parse(exception.response()?.errorBody()?.string())
            ApiResult.Error(
                code = exception.code(),
                message = parsedError.message,
                validationErrors = parsedError.validationErrors,
            )
        } catch (_: IOException) {
            fallbackError()
        } catch (_: Exception) {
            fallbackError()
        }
    }

    private fun fallbackError(): ApiResult.Error {
        val parsedError = errorParser.parse(null)
        return ApiResult.Error(message = parsedError.message)
    }
}
