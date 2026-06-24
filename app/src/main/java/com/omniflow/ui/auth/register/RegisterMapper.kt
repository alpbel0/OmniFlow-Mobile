package com.omniflow.ui.auth.register

import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult

data class RegisterFieldErrors(
    val username: UiText? = null,
    val email: UiText? = null,
    val password: UiText? = null,
    val confirmPassword: UiText? = null,
) {
    val hasFieldError: Boolean
        get() = username != null || email != null || password != null || confirmPassword != null
}

fun ApiResult.Error.toRegisterFieldErrors(): RegisterFieldErrors {
    var errors = RegisterFieldErrors()
    validationErrors.forEach { detail ->
        val fieldMessage = UiText.DynamicString(detail.message)
        errors = when (detail.field.lowercase()) {
            "username" -> errors.copy(username = fieldMessage)
            "email" -> errors.copy(email = fieldMessage)
            "password" -> errors.copy(password = fieldMessage)
            "confirmpassword", "confirm_password" -> errors.copy(confirmPassword = fieldMessage)
            else -> errors
        }
    }

    if (code == 400 && message is UiText.DynamicString) {
        errors = when (message.value) {
            DUPLICATE_EMAIL_MESSAGE -> errors.copy(email = message)
            DUPLICATE_USERNAME_MESSAGE -> errors.copy(username = message)
            else -> errors
        }
    }
    return errors
}

private const val DUPLICATE_EMAIL_MESSAGE = "Email address is already registered."
private const val DUPLICATE_USERNAME_MESSAGE = "Username is already taken."
