package eu.alsk.transmissionremote.connection.views

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
import eu.alsk.transmissionremote.theme.AppTheme

/**
 * Shows one single-line text field of a form.
 * If [error] is not null, the field shows [error] below itself and marks itself as invalid.
 * [label], [placeholder] and [keyboardType] are config: the call site sets them, and they do not
 * change.
 */
@Composable
internal fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        modifier = modifier.fillMaxWidth(),
    )
}

private val dummyHost = "192.168.1.10"

private val dummyPort = "99999"

private val dummyPortError = "Port must be between 1 and 65535"

@Preview(showBackground = true)
@Composable
private fun FormFieldPreview() {
    AppTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FormField(label = "Host", value = dummyHost, onValueChange = {})
            FormField(label = "Host", value = "", onValueChange = {}, placeholder = "192.168.1.10 or nas.local")
            FormField(
                label = "Port",
                value = dummyPort,
                onValueChange = {},
                error = dummyPortError,
                keyboardType = KeyboardType.Number,
            )
        }
    }
}
