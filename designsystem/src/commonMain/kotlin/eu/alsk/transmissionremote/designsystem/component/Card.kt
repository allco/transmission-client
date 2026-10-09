package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.alsk.transmissionremote.designsystem.theme.AppTheme
import eu.alsk.transmissionremote.designsystem.theme.Radius
import eu.alsk.transmissionremote.designsystem.theme.Spacing
import androidx.compose.material3.Card as M3Card

/** Selects the look of a [Card]. */
public enum class CardStyle {
    /** Fills the card with surfaceContainerLow. */
    Filled,

    /** Shows the card on surface with an outlineVariant border. */
    Outlined,
}

/** Shows a group of related content in a rounded container with a padding of 20 dp. */
@Composable
public fun Card(
    modifier: Modifier = Modifier,
    style: CardStyle = CardStyle.Filled,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(Radius.xl)
    val colors = CardDefaults.cardColors(
        containerColor = when (style) {
            CardStyle.Filled -> AppTheme.colors.surfaceContainerLow
            CardStyle.Outlined -> AppTheme.colors.surface
        },
        contentColor = AppTheme.colors.onSurface,
    )
    val border = when (style) {
        CardStyle.Filled -> null
        CardStyle.Outlined -> BorderStroke(1.dp, AppTheme.colors.outlineVariant)
    }
    val inner: @Composable ColumnScope.() -> Unit = {
        Column(Modifier.padding(Spacing.s20), content = content)
    }
    if (onClick == null) {
        M3Card(modifier = modifier, shape = shape, colors = colors, border = border, content = inner)
    } else {
        M3Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, border = border, content = inner)
    }
}

private const val dummyCardTitle = "ubuntu-24.04-desktop-amd64.iso"
private const val dummyCardBody = "5.7 GB, 42 peers"

@Composable
private fun CardSample() {
    Surface(color = AppTheme.colors.surface) {
        Column(Modifier.padding(Spacing.s16), verticalArrangement = Arrangement.spacedBy(Spacing.s16)) {
            CardStyle.entries.forEach { style ->
                Card(modifier = Modifier.fillMaxWidth(), style = style) {
                    Text(dummyCardTitle, style = AppTheme.typography.titleMedium)
                    Text(dummyCardBody, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Preview
@Composable
private fun CardLightPreview() {
    AppTheme(darkTheme = false) { CardSample() }
}

@Preview
@Composable
private fun CardDarkPreview() {
    AppTheme(darkTheme = true) { CardSample() }
}
