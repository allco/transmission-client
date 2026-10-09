package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.alsk.transmissionremote.designsystem.icon.AppIcons
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing

private val BottomSheetShape = RoundedCornerShape(topStart = Radius.xxl, topEnd = Radius.xxl)

/** Shows a modal sheet from the bottom of the screen, with a drag handle, a title and [content]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun BottomSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = BottomSheetShape,
        containerColor = AppTheme.colors.surfaceContainerLow,
        contentColor = AppTheme.colors.onSurface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        BottomSheetContent(title = title, content = content)
    }
}

/** Shows the title and the content of a [BottomSheet], below the drag handle. */
@Composable
private fun BottomSheetContent(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = Spacing.s24)) {
        Text(
            text = title,
            modifier = Modifier.padding(start = Spacing.s24, end = Spacing.s24, bottom = Spacing.s16),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.onSurface,
        )
        content()
    }
}

private const val dummyBottomSheetTitle = "Sort by"

// The preview shows the sheet content on the sheet surface. A modal sheet does not show in all
// preview tools.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetSample() {
    Surface(shape = BottomSheetShape, color = AppTheme.colors.surfaceContainerLow) {
        Column(Modifier.fillMaxWidth()) {
            BottomSheetDefaults.DragHandle(Modifier.align(Alignment.CenterHorizontally))
            BottomSheetContent(title = dummyBottomSheetTitle) {
                ListItem(headline = "Name", leadingIcon = AppIcons.Sort, trailing = ListItemTrailing.Radio(selected = true), onClick = {})
                ListItem(headline = "Date added", leadingIcon = AppIcons.Calendar, trailing = ListItemTrailing.Radio(selected = false), onClick = {})
            }
        }
    }
}

@Preview
@Composable
private fun BottomSheetLightPreview() {
    AppTheme(darkTheme = false) { BottomSheetSample() }
}

@Preview
@Composable
private fun BottomSheetDarkPreview() {
    AppTheme(darkTheme = true) { BottomSheetSample() }
}
