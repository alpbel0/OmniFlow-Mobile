package com.omniflow.ui.auth.verifyemail

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Test

class EmailAppIntentTest {

    @Test
    fun emailAppIntentOpensExternalMailTask() {
        val intent = createEmailAppIntent(ApplicationProvider.getApplicationContext())

        assertEquals(Intent.ACTION_MAIN, intent.action)
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK)
        assertEquals(
            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
            intent.flags and Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
        )
        assertTrue(
            intent.`package` == "com.google.android.gm" ||
                intent.component?.packageName == "com.google.android.gm" ||
                intent.categories.orEmpty().contains(Intent.CATEGORY_APP_EMAIL),
        )
        assertNotNull(intent)
    }
}
