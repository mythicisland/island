package net.mythicisland.ui.interfaces

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.kyori.adventure.text.ComponentLike
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.tag.Tag
import net.mythicisland.ui.interfaces.click.InterfaceClick
import net.mythicisland.ui.interfaces.interfaces.Interface
import net.mythicisland.ui.interfaces.view.InterfaceView

/**
 * Routes clicks to the interface a player has open.
 *
 * The open view is stored on the player itself, so a player who disconnects
 * with a menu open leaves nothing behind.
 */
object Interfaces {

    private val viewTag = Tag.Transient<InterfaceView>("island:interface_view")

    private val node = EventNode.all("interfaces")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Registers the listeners, called once on startup. */
    fun initialize(): Interfaces {
        node.addListener(InventoryPreClickEvent::class.java) { event -> click(event) }
        node.addListener(InventoryCloseEvent::class.java) { event -> close(event) }

        MinecraftServer.getGlobalEventHandler().addChild(node)
        return this
    }

    /** The interface [player] has open, or null if there is none. */
    fun opened(player: Player): InterfaceView? =
        player.getTag(viewTag)

    /**
     * Creates the window of an interface. The title stays a component, so the
     * platform translates it for its viewer like any other packet.
     */
    internal fun window(rows: Int, title: ComponentLike): Inventory =
        Inventory(type(rows), title.asComponent())

    /**
     * Opens [definition] for [player]. The view is stored before the window is
     * shown, so a menu that opens another menu keeps working.
     */
    internal fun open(player: Player, definition: Interface, window: Inventory?): InterfaceView {
        val view = InterfaceView(player, definition, window)
        player.setTag(viewTag, view)

        when (window) {
            null -> Unit
            else -> player.openInventory(window)
        }

        launch { view.redraw() }
        return view
    }

    internal fun forget(player: Player) {
        player.removeTag(viewTag)
    }

    internal fun launch(block: suspend () -> Unit): Job =
        scope.launch { block() }

    private fun type(rows: Int): InventoryType = when (rows) {
        1 -> InventoryType.CHEST_1_ROW
        2 -> InventoryType.CHEST_2_ROW
        3 -> InventoryType.CHEST_3_ROW
        4 -> InventoryType.CHEST_4_ROW
        5 -> InventoryType.CHEST_5_ROW
        else -> InventoryType.CHEST_6_ROW
    }

    /**
     * Cancels every click in an inventory the pane covers and lets the element
     * in that slot decide what happens.
     */
    private fun click(event: InventoryPreClickEvent) {
        val view = event.player.getTag(viewTag) ?: return

        when {
            view.draws(event.inventory) -> {
                event.isCancelled = true
                when (val element = view.find(event.inventory, event.slot)) {
                    null -> Unit
                    else -> launch { element.onClick(InterfaceClick(view, event.slot, event.click)) }
                }
            }

            else -> event.isCancelled = !view.allowsPlayerInventory()
        }
    }

    /**
     * Forgets the view once its window closes and writes the saved items of the
     * player back. A player interface has no window and survives the player
     * opening and closing their inventory.
     */
    private fun close(event: InventoryCloseEvent) {
        val view = event.player.getTag(viewTag) ?: return

        when {
            view.closes(event.inventory) -> {
                event.player.removeTag(viewTag)
                view.restore()
            }

            else -> Unit
        }
    }
}
