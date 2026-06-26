package com.omniflow.ui.auth.verifyemail

import android.content.Context
import android.content.Intent

private const val GMAIL_PACKAGE_NAME = "com.google.android.gm"

fun createEmailAppIntent(context: Context): Intent =
    context.packageManager.getLaunchIntentForPackage(GMAIL_PACKAGE_NAME)
        ?.asExternalAppTask()
        ?: Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
            asExternalAppTask()
        }

private fun Intent.asExternalAppTask(): Intent =
    apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
    }
