package net.mythicisland.ui.interfaces.interfaces

import net.minestom.server.entity.Player
import net.mythicisland.ui.interfaces.transform.InterfaceTransform
import net.mythicisland.ui.interfaces.view.InterfaceView

/**
 * The definition of a menu. It holds no player state and is built once, then
 * opened for any number of players.
 *
 * A pane starts at the top of the window and continues into the inventory of
 * the player, which lets a menu use the whole screen. [windowRows] says where
 * the window ends and the inventory of the player begins.
 */
sealed interface Interface {

    /** The height of the pane, window and player inventory together. */
    val rows: Int

    /** How many of those rows are drawn into a window. The rest go to the player. */
    val windowRows: Int

    /** Whether the player may still use the slots the pane does not cover. */
    val allowPlayerInventory: Boolean

    /** The transforms that draw it, in the order they run. */
    val transforms: List<InterfaceTransform>

    /**
     * Opens the interface for [player] and returns its view. The content
     * appears one tick later, once every transform has run.
     */
    fun open(player: Player): InterfaceView

    companion object {

        /** The rows of the inventory of a player: three main rows and the hotbar. */
        const val PLAYER_ROWS = 4
    }
}