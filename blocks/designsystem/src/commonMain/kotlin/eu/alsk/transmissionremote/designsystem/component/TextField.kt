package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows an outlined text field with a label. The field fills the full width.
 *
 * When [error] is not null, the field shows the error colours and shows [error] below the field.
 * When [error] is null, the field shows [supportingText] below the field, if it is set.
 */
@Composable
public fun TextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val bottomText = error ?: supportingText
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        trailingIcon = trailingIcon,
        supportingText = bottomText?.let { { Text(it) } },
        isError = error != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        shape = RoundedCornerShape(Radius.sm),
    )
}

private val dummyTextFieldValue = "nas.local"
private val dummyTextFieldEmptyValue = ""
private val dummyTextFieldError = "Enter a host name or an IP address"

@Composable
private fun TextFieldPreviewContent() {
    Surface {
        Column(
            modifier = Modifier.padding(Spacing.s16),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            TextField(
                label = "Host",
                value = dummyTextFieldValue,
                onValueChange = {},
                supportingText = "The address of the Transmission daemon",
            )
            TextField(
                label = "Host",
                value = dummyTextFieldEmptyValue,
                onValueChange = {},
                placeholder = "192.168.1.10 or nas.local",
            )
            TextField(
                label = "Host",
                value = dummyTextFieldEmptyValue,
                onValueChange = {},
                error = dummyTextFieldError,
            )
            TextField(
                label = "Host",
                value = dummyTextFieldValue,
                onValueChange = {},
                enabled = false,
            )
        }
    }
}

@Preview
@Composable
private fun TextFieldPreview() {
    AppTheme(darkTheme = false) { TextFieldPreviewContent() }
}

@Preview
@Composable
private fun TextFieldDarkPreview() {
    AppTheme(darkTheme = true) { TextFieldPreviewContent() }
}
