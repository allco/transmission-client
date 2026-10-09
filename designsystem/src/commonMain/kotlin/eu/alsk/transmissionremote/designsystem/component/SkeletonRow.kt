package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing

/** Shows a placeholder row while the app loads the data of a list. */
@Composable
public fun SkeletonRow(modifier: Modifier = Modifier) {
    val fill = AppTheme.colors.surfaceContainerHigh
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = Spacing.s16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s16),
    ) {
        Box(Modifier.size(40.dp).background(fill, CircleShape))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.s8)) {
            SkeletonBar(widthFraction = 0.7f, height = 14.dp)
            SkeletonBar(widthFraction = 1f, height = 8.dp)
            SkeletonBar(widthFraction = 0.4f, height = 10.dp)
        }
    }
}

/** Shows one grey bar of a [SkeletonRow]. */
@Composable
private fun SkeletonBar(widthFraction: Float, height: Dp) {
    Box(
        Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .background(AppTheme.colors.surfaceContainerHigh, RoundedCornerShape(Radius.xs)),
    )
}

@Preview
@Composable
private fun SkeletonRowLightPreview() {
    AppTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.surface) { SkeletonRow() }
    }
}

@Preview
@Composable
private fun SkeletonRowDarkPreview() {
    AppTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.surface) { SkeletonRow() }
    }
}
