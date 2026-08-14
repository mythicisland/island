package net.mythicisland.ui.render

import net.kyori.adventure.text.Component

/**
 * Dynamic ui content.
 */
fun interface UiRenderable {

    fun render(context: UiRenderContext): Component

    companion object {
        fun static(component: Component): UiRenderable =
            UiRenderable { component }
    }
}