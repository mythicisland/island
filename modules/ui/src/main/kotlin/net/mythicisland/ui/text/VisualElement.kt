package net.mythicisland.ui.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * A piece of visual ui content that can be serialized to a MiniMessage string.
 */
interface VisualElement {

    fun asMiniMessage(): String

    fun asComponent(): Component =
        MiniMessage.miniMessage().deserialize(asMiniMessage())

}