package net.mythicisland.core.command.impl

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandRegistry
import java.util.Locale

/**
 * Default [net.mythicisland.core.command.CommandRegistry], also keeping the Minestom command every
 * registered command was built into.
 */
class CommandRegistryImpl : CommandRegistry {

    private val commands = ObjectArrayList<Command>()
    private val commandsByName = Object2ObjectOpenHashMap<String, Command>()
    private val minestomCommands = Object2ObjectOpenHashMap<Command, net.minestom.server.command.builder.Command>()

    override fun getCommands(): List<Command> =
        ObjectArrayList(commands)

    override fun getCommand(name: String): Command? =
        commandsByName[name.lowercase(Locale.ROOT)]

    override fun isRegistered(name: String): Boolean =
        commandsByName.containsKey(name.lowercase(Locale.ROOT))

    /**
     * Adds a command and claims its name and aliases.
     *
     * @param command the command to add.
     * @param minestomCommand the Minestom command it was built into.
     * @throws IllegalStateException if the name or one of the aliases is taken.
     */
    internal fun add(command: Command, minestomCommand: net.minestom.server.command.builder.Command) {
        val names = namesOf(command)
        for (name in names) {
            check(!commandsByName.containsKey(name)) {
                "A command with the name '$name' is already registered."
            }
        }

        commands.add(command)
        minestomCommands[command] = minestomCommand
        for (name in names) {
            commandsByName[name] = command
        }
    }

    /**
     * Removes a command and releases its name and aliases.
     *
     * @param command the command to remove.
     * @return the Minestom command it was built into, null if it was not
     * registered.
     */
    internal fun remove(command: Command): net.minestom.server.command.builder.Command? {
        val minestomCommand = minestomCommands.remove(command) ?: return null

        commands.remove(command)
        for (name in namesOf(command)) {
            commandsByName.remove(name)
        }
        return minestomCommand
    }

    private fun namesOf(command: Command): List<String> =
        (listOf(command.name) + command.aliases).map { name -> name.lowercase(Locale.ROOT) }
}