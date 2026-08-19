package net.mythicisland.core.command.defaults

import net.kyori.adventure.text.Component
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandBuilder
import net.mythicisland.core.command.CommandResult

class TestCommand : Command {

    override val name = "test"

    override val aliases = listOf("t", "test123", "hello")

    override fun build(command: CommandBuilder) {
        command.executes { context ->
            context.sender.sendMessage(Component.text("Test 1 2 3"))
            CommandResult.Success
        }
    }
}