package net.mythicisland.ui.interfaces.interfaces

import net.kyori.adventure.text.ComponentLike
import net.minestom.server.entity.Player
import net.mythicisland.ui.interfaces.transform.InterfaceTransform
import net.mythicisland.ui.interfaces.view.InterfaceView
import net.mythicisland.ui.interfaces.Interfaces

/**
 * A menu that spans the window and the inventory of the player, for interfaces
 * drawn across the whole screen.
 *
 * The first [windowRows] rows are the window. The four below them are the
 * inventory of the player, three main rows followed by the hotbar.
 *
 * The items of the player are saved when the menu opens and written back when
 * it closes, so nothing is lost while the menu covers them.
 */
class CombinedInterface internal constructor(
    override val windowRows: Int,
    val title: ComponentLike,
    override val transforms: List<InterfaceTransform>,
) : Interface {

    init {
        require(windowRows in 1..ChestInterface.MAX_ROWS) {
            "A combined interface has between 1 and ${ChestInterface.MAX_ROWS} window rows."
        }
    }

    override val rows: Int = windowRows + Interface.PLAYER_ROWS

    /** The pane covers every slot, so none is left for the player. */
    override val allowPlayerInventory: Boolean = false

    override fun open(player: Player): InterfaceView =
        Interfaces.open(player, this, Interfaces.window(windowRows, title))
}