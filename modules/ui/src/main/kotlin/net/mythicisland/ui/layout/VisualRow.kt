package net.mythicisland.ui.layout

/**
 * Sequential horizontal layout with an optional gap between the children.
 */
class VisualRow private constructor(
    private val gap: Int,
    private val children: List<VisualComponent>,
) {

    constructor(gap: Int = 0) : this(gap, emptyList())

    fun child(component: VisualComponent): VisualRow =
        VisualRow(gap, children + component)

    fun children(components: Iterable<VisualComponent>): VisualRow =
        VisualRow(gap, children + components)

    fun toComponent(): VisualComponent {
        var cursor = 0
        var layer = VisualLayer()

        children.forEachIndexed { index, child ->
            if (index > 0) {
                cursor += gap
            }

            layer = layer.child(x = cursor, child)
            cursor += child.width
        }

        return VisualComponent(cursor, element = layer.toText())
    }

    /**
     * Places the row within a fixed [width], aligned by [align]. Handy to center
     * a narrow row underneath a wider line. When the row is wider than [width] it
     * is left untouched and starts at zero.
     */
    fun toComponent(width: Int, align: VisualAlignment = VisualAlignment.CENTER): VisualComponent {
        require(width >= 0) { "width must be zero or positive." }

        val row = toComponent()
        val offset = when {
            row.width >= width -> 0
            align == VisualAlignment.START -> 0
            align == VisualAlignment.CENTER -> (width - row.width) / 2
            else -> width - row.width
        }

        return VisualLayer(width = width)
            .child(x = offset, row)
            .toComponent()
    }
}