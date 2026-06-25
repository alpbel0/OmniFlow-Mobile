package com.omniflow.core.common

object EmailValidator {
    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun isValid(email: String): Boolean = emailRegex.matches(email.trim())
}
