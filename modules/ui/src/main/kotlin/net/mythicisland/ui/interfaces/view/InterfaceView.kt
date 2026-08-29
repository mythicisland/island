package net.mythicisland.ui.interfaces.view

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.kyori.adventure.text.ComponentLike
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.MinestomAdventure
import net.minestom.server.entity.Player
import net.minestom.server.inventory.AbstractInventory
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemStack
import net.mythicisland.ui.interfaces.element.InterfaceElement
import net.mythicisland.ui.interfaces.pane.InterfacePane
import net.mythicisland.ui.interfaces.Interfaces
import net.mythicisland.ui.interfaces.interfaces.Interface
import java.util.Locale

/**
 * One interface, open for one player.
 */
class InterfaceView internal constructor(
    val player: Player,
    private val definition: Interface,
    private val window: Inventory?,
) {

    /**
     * The elements by inventory slot. Window and player inventory are kept
     * apart because both number their slots from zero. Only read and written
     * on the tick thread.
     */
    private val windowElements = Int2ObjectOpenHashMap<InterfaceElement>()
    private val playerElements = Int2ObjectOpenHashMap<InterfaceElement>()

    private val windowSize = definition.windowRows * InterfacePane.COLUMNS

    /** Whether the pane reaches into the inventory of the player. */
    private val usesPlayer = definition.rows > definition.windowRows

    /**
     * The items of the player from before the menu opened, written back when it
     * closes. Null when there is nothing to restore.
     */
    private val captured = capture()

    /** The locale the content of this view is drawn for. */
    val locale: Locale
        get() = player.locale ?: MinestomAdventure.getDefaultLocale()

    /**
     * Runs every transform again and writes the result.
     *
     * The writing happens on the next tick, because a transform may have
     * suspended and left the tick thread.
     */
    suspend fun redraw() {
        val pane = InterfacePane(definition.rows)
        for (transform in definition.transforms) {
            transform.apply(pane, this)
        }

        MinecraftServer.getSchedulerManager().scheduleNextTick { draw(pane) }
    }

    /**
     * Draws again in the background, for click handlers that changed something
     * the menu shows.
     */
    fun refresh() {
        Interfaces.launch { redraw() }
    }

    /**
     * Replaces the title of the window. A player interface has no window, there
     * this does nothing.
     */
    fun title(title: ComponentLike) {
        when (window) {
            null -> Unit
            else -> window.title = title.asComponent()
        }
    }

    /**
     * Ends the view. A window is closed, a player interface is only forgotten
     * and the items it drew stay where they are.
     */
    fun close() {
        when (window) {
            null -> Interfaces.forget(player)
            else -> player.closeInventory()
        }
    }

    internal fun find(clicked: AbstractInventory, slot: Int): InterfaceElement? = when {
        clicked === window -> windowElements.get(slot)
        else -> playerElements.get(slot)
    }

    /** Whether this view draws into [clicked] and therefore answers its clicks. */
    internal fun draws(clicked: AbstractInventory): Boolean = when {
        clicked === window -> true
        else -> usesPlayer && clicked === player.inventory
    }

    /**
     * Whether [closed] closing ends this view. Only a window does that, a
     * player interface outlives the inventory screen of its player.
     */
    internal fun closes(closed: AbstractInventory): Boolean =
        window != null && closed === window

    internal fun allowsPlayerInventory(): Boolean =
        definition.allowPlayerInventory

    /** Writes the saved items of the player back, if there are any. */
    internal fun restore() {
        val items = captured ?: return

        for (slot in items.indices) {
            player.inventory.setItemStack(slot, items[slot], false)
        }
        player.inventory.update(player)
    }

    /**
     * Writes the pane into the inventories behind it. No slot sends a packet of
     * its own, each inventory is sent once at the end.
     */
    private fun draw(pane: InterfacePane) {
        windowElements.clear()
        playerElements.clear()

        val opened = window
        when (opened) {
            null -> Unit
            else -> {
                for (index in 0 until windowSize) {
                    place(pane, index, opened, index, windowElements)
                }
                opened.update(player)
            }
        }

        when {
            usesPlayer -> {
                for (index in windowSize until pane.size) {
                    place(pane, index, player.inventory, playerSlot(index - windowSize), playerElements)
                }
                player.inventory.update(player)
            }

            else -> Unit
        }
    }

    private fun place(
        pane: InterfacePane,
        index: Int,
        inventory: AbstractInventory,
        slot: Int,
        elements: Int2ObjectOpenHashMap<InterfaceElement>,
    ) {
        val element = pane.find(index)

        when (element) {
            null -> inventory.setItemStack(slot, ItemStack.AIR, false)
            else -> {
                inventory.setItemStack(slot, element.item, false)
                elements.put(slot, element)
            }
        }
    }

    /**
     * Turns a slot of the player part of the pane into a slot of the inventory
     * of the player. The client numbers the hotbar first, the pane draws it
     * last.
     */
    private fun playerSlot(index: Int): Int = when {
        index < MAIN_SIZE -> index + InterfacePane.COLUMNS
        else -> index - MAIN_SIZE
    }

    private fun capture(): Array<ItemStack>? = when {
        window != null && usesPlayer ->
            Array(PLAYER_SIZE) { slot -> player.inventory.getItemStack(slot) }

        else -> null
    }

    companion object {

        /** The three main rows of the pane, the hotbar is the row after them. */
        private const val MAIN_SIZE = 27

        private const val PLAYER_SIZE = Interface.PLAYER_ROWS * InterfacePane.COLUMNS
    }
}