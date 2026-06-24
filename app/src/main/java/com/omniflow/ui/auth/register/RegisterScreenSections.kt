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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omniflow.R
import com.omniflow.uicomponents.OmniTextField

@Composable
internal fun RegisterHeader() {
    Box(
        modifier = Modifier
            .size(52.dp)
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(RegisterBlue, Color(0xFF4DB3FF)),
                    start = Offset.Zero,
                    end = Offset(52f, 52f),
                ),
                RoundedCornerShape(18.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(22.dp).background(Color.White, CircleShape))
        Box(Modifier.size(14.dp).background(RegisterBlue, CircleShape))
    }
    Spacer(Modifier.height(28.dp))
    Text(
        text = stringResource(R.string.register_title),
        color = RegisterTextPrimary,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.register_subtitle),
        color = RegisterTextSecondary,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(R.string.register_verification_hint),
        color = RegisterBlue,
        fontSize = 14.sp,
        lineHeight = 20.sp,
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
    FieldLabel(label)
    OmniTextField(
        value = value,
        onValueChange = onValueChange,
        label = null,
        placeholder = label,
        placeholderColor = RegisterTextSecondary,
        enabled = enabled,
        errorMessage = error,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(18.dp),
        focusedBorderColor = RegisterBlue,
        unfocusedBorderColor = RegisterBorder,
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
    FieldLabel(label)
    OmniTextField(
        value = value,
        onValueChange = onValueChange,
        label = null,
        placeholder = label,
        placeholderColor = RegisterTextSecondary,
        enabled = enabled,
        errorMessage = error,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle, enabled = enabled) {
                Icon(
                    imageVector = if (isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    tint = RegisterTextSecondary,
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
        shape = RoundedCornerShape(18.dp),
        focusedBorderColor = RegisterBlue,
        unfocusedBorderColor = RegisterBorder,
    )
}

@Composable
private fun FieldLabel(label: String) {
    Text(
        text = label,
        color = RegisterTextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
    )
}

@Composable
internal fun PasswordChecklist(requirements: PasswordRequirements) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 14.dp),
    ) {
        requirements.toUiModels().forEach { item ->
            val color = if (item.isMet) RequirementMet else RegisterTextSecondary
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (item.isMet) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(item.labelRes), color = color, fontSize = 13.sp)
            }
        }
    }
}

@Composable
internal fun RegisterSocialLogin() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 28.dp, bottom = 20.dp),
    ) {
        HorizontalDivider(Modifier.weight(1f), color = RegisterDivider)
        Text(
            text = stringResource(R.string.register_social_divider),
            color = RegisterTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        HorizontalDivider(Modifier.weight(1f), color = RegisterDivider)
    }
    OutlinedButton(
        onClick = {},
        enabled = false,
        modifier = Modifier.fillMaxWidth().height(58.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, RegisterBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            disabledContainerColor = Color.White,
            disabledContentColor = RegisterTextPrimary,
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google_g),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.register_continue_google),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun RegisterFooter(enabled: Boolean, onLoginClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
    ) {
        Text(
            text = stringResource(R.string.register_have_account) + " ",
            color = RegisterTextSecondary,
            fontSize = 14.sp,
        )
        Text(
            text = stringResource(R.string.register_login),
            color = RegisterBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = enabled, onClick = onLoginClick),
        )
    }
}

@Composable
internal fun BoxScope.RegisterGlow() {
    Box(
        Modifier
            .align(Alignment.TopEnd)
            .offset(x = 96.dp, y = (-120).dp)
            .size(300.dp)
            .blur(50.dp, BlurredEdgeTreatment.Unbounded)
            .background(Color(0x1F007BFF), CircleShape),
    )
}

internal val RegisterBlue = Color(0xFF007BFF)
internal val RegisterBackground = Color(0xFFF5F7F8)
internal val RegisterTextPrimary = Color(0xFF0F172A)
internal val RegisterTextSecondary = Color(0xFF71829B)
private val RegisterBorder = Color(0xFFD7E0EA)
private val RegisterDivider = Color(0xFFE8EEF5)
private val RequirementMet = Color(0xFF16A34A)
