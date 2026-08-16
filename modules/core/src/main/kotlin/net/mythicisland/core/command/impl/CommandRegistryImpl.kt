package net.mythicisland.core.command.impl

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandRegistry
import java.util.Locale
import net.minestom.server.command.builder.Command as MinestomCommand

/**
 * Default [CommandRegistry]. Also remembers the Minestom command each command
 * was built into, so it can be removed again later.
 */
class CommandRegistryImpl : CommandRegistry {

    private val commands = ObjectArrayList<Command>()
    private val commandsByName = Object2ObjectOpenHashMap<String, Command>()
    private val minestomCommands = Object2ObjectOpenHashMap<Command, MinestomCommand>()

    override fun getCommands(): List<Command> =
        ObjectArrayList(commands)

    override fun getCommand(name: String): Command? =
        commandsByName[name.lowercase(Locale.ROOT)]

    override fun isRegistered(name: String): Boolean =
        commandsByName.containsKey(name.lowercase(Locale.ROOT))

    /**
     * Adds a command and takes its name and aliases.
     *
     * @param command the command to add.
     * @param minestomCommand the Minestom command it was built into.
     * @throws IllegalStateException if the name or one of the aliases is taken.
     */
    fun add(command: Command, minestomCommand: MinestomCommand) {
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
     * Removes a command and frees its name and aliases.
     *
     * @param command the command to remove.
     * @return the Minestom command it was built into, null if it was never
     * added.
     */
    fun remove(command: Command): MinestomCommand? {
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
