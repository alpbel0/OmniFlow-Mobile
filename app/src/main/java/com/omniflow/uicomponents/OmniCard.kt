package com.omniflow.uicomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omniflow.core.designsystem.theme.OmniTokens

@Composable
fun OmniCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(OmniTokens.dimens.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(OmniTokens.spacing.base)) {
            content()
        }
    }
}
