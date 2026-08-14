package net.mythicisland.ui.text.font

import net.mythicisland.ui.layout.VisualComponent
import net.mythicisland.ui.text.VisualText

/**
 * A font of a resource pack, referenced by its `namespace:path` key.
 */
data class VisualFont(
    val namespace: String,
    val path: String,
    val metrics: VisualFontMetrics? = null,
) {

    val key: String = "$namespace:$path"

    fun glyph(codePoint: Int, shadow: Boolean = false): VisualGlyph =
        VisualGlyph(this, codePoint, shadow)

    fun text(content: String, shadow: Boolean = false): VisualText =
        VisualText(wrap(content, shadow))

    /**
     * Measures [content] with this font's [metrics] and returns a positioned
     */
    fun component(content: String, shadow: Boolean = false): VisualComponent =
        width(content).let { width ->
            VisualComponent(width, advance = width + 1, element = text(content, shadow))
        }

    fun width(content: String): Int =
        requireNotNull(metrics) { "Font $key does not have metrics configured." }
            .width(content)

    fun formattedWidth(content: String): Int =
        requireNotNull(metrics) { "Font $key does not have metrics configured." }
            .formattedWidth(content)

    fun wrap(content: String, shadow: Boolean = false): String =
        if (shadow) {
            "<font:$key>$content</font>"
        } else {
            "<shadow:#00000000><font:$key>$content</font></shadow>"
        }
}