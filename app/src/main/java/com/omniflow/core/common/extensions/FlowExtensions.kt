package com.omniflow.core.common.extensions

import com.omniflow.core.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException

fun <T> Flow<T>.asUiState(
    isEmpty: (T) -> Boolean = { false },
): Flow<UiState<T>> {
    return map<T, UiState<T>> { value ->
        if (isEmpty(value)) UiState.Empty else UiState.Success(value)
    }.catch { throwable ->
        val message = throwable.message ?: IOException("Unexpected error").message.orEmpty()
        emit(UiState.Error(com.omniflow.core.common.UiText.DynamicString(message)))
    }.onStart { emit(UiState.Loading) }
}
