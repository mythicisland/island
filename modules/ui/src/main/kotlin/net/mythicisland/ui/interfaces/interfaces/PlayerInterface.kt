package net.mythicisland.ui.interfaces.interfaces

import net.minestom.server.entity.Player
import net.mythicisland.ui.interfaces.transform.InterfaceTransform
import net.mythicisland.ui.interfaces.view.InterfaceView
import net.mythicisland.ui.interfaces.Interfaces

/**
 * A menu drawn into the inventory of the player, without a window.
 *
 * The pane has four rows: three main rows followed by the hotbar.
 *
 * Unlike [CombinedInterface] the previous items are not written back when the
 * view ends. This is for inventories the server owns anyway, such as a lobby
 * hotbar that should stay once the view is gone.
 */
class PlayerInterface internal constructor(
    override val transforms: List<InterfaceTransform>,
) : Interface {

    override val rows: Int = Interface.PLAYER_ROWS

    /** There is no window, every row is drawn into the player. */
    override val windowRows: Int = 0

    /** The pane covers every slot, so none is left for the player. */
    override val allowPlayerInventory: Boolean = false

    override fun open(player: Player): InterfaceView =
        Interfaces.open(player, this, null)
}