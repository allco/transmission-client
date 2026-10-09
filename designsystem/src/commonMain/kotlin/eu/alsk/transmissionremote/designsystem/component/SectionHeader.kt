package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/** Shows the title of a group of list items or settings. */
@Composable
public fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Spacing.s16, top = Spacing.s16, end = Spacing.s16, bottom = Spacing.s8),
        style = AppTheme.typography.titleSmall,
        color = AppTheme.colors.primary,
    )
}

private const val dummySectionHeaderText = "Speed limits"

@Preview
@Composable
private fun SectionHeaderLightPreview() {
    AppTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.surface) { SectionHeader(dummySectionHeaderText) }
    }
}

@Preview
@Composable
private fun SectionHeaderDarkPreview() {
    AppTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.surface) { SectionHeader(dummySectionHeaderText) }
    }
}
