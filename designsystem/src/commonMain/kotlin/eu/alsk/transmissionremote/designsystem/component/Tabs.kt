package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.theme.AppTheme

/**
 * Shows a row of tabs with the same width. The user selects one tab at a time.
 *
 * The tab at [selectedIndex] shows the primary colour and an indicator line. A click on a tab calls
 * [onSelect] with the index of that tab.
 */
@Composable
public fun Tabs(
    titles: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrimaryTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = AppTheme.colors.surface,
        contentColor = AppTheme.colors.primary,
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                text = {
                    Text(
                        text = title,
                        style = AppTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                selectedContentColor = AppTheme.colors.primary,
                unselectedContentColor = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

private val dummyTabTitles = listOf("General", "Files", "Peers", "Trackers")
private val dummyTabSelectedIndex = 1

@Composable
private fun TabsPreviewContent() {
    Surface {
        Tabs(titles = dummyTabTitles, selectedIndex = dummyTabSelectedIndex, onSelect = {})
    }
}

@Preview
@Composable
private fun TabsPreview() {
    AppTheme(darkTheme = false) { TabsPreviewContent() }
}

@Preview
@Composable
private fun TabsDarkPreview() {
    AppTheme(darkTheme = true) { TabsPreviewContent() }
}
