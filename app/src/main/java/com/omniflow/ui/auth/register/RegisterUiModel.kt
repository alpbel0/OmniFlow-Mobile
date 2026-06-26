package com.omniflow.ui.auth.register

import androidx.annotation.StringRes
import com.omniflow.R

data class PasswordRequirementUiModel(
    @StringRes val labelRes: Int,
    val isMet: Boolean,
)

fun PasswordRequirements.toUiModels(): List<PasswordRequirementUiModel> = listOf(
    PasswordRequirementUiModel(R.string.register_password_minimum, hasMinimumLength),
    PasswordRequirementUiModel(R.string.register_password_uppercase, hasUppercase),
    PasswordRequirementUiModel(R.string.register_password_lowercase, hasLowercase),
    PasswordRequirementUiModel(R.string.register_password_digit, hasDigit),
    PasswordRequirementUiModel(R.string.register_password_special, hasSpecialCharacter),
)
