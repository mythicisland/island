package net.mythicisland.core.command.impl

import net.minestom.server.MinecraftServer
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandFactory
import net.mythicisland.core.command.CommandRegistry

class CommandManagerImpl : net.mythicisland.core.command.CommandManager {

    private val commandManager = MinecraftServer.getCommandManager()
    private val registry = CommandRegistryImpl()
    private val factory = CommandFactory()

    override fun getCommandRegistry(): CommandRegistry = registry

    override fun getRegisteredCommands(): List<Command> = registry.getCommands()

    override fun register(command: Command) {
        val nodes = factory.nodesOf(command)
        val minestomCommand = factory.create(command, nodes)

        registry.add(command, minestomCommand, nodes)
        commandManager.register(minestomCommand)
    }

    override fun unregister(command: Command) {
        val minestomCommand = registry.remove(command) ?: return

        commandManager.unregister(minestomCommand)
    }
}
