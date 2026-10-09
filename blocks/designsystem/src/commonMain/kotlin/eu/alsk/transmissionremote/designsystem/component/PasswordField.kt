package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/**
 * Shows a text field for a password. The field hides the characters.
 *
 * An icon button at the end of the field shows or hides the characters. The field keeps this
 * choice when the configuration changes.
 */
@Composable
public fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    TextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        error = error,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                icon = if (visible) AppIcons.EyeOff else AppIcons.Eye,
                contentDescription = if (visible) "Hide password" else "Show password",
                onClick = { visible = !visible },
            )
        },
    )
}

private val dummyPassword = "secret"
private val dummyPasswordError = "The password is not correct"

@Composable
private fun PasswordFieldPreviewContent() {
    Surface {
        Column(
            modifier = Modifier.padding(Spacing.s16),
            verticalArrangement = Arrangement.spacedBy(Spacing.s8),
        ) {
            PasswordField(label = "Password", value = dummyPassword, onValueChange = {})
            PasswordField(
                label = "Password",
                value = dummyPassword,
                onValueChange = {},
                error = dummyPasswordError,
            )
        }
    }
}

@Preview
@Composable
private fun PasswordFieldPreview() {
    AppTheme(darkTheme = false) { PasswordFieldPreviewContent() }
}

@Preview
@Composable
private fun PasswordFieldDarkPreview() {
    AppTheme(darkTheme = true) { PasswordFieldPreviewContent() }
}
