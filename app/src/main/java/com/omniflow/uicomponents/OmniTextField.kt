package com.omniflow.uicomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.input.VisualTransformation
import com.omniflow.core.designsystem.theme.OmniTokens

@Composable
fun OmniTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String?,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    placeholderColor: Color = Color.Unspecified,
    shape: Shape = MaterialTheme.shapes.medium,
    focusedBorderColor: Color = Color.Unspecified,
    unfocusedBorderColor: Color = Color.Unspecified,
    enabled: Boolean = true,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = errorMessage != null,
) {
    val colors = OmniTokens.colors
    val resolvedPlaceholderColor = placeholderColor.takeOrElse { colors.hint }
    val resolvedFocusedBorderColor = focusedBorderColor.takeOrElse { colors.fieldBorder }
    val resolvedUnfocusedBorderColor = unfocusedBorderColor.takeOrElse { colors.fieldBorder }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = OmniTokens.dimens.authControlHeight),
        label = label?.let { text -> { Text(text = text) } },
        placeholder = placeholder?.let { text ->
            { Text(text = text, color = resolvedPlaceholderColor, style = MaterialTheme.typography.bodyMedium) }
        },
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.takeIf { it.isNotEmpty() }?.let { message ->
            { Text(text = message) }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.fieldContainer,
            unfocusedContainerColor = colors.fieldContainer,
            disabledContainerColor = colors.fieldContainer,
            focusedBorderColor = resolvedFocusedBorderColor,
            unfocusedBorderColor = resolvedUnfocusedBorderColor,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = colors.hint,
            errorContainerColor = colors.fieldContainer,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorSupportingTextColor = MaterialTheme.colorScheme.error,
        )
    )
}
