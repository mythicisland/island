package net.mythicisland.core.command.impl

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.entity.Player
import net.mythicisland.core.command.argument.CommandArgument

class CommandContextImpl(
    override val sender: CommandSender,
    private val context: CommandContext,
    private val elements: List<CommandArgument<*>>,
) : net.mythicisland.core.command.CommandContext {

    override val input: String
        get() = context.input

    override val player: Player
        get() = checkNotNull(sender as? Player) {
            "The command was run by the console, mark the chain with playerOnly()."
        }

    override fun <T : Any> get(argument: CommandArgument<T>): T {
        val value = find(argument)
        requireNotNull(value) {
            "Argument '${argument.name}' was left out and has no default value, use find() instead."
        }
        return value
    }

    override fun <T : Any> find(argument: CommandArgument<T>): T? {
        require(elements.contains(argument)) {
            "Argument '${argument.name}' does not belong to the syntax that ran."
        }

        if (context.has(argument.argument)) {
            return context.get(argument.argument)
        }
        return argument.defaultValue?.invoke()
    }
}
