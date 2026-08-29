package net.mythicisland.ui.interfaces.click

import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.Click
import net.mythicisland.ui.interfaces.view.InterfaceView

/**
 * A click on an element.
 *
 * [click] is a sealed type, so a handler can tell the buttons apart with an
 * exhaustive `when`.
 */
data class InterfaceClick(
    val view: InterfaceView,
    val slot: Int,
    val click: Click,
) {

    /** The player who clicked. */
    val player: Player
        get() = view.player
}