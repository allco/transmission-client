package eu.alsk.transmissionremote.ui.connection

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

private val dummyFormFieldLabel = "Host"
private val dummyFormFieldValue = "192.168.1.10"
private val dummyFormFieldPlaceholder = "192.168.1.10 or nas.local"
private val dummyFormFieldErrorLabel = "Port"
private val dummyFormFieldErrorValue = "99999"
private val dummyFormFieldError = "Port must be between 1 and 65535"

@Preview(showBackground = true)
@Composable
private fun FormFieldPreview() {
    AppTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FormField(dummyFormFieldLabel, dummyFormFieldValue, {})
            FormField(dummyFormFieldLabel, "", {}, placeholder = dummyFormFieldPlaceholder)
            FormField(dummyFormFieldErrorLabel, dummyFormFieldErrorValue, {}, error = dummyFormFieldError)
        }
    }
}
