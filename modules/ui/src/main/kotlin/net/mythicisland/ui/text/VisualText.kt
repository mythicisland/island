package net.mythicisland.ui.text

/**
 * An MiniMessage snippet.
 */
data class VisualText(
    private val content: String,
) : VisualElement {

    override fun asMiniMessage(): String = content

    operator fun plus(other: VisualElement): VisualText =
        VisualText(content + other.asMiniMessage())

    companion object {
        val empty = VisualText("")

        fun raw(miniMessage: String): VisualText =
            VisualText(miniMessage)

        fun of(vararg elements: VisualElement): VisualText =
            VisualText(elements.joinToString("") { it.asMiniMessage() })

        fun of(elements: Iterable<VisualElement>): VisualText =
            VisualText(elements.joinToString("") { it.asMiniMessage() })

        fun join(separator: VisualElement, elements: Iterable<VisualElement>): VisualText =
            VisualText(elements.joinToString(separator.asMiniMessage()) { it.asMiniMessage() })
    }
}