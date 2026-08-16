package net.mythicisland.core.command.impl

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.LuckPerms
import net.minestom.server.command.CommandManager
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandFactory
import net.mythicisland.core.command.CommandRegistry

/**
 * Default [net.mythicisland.core.command.CommandManager]. Builds the commands
 * and hands them to Minestom.
 *
 * ```
 * val commands = CommandManagerImpl(MinecraftServer.getCommandManager(), getLuckPerms())
 *
 * commands.register(IslandCommand(islands))
 * ```
 *
 * @param permissions checks the permissions. Use the LuckPerms constructor
 * below, this one is here to run the tests without LuckPerms.
 */
class CommandManagerImpl(
    private val commandManager: CommandManager,
    permissions: (CommandSender, String) -> Boolean,
    noPermissionMessage: Component,
    playerOnlyMessage: Component,
) : net.mythicisland.core.command.CommandManager {

    /**
     * Creates a manager that asks LuckPerms for the permissions.
     *
     * LuckPerms has to be running already, its player adapter is looked up
     * right away.
     *
     * @param commandManager the Minestom command manager to register in.
     * @param luckPerms the running LuckPerms instance.
     * @param noPermissionMessage sent when somebody lacks a permission.
     * @param playerOnlyMessage sent when the console uses a player only syntax.
     */
    constructor(
        commandManager: CommandManager,
        luckPerms: LuckPerms,
        noPermissionMessage: Component = NO_PERMISSION_MESSAGE,
        playerOnlyMessage: Component = PLAYER_ONLY_MESSAGE,
    ) : this(
        commandManager,
        luckPermsPermissions(luckPerms),
        noPermissionMessage,
        playerOnlyMessage,
    )

    private val registry = CommandRegistryImpl()
    private val factory = CommandFactory(permissions, noPermissionMessage, playerOnlyMessage)

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

    companion object {

        /**
         * Sent when somebody lacks the permission of a command.
         */
        val NO_PERMISSION_MESSAGE: Component = Component.text(
            "You are not allowed to use this command.",
            NamedTextColor.RED,
        )

        /**
         * Sent when the console or the server uses a player only syntax.
         */
        val PLAYER_ONLY_MESSAGE: Component = Component.text(
            "This command can only be used by players.",
            NamedTextColor.RED,
        )

        /**
         * Asks LuckPerms for the permissions.
         *
         * The check runs with the current query options of the player, so the
         * contexts of a permission, world or game mode for example, count.
         * Everybody who is not a player, the console and the server itself,
         * may use every command. LuckPerms handles its own commands the same
         * way.
         *
         * @param luckPerms the running LuckPerms instance.
         * @return the check, holding the player adapter it looked up.
         */
        private fun luckPermsPermissions(luckPerms: LuckPerms): (CommandSender, String) -> Boolean {
            val playerAdapter = luckPerms.getPlayerAdapter(Player::class.java)

            return { sender, permission ->
                if (sender !is Player) {
                    true
                } else {
                    playerAdapter.getPermissionData(sender)
                        .checkPermission(permission)
                        .asBoolean()
                }
            }
        }
    }
}
