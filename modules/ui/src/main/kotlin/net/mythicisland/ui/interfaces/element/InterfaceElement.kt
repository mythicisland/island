package net.mythicisland.ui.interfaces.element

import net.minestom.server.item.ItemStack
import net.mythicisland.ui.interfaces.click.InterfaceClick

/**
 * An item and what happens when it is clicked.
 *
 * The handler may suspend, so it can call an api directly instead of starting
 * a coroutine of its own.
 */
data class InterfaceElement(
    val item: ItemStack,
    val onClick: suspend (InterfaceClick) -> Unit = {},
)