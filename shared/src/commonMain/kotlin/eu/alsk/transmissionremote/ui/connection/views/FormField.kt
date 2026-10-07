package eu.alsk.transmissionremote.ui.connection.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.ui.theme.AppTheme

/** What a [FormField] shows. [error] is shown below the field and marks it invalid when not null. */
internal data class FormFieldState(
    val label: String,
    val value: String,
    val placeholder: String? = null,
    val error: String? = null,
    val keyboardType: KeyboardType = KeyboardType.Text,
)

@Composable
internal fun FormField(
    state: FormFieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = state.value,
        onValueChange = onValueChange,
        label = { Text(state.label) },
        placeholder = state.placeholder?.let { { Text(it) } },
        isError = state.error != null,
        supportingText = state.error?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = state.keyboardType, imeAction = ImeAction.Next),
        modifier = modifier.fillMaxWidth(),
    )
}

private val dummyFormFieldState = FormFieldState(label = "Host", value = "192.168.1.10")

private val dummyFormFieldStateWithPlaceholder = FormFieldState(
    label = "Host",
    value = "",
    placeholder = "192.168.1.10 or nas.local",
)

private val dummyFormFieldStateWithError = FormFieldState(
    label = "Port",
    value = "99999",
    error = "Port must be between 1 and 65535",
    keyboardType = KeyboardType.Number,
)

@Preview(showBackground = true)
@Composable
private fun FormFieldPreview() {
    AppTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FormField(dummyFormFieldState, {})
            FormField(dummyFormFieldStateWithPlaceholder, {})
            FormField(dummyFormFieldStateWithError, {})
        }
    }
}
