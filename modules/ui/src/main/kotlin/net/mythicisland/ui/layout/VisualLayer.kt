package net.mythicisland.ui.layout

import net.mythicisland.ui.text.VisualElement
import net.mythicisland.ui.text.VisualSpace
import net.mythicisland.ui.text.VisualText

/**
 * Absolute positioning on a single line. Children are placed at pixel offsets
 * and may overlap, the layer moves the cursor back and forth with [VisualSpace].
 */
class VisualLayer private constructor(
    private val width: Int?,
    private val children: List<Child>,
) {

    constructor(width: Int? = null) : this(width, emptyList())

    fun child(x: Int, element: VisualElement, advance: Int = 0): VisualLayer =
        VisualLayer(width, children + Child(x, advance, element))

    fun child(x: Int, component: VisualComponent): VisualLayer =
        child(x, component, component.advance)

    fun childEnd(component: VisualComponent): VisualLayer {
        requireNotNull(width) { "Cannot place at the end of a layer without a width." }
        return child(width - component.width, component)
    }

    fun toComponent(): VisualComponent =
        VisualComponent(measuredWidth(), element = toText())

    fun toText(): VisualText {
        val ordered = children.sortedBy(Child::x)
        var cursor = 0
        val content = mutableListOf<VisualElement>()

        ordered.forEach { child ->
            content.add(VisualSpace.pixels(child.x - cursor))
            content.add(child.element)
            cursor = child.x + child.advance
        }

        if (width != null) {
            content.add(VisualSpace.pixels(width - cursor))
        }

        return VisualText.of(content)
    }

    private fun measuredWidth(): Int =
        width ?: children.maxOfOrNull { it.x + it.advance } ?: 0

    private data class Child(
        val x: Int,
        val advance: Int,
        val element: VisualElement,
    )
}