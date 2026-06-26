package com.omniflow.core.auth

import javax.inject.Inject
import javax.inject.Singleton

data class PendingAuthCredentials(
    val email: String,
    val password: String,
)

@Singleton
class PendingAuthCredentialsStore @Inject constructor() {
    private var credentials: PendingAuthCredentials? = null

    @Synchronized
    fun save(email: String, password: String) {
        credentials = PendingAuthCredentials(email = email, password = password)
    }

    @Synchronized
    fun get(): PendingAuthCredentials? = credentials

    @Synchronized
    fun clear() {
        credentials = null
    }
}
