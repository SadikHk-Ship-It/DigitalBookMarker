package com.example.digitalbookmark.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

enum class ThemeMode(val label: String) { SYSTEM("System"), LIGHT("Light"), DARK("Dark") }

/** Things that can have their color changed. */
enum class ColorSlot(val label: String) {
    Background("Background"),
    Card("Boxes / cards"),
    Border("Borders"),
    Text("Main text"),
    SecondaryText("Secondary text"),
    Accent("Buttons & highlights"),
    AccentText("Text on buttons"),
    Chip("Status chip"),
    ChipText("Status chip text")
}

data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val overrides: Map<ColorSlot, Color> = emptyMap()
)

/** The color a slot currently has in this scheme (used by the settings screen). */
fun ColorScheme.colorFor(slot: ColorSlot): Color = when (slot) {
    ColorSlot.Background -> background
    ColorSlot.Card -> surfaceVariant
    ColorSlot.Border -> outline
    ColorSlot.Text -> onSurface
    ColorSlot.SecondaryText -> onSurfaceVariant
    ColorSlot.Accent -> primary
    ColorSlot.AccentText -> onPrimary
    ColorSlot.Chip -> primaryContainer
    ColorSlot.ChipText -> onPrimaryContainer
}

/** Applies the user's chosen colors on top of a base scheme. */
fun ColorScheme.withOverrides(o: Map<ColorSlot, Color>): ColorScheme {
    var s = this
    o[ColorSlot.Background]?.let { s = s.copy(background = it, surface = it) }
    o[ColorSlot.Card]?.let {
        s = s.copy(
            surfaceVariant = it,
            surfaceContainer = it,
            surfaceContainerHigh = it,
            surfaceContainerHighest = it
        )
    }
    o[ColorSlot.Border]?.let { s = s.copy(outline = it, outlineVariant = it) }
    o[ColorSlot.Text]?.let { s = s.copy(onBackground = it, onSurface = it) }
    o[ColorSlot.SecondaryText]?.let { s = s.copy(onSurfaceVariant = it) }
    o[ColorSlot.Chip]?.let { s = s.copy(primaryContainer = it, secondaryContainer = it) }
    o[ColorSlot.ChipText]?.let { s = s.copy(onPrimaryContainer = it, onSecondaryContainer = it) }
    if (o.isEmpty()) return s

    return s.copy(
        onBackground = s.onBackground.readableOn(s.background, min = 1.5f),
        onSurface = s.onSurface.readableOn(listOf(s.background, s.surfaceVariant), min = 1.5f),
        onSurfaceVariant = s.onSurfaceVariant.readableOn(listOf(s.background, s.surfaceVariant), min = 1.5f),
        primary = s.primary.readableOn(s.background, min = 2f),
        onPrimary = s.onPrimary.readableOn(s.primary),
        onPrimaryContainer = s.onPrimaryContainer.readableOn(s.primaryContainer),
        onSecondaryContainer = s.onSecondaryContainer.readableOn(s.secondaryContainer),
        outline = s.outline.readableOn(listOf(s.background, s.surfaceVariant), min = 1.8f)
    )
}

private fun contrastRatio(a: Color, b: Color): Float {
    val l1 = a.luminance()
    val l2 = b.luminance()
    return (maxOf(l1, l2) + 0.05f) / (minOf(l1, l2) + 0.05f)
}

/** Keeps this color if it is readable on all given backgrounds, otherwise returns black or white. */
private fun Color.readableOn(backgrounds: List<Color>, min: Float = 3f): Color {
    if (backgrounds.all { contrastRatio(this, it) >= min }) return this
    val avg = backgrounds.map { it.luminance() }.average()
    return if (avg > 0.4) Color.Black else Color.White
}

private fun Color.readableOn(background: Color, min: Float = 3f): Color =
    readableOn(listOf(background), min)