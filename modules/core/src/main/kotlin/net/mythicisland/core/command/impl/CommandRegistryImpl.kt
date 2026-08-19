package net.mythicisland.core.command.impl

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandRegistry
import java.util.Locale
import net.minestom.server.command.builder.Command as MinestomCommand

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
