package net.mythicisland.ui.interfaces.pane

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.mythicisland.ui.interfaces.element.InterfaceElement

/**
 * The grid a transform draws into. Rows are counted from the top, columns from
 * the left.
 *
 * A pane is written once and read once, so it is changed in place rather than
 * copied like [net.mythicisland.ui.layout.VisualLayer].
 */
class InterfacePane internal constructor(val rows: Int) {

    private val elements = Int2ObjectOpenHashMap<InterfaceElement>()

    /** The number of slots of the pane. */
    val size: Int
        get() = rows * COLUMNS

    operator fun set(row: Int, column: Int, element: InterfaceElement) {
        elements.put(slot(row, column), element)
    }

    operator fun get(row: Int, column: Int): InterfaceElement? =
        elements.get(slot(row, column))

    /**
     * Places [elements] left to right and top to bottom inside the region and
     * stops as soon as either the region or the elements run out.
     */
    fun fill(rows: IntRange, columns: IntRange, elements: List<InterfaceElement>) {
        var index = 0

        for (row in rows) {
            for (column in columns) {
                if (index >= elements.size) {
                    return
                }

                set(row, column, elements[index])
                index++
            }
        }
    }

    /**
     * Writes [element] into every slot that is still empty, for the filler
     * behind a menu. Call it after the content has been placed.
     */
    fun fillEmpty(element: InterfaceElement) {
        for (index in 0 until size) {
            if (!elements.containsKey(index)) {
                elements.put(index, element)
            }
        }
    }

    internal fun find(index: Int): InterfaceElement? =
        elements.get(index)

    private fun slot(row: Int, column: Int): Int {
        require(row in 0 until rows) { "Row $row is outside of the pane." }
        require(column in 0 until COLUMNS) { "Column $column is outside of the pane." }
        return row * COLUMNS + column
    }

    companion object {
        const val COLUMNS = 9
    }
}