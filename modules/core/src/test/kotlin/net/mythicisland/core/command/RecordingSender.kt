package net.mythicisland.core.command

import net.kyori.adventure.text.Component
import net.minestom.server.command.ConsoleSender

class RecordingSender : ConsoleSender() {

    val messages = mutableListOf<Component>()

    override fun sendMessage(message: Component) {
        messages.add(message)
    }
}
