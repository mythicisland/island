package net.mythicisland.core.command.impl

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandManager
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandFactory
import net.mythicisland.core.command.CommandPermissionHandler
import net.mythicisland.core.command.CommandRegistry

/**
 * Default [net.mythicisland.core.command.CommandManager], registering the built commands in Minestom.
 *
 * ```
 * val commands = IslandCommandManager(
 *     MinecraftServer.getCommandManager(),
 *     CommandPermissionHandler { sender, permission -> permissions.has(sender, permission) },
 * )
 *
 * commands.register(IslandCommand(islands))
 * ```
 *
 * @param commandManager the Minestom command manager to register in.
 * @param permissionHandler answers the permission checks of the commands.
 * @param noPermissionMessage sent when a sender lacks a permission.
 * @param playerOnlyMessage sent when the console uses a player only syntax.
 */
class CommandManagerImpl(
    private val commandManager: CommandManager,
    permissionHandler: CommandPermissionHandler,
    noPermissionMessage: Component = Component.text(
        "You are not allowed to use this command.",
        NamedTextColor.RED,
    ),
    playerOnlyMessage: Component = Component.text(
        "This command can only be used by players.",
        NamedTextColor.RED,
    ),
) : net.mythicisland.core.command.CommandManager {

    private val registry = CommandRegistryImpl()
    private val factory = CommandFactory(permissionHandler, noPermissionMessage, playerOnlyMessage)

    override fun getCommandRegistry(): CommandRegistry = registry

    override fun getRegisteredCommands(): List<Command> = registry.getCommands()

    override fun register(command: Command) {
        val minestomCommand = factory.create(command)

        registry.add(command, minestomCommand)
        commandManager.register(minestomCommand)
    }

    override fun unregister(command: Command) {
        val minestomCommand = registry.remove(command) ?: return

        commandManager.unregister(minestomCommand)
    }
}