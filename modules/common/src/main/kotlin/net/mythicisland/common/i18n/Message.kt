package net.mythicisland.common.i18n

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent

/**
 * A translation key.
 */
@JvmInline
value class Message(val key: String) : ComponentLike {

    /**
     * The message without arguments.
     */
    override fun asComponent(): TranslatableComponent =
        Component.translatable(this.key)

    /**
     * The message with named arguments.
     *
     * Build the arguments with [net.kyori.adventure.text.minimessage.translation.Argument],
     * their names are usable as `<name>` inside the translation.
     *
     * @param arguments the arguments of the message.
     * @return the message.
     */
    fun component(vararg arguments: ComponentLike): TranslatableComponent =
        Component.translatable(this.key).arguments(*arguments)

}
