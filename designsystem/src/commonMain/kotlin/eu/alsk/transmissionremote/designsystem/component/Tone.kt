package eu.alsk.transmissionremote.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import eu.alsk.transmissionremote.designsystem.theme.AppTheme

/** Selects the status colours of a component, for example a badge, a banner or a progress bar. */
public enum class Tone { Info, Success, Warning, Error, Neutral }

/** Returns the strong colour of this tone, for icons, text and progress. */
public val Tone.color: Color
    @Composable @ReadOnlyComposable get() = when (this) {
        Tone.Info -> AppTheme.statusColors.info
        Tone.Success -> AppTheme.statusColors.success
        Tone.Warning -> AppTheme.statusColors.warning
        Tone.Error -> AppTheme.colors.error
        Tone.Neutral -> AppTheme.colors.onSurfaceVariant
    }

/** Returns the container colour of this tone, for backgrounds. */
public val Tone.containerColor: Color
    @Composable @ReadOnlyComposable get() = when (this) {
        Tone.Info -> AppTheme.statusColors.infoContainer
        Tone.Success -> AppTheme.statusColors.successContainer
        Tone.Warning -> AppTheme.statusColors.warningContainer
        Tone.Error -> AppTheme.colors.errorContainer
        Tone.Neutral -> AppTheme.colors.surfaceContainerHigh
    }
