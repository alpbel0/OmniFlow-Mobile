package com.omniflow.core.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PendingAuthCredentialsStoreTest {

    @Test
    fun `credentials remain in memory until cleared`() {
        val store = PendingAuthCredentialsStore()

        store.save("user@example.com", "Secret123!")

        assertEquals(
            PendingAuthCredentials("user@example.com", "Secret123!"),
            store.get(),
        )

        store.clear()

        assertNull(store.get())
    }
}
