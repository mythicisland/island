package net.mythicisland.ui.interfaces

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.mythicisland.ui.interfaces.interfaces.ChestInterface
import net.mythicisland.ui.interfaces.interfaces.CombinedInterface
import net.mythicisland.ui.interfaces.interfaces.PlayerInterface
import net.mythicisland.ui.interfaces.transform.InterfaceTransform

/**
 * Collects the options and transforms an interface is built from.
 */
class InterfaceBuilder {

    /** The number of rows, between one and [net.mythicisland.ui.interfaces.interfaces.ChestInterface.MAX_ROWS]. */
    var rows: Int = ChestInterface.MAX_ROWS

    /**
     * The title of the window. A translation key can be used directly, the
     * platform resolves it for each viewer.
     */
    var title: ComponentLike = Component.empty()

    /** Whether the player may still use the slots the pane does not cover. */
    var allowPlayerInventory: Boolean = false

    private val transforms = mutableListOf<InterfaceTransform>()

    /**
     * Adds a transform. They run in the order they were added, so a later one
     * can overwrite the slots of an earlier one.
     */
    fun withTransform(transform: InterfaceTransform) {
        transforms.add(transform)
    }

    internal fun transforms(): List<InterfaceTransform> =
        transforms.toList()
}

/**
 * Builds a menu in a window of its own.
 */
fun buildChestInterface(builder: InterfaceBuilder.() -> Unit): ChestInterface {
    val scope = InterfaceBuilder()
    scope.builder()

    return ChestInterface(scope.rows, scope.title, scope.allowPlayerInventory, scope.transforms())
}

/**
 * Builds a menu that spans the window and the inventory of the player.
 *
 * Here `rows` is the height of the window alone, the four rows of the player
 * are added below it.
 */
fun buildCombinedInterface(builder: InterfaceBuilder.() -> Unit): CombinedInterface {
    val scope = InterfaceBuilder()
    scope.builder()

    return CombinedInterface(scope.rows, scope.title, scope.transforms())
}

/**
 * Builds a menu drawn into the inventory of the player.
 *
 * An inventory of a player has neither a title nor a free height, so `title`
 * and `rows` are ignored.
 */
fun buildPlayerInterface(builder: InterfaceBuilder.() -> Unit): PlayerInterface {
    val scope = InterfaceBuilder()
    scope.builder()

    return PlayerInterface(scope.transforms())
}
