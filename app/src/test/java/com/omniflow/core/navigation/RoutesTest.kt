package com.omniflow.core.navigation

import com.omniflow.ui.auth.login.LOGIN_EMAIL_KEY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RoutesTest {

    @Test
    fun emptyLoginRouteDoesNotLeakNavigationPlaceholder() {
        val route = Routes.Login.createRoute()

        assertEquals("login?$LOGIN_EMAIL_KEY=", route)
        assertFalse(route.contains("{"))
        assertFalse(route.contains("}"))
    }
}
