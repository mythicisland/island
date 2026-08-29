package net.mythicisland.ui.interfaces.transform

import net.mythicisland.ui.interfaces.pane.InterfacePane
import net.mythicisland.ui.interfaces.view.InterfaceView

/**
 * Draws one part of an interface.
 *
 * Every transform runs again each time the view is drawn, in the order they
 * were added, so a later one can overwrite the slots of an earlier one. A
 * transform may suspend, which is how content that has to be loaded first
 * reaches a menu.
 */
fun interface InterfaceTransform {

    suspend fun apply(pane: InterfacePane, view: InterfaceView)
}