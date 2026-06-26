package com.omniflow.ui.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omniflow.uicomponents.OmniCard

@Composable
fun ProfileScreen(paddingValues: PaddingValues) {
    OmniCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        Text(text = "Profile module scaffolded.")
    }
}
