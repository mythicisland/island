package net.mythicisland.ui.text.font

import kotlin.text.iterator

/**
 * Pixel measurements for a font, used to lay content out without guessing widths.
 */
interface VisualFontMetrics {

    /**
     * The rendered width of a single character in pixels, without the
     * one pixel spacing the client adds after every glyph.
     */
    fun charWidth(char: Char): Int

    /**
     * The rendered width of plain text in pixels, including the one pixel
     * spacing between glyphs.
     */
    fun width(content: String): Int {
        if (content.isEmpty()) {
            return 0
        }

        return content.sumOf { charWidth(it) + 1 } - 1
    }

    /**
     * The rendered width of a MiniMessage string in pixels, ignoring all tags.
     */
    fun formattedWidth(content: String): Int =
        width(stripTags(content))

    companion object {
        fun stripTags(miniMessage: String): String =
            buildString {
                var inTag = false

                for (char in miniMessage) {
                    when {
                        char == '<' -> inTag = true
                        char == '>' && inTag -> inTag = false
                        !inTag -> append(char)
                    }
                }
            }
    }
}