package net.mythicisland.core.command.impl

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandContext
import net.mythicisland.core.command.argument.CommandArgument

/**
 * Default [net.mythicisland.core.command.CommandContext], reading what Minestom parsed.
 *
 * @param elements the arguments of the syntax that ran, used to catch reads
 * with an argument of another syntax.
 */
class CommandContextImpl(
    override val sender: CommandSender,
    private val context: CommandContext,
    private val elements: List<CommandArgument<*>>,
) : net.mythicisland.core.command.CommandContext {

    override val input: String
        get() = context.input

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
