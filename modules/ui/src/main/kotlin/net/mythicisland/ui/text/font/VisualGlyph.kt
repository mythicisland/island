package net.mythicisland.ui.text.font

import net.mythicisland.ui.text.VisualElement

/**
 * A single glyph of a [net.mythicisland.ui.text.font.VisualFont].
 */
data class VisualGlyph(
    val font: VisualFont,
    val codePoint: Int,
    val shadow: Boolean = false,
) : VisualElement {

    init {
        require(Character.isValidCodePoint(codePoint)) {
            "Invalid unicode code point: $codePoint"
        }
    }

    val character: String = String(Character.toChars(codePoint))

    override fun asMiniMessage(): String =
        font.wrap(character, shadow)
}