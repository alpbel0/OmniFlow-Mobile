package com.omniflow.ui.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.omniflow.R
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.uicomponents.OmniTextField

@Composable
internal fun RegisterHeader() {
    val colors = OmniTokens.colors
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(OmniTokens.dimens.authLogoMedium)
            .shadow(spacing.m, MaterialTheme.shapes.medium)
            .background(
                Brush.linearGradient(
                    colors = listOf(colorScheme.primary, colors.logoGradientEnd),
                    start = Offset.Zero,
                    end = Offset(52f, 52f),
                ),
                MaterialTheme.shapes.medium,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(OmniTokens.dimens.authLogoInnerOuter).background(colorScheme.onPrimary, CircleShape))
        Box(Modifier.size(OmniTokens.dimens.authLogoInner).background(colorScheme.primary, CircleShape))
    }
    Spacer(Modifier.height(spacing.xxl - spacing.xs))
    Text(
        text = stringResource(R.string.register_title),
        color = colorScheme.onBackground,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(spacing.s))
    Text(
        text = stringResource(R.string.register_subtitle),
        color = colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyLarge,
    )
    Spacer(Modifier.height(spacing.m))
    Text(
        text = stringResource(R.string.register_verification_hint),
        color = colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
internal fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    enabled: Boolean,
) {
    val colors = OmniTokens.colors
    val colorScheme = MaterialTheme.colorScheme

    FieldLabel(label)
    OmniTextField(
        value = value,
        onValueChange = onValueChange,
        label = null,
        placeholder = label,
        placeholderColor = colorScheme.onSurfaceVariant,
        enabled = enabled,
        errorMessage = error,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = MaterialTheme.shapes.medium,
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colors.fieldBorder,
    )
}

@Composable
internal fun RegisterPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    error: String?,
    enabled: Boolean,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
) {
    val colors = OmniTokens.colors
    val colorScheme = MaterialTheme.colorScheme

    FieldLabel(label)
    OmniTextField(
        value = value,
        onValueChange = onValueChange,
        label = null,
        placeholder = label,
        placeholderColor = colorScheme.onSurfaceVariant,
        enabled = enabled,
        errorMessage = error,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle, enabled = enabled) {
                Icon(
                    imageVector = if (isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant,
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        keyboardActions = if (imeAction == ImeAction.Done) {
            KeyboardActions(onDone = { onImeAction() })
        } else {
            KeyboardActions(onNext = { onImeAction() })
        },
        shape = MaterialTheme.shapes.medium,
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colors.fieldBorder,
    )
}

@Composable
private fun FieldLabel(label: String) {
    val spacing = OmniTokens.spacing

    Text(
        text = label,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = spacing.l - spacing.tiny, bottom = spacing.s),
    )
}

@Composable
internal fun PasswordChecklist(requirements: PasswordRequirements) {
    val colors = OmniTokens.colors
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.s - spacing.tiny),
        modifier = Modifier.padding(top = spacing.base - spacing.tiny),
    ) {
        requirements.toUiModels().forEach { item ->
            val color = if (item.isMet) colors.success else colorScheme.onSurfaceVariant
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (item.isMet) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(OmniTokens.dimens.checklistIconSize),
                )
                Spacer(Modifier.width(spacing.s))
                Text(text = stringResource(item.labelRes), color = color, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
internal fun RegisterSocialLogin() {
    val colors = OmniTokens.colors
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = spacing.xxl - spacing.xs, bottom = spacing.l),
    ) {
        HorizontalDivider(Modifier.weight(1f), color = colors.divider)
        Text(
            text = stringResource(R.string.register_social_divider),
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = spacing.m),
        )
        HorizontalDivider(Modifier.weight(1f), color = colors.divider)
    }
    OutlinedButton(
        onClick = {},
        enabled = false,
        modifier = Modifier.fillMaxWidth().height(OmniTokens.dimens.authControlHeight),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(OmniTokens.dimens.hairline, colors.fieldBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            disabledContainerColor = colorScheme.surface,
            disabledContentColor = colorScheme.onSurface,
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google_g),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(OmniTokens.dimens.googleIconSize),
        )
        Spacer(Modifier.width(spacing.s + spacing.tiny))
        Text(
            text = stringResource(R.string.register_continue_google),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun RegisterFooter(enabled: Boolean, onLoginClick: () -> Unit) {
    val spacing = OmniTokens.spacing

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(top = spacing.xxl - spacing.xs),
    ) {
        Text(
            text = stringResource(R.string.register_have_account) + " ",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.register_login),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = enabled, onClick = onLoginClick),
        )
    }
}

@Composable
internal fun BoxScope.RegisterGlow() {
    val colors = OmniTokens.colors

    Box(
        Modifier
            .align(Alignment.TopEnd)
            .offset(x = OmniTokens.dimens.registerGlowOffsetX, y = OmniTokens.dimens.registerGlowOffsetY)
            .size(OmniTokens.dimens.authGlowMedium)
            .blur(OmniTokens.spacing.section - OmniTokens.spacing.s + OmniTokens.spacing.tiny, BlurredEdgeTreatment.Unbounded)
            .background(colors.glow, CircleShape),
    )
}
