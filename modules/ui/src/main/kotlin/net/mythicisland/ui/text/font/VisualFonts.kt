package net.mythicisland.ui.text.font

import net.mythicisland.ui.text.font.metrics.MinecraftFontMetrics

/**
 * All fonts that bring by moonrise.
 */
object VisualFonts {

    /** The vanilla font, with metrics so text can be measured. */
    val minecraft = VisualFont("minecraft", "default", MinecraftFontMetrics)

    /** Invisible advances for pixel positioning, see [net.mythicisland.moonrise.ui.text.VisualSpace]. */
    val space = VisualFont("ui", "space")

    /** The translucent panel background. */
    val background = VisualFont("ui", "background")
}