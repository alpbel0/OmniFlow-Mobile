package com.omniflow.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.omniflow.core.designsystem.theme.OmniTokens

@Composable
fun OmniButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    fontWeight: FontWeight = FontWeight.Bold,
    shadowElevation: Dp? = null,
) {
    val tokens = OmniTokens
    val resolvedShadowElevation = shadowElevation ?: tokens.dimens.buttonShadowElevation

    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = OmniTokens.dimens.authControlHeight)
            .shadow(
                elevation = if (enabled && !loading) resolvedShadowElevation else tokens.spacing.none,
                shape = shape,
                ambientColor = tokens.colors.buttonShadow,
                spotColor = tokens.colors.buttonShadow,
            ),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(tokens.spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(OmniTokens.dimens.googleIconSize),
                    strokeWidth = OmniTokens.dimens.progressStrokeWidth,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = fontWeight,
                )
            )
        }
    }
}
