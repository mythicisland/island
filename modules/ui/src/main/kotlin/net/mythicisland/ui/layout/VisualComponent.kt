package net.mythicisland.ui.layout

import net.mythicisland.ui.text.VisualElement
import net.mythicisland.ui.text.font.VisualFont

/**
 * A [VisualElement] with a known size.
 */
data class VisualComponent(
    val width: Int,
    val advance: Int = width,
    private val element: VisualElement,
) : VisualElement {

    init {
        require(width >= 0) { "width must be zero or positive." }
    }

    override fun asMiniMessage(): String =
        element.asMiniMessage()

    companion object {
        fun glyph(width: Int, font: VisualFont, codePoint: Int, shadow: Boolean = false): VisualComponent =
            VisualComponent(width, advance = width + 1, element = font.glyph(codePoint, shadow))
    }
}