package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/** Shows a horizontal line of 1 dp that separates two groups of content. */
@Composable
public fun Divider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, thickness = 1.dp, color = AppTheme.colors.outlineVariant)
}

private const val dummyDividerText = "Content"

@Composable
private fun DividerSample() {
    Surface(color = AppTheme.colors.surface) {
        Column(Modifier.padding(Spacing.s16)) {
            Text(dummyDividerText)
            Divider(Modifier.padding(vertical = Spacing.s8))
            Text(dummyDividerText)
        }
    }
}

@Preview
@Composable
private fun DividerLightPreview() {
    AppTheme(darkTheme = false) { DividerSample() }
}

@Preview
@Composable
private fun DividerDarkPreview() {
    AppTheme(darkTheme = true) { DividerSample() }
}
