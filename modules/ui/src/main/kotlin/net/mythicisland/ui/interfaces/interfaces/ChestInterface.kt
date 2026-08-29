package net.mythicisland.ui.interfaces.interfaces

import net.kyori.adventure.text.ComponentLike
import net.minestom.server.entity.Player
import net.mythicisland.ui.interfaces.transform.InterfaceTransform
import net.mythicisland.ui.interfaces.view.InterfaceView
import net.mythicisland.ui.interfaces.Interfaces

/**
 * A menu in a window of its own, which leaves the inventory of the player
 * untouched.
 *
 * Each view gets its own inventory, so content and title can differ from
 * player to player.
 */
class ChestInterface internal constructor(
    override val rows: Int,
    val title: ComponentLike,
    override val allowPlayerInventory: Boolean,
    override val transforms: List<InterfaceTransform>,
) : Interface {

    init {
        require(rows in 1..MAX_ROWS) { "A chest interface has between 1 and $MAX_ROWS rows." }
    }

    /** The whole pane is the window. */
    override val windowRows: Int
        get() = rows

    override fun open(player: Player): InterfaceView =
        Interfaces.open(player, this, Interfaces.window(rows, title))

    companion object {
        const val MAX_ROWS = 6
    }
}