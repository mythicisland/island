package net.mythicisland.core.command

import net.minestom.server.command.CommandSender
import net.mythicisland.core.command.argument.CommandArgument

/**
 * Gives access to the parsed arguments of a single execution.
 *
 * Arguments are read with the declaration itself instead of a string key, so
 * the returned value is already typed:
 *
 * ```
 * val target: Player = context[targetArgument]
 * ```
 */
interface CommandContext {

    /**
     * The sender that executed the command.
     */
    val sender: CommandSender

    /**
     * The raw command string, without the leading slash.
     */
    val input: String

    /**
     * Gets the value of an argument.
     *
     * Optional arguments fall back to their default value when the sender left
     * them out.
     *
     * @param argument the argument declaration to read.
     * @return the parsed value.
     * @throws IllegalArgumentException if the argument is not part of the
     * executed syntax, or if it is optional, was left out and has no default.
     */
    operator fun <T : Any> get(argument: CommandArgument<T>): T

    /**
     * Gets the value of an argument, or null if it was left out and has no
     * default value.
     *
     * @param argument the argument declaration to read.
     * @return the parsed value, null if the argument has no value.
     * @throws IllegalArgumentException if the argument is not part of the
     * executed syntax.
     */
    fun <T : Any> find(argument: CommandArgument<T>): T?

}
