package net.mythicisland.core.command.impl

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandContext
import net.mythicisland.core.command.argument.CommandArgument

/**
 * Default [net.mythicisland.core.command.CommandContext], reading the values parsed by Minestom.
 *
 * @param parameters the arguments declared by the executed branch, used to
 * reject lookups with arguments of another branch.
 */
internal class CommandContextImpl(
    override val sender: CommandSender,
    private val context: CommandContext,
    private val parameters: List<CommandArgument<*>>,
) : net.mythicisland.core.command.CommandContext {

    override val input: String
        get() = context.input

    override fun <T : Any> get(argument: CommandArgument<T>): T {
        val value = find(argument)
        requireNotNull(value) {
            "Argument '${argument.name}' was not given and has no default value, use find() instead."
        }
        return value
    }

    override fun <T : Any> find(argument: CommandArgument<T>): T? {
        require(parameters.contains(argument)) {
            "Argument '${argument.name}' is not part of the executed syntax."
        }

        if (context.has(argument.argument)) {
            return context.get(argument.argument)
        }
        return argument.defaultValue?.invoke()
    }
}