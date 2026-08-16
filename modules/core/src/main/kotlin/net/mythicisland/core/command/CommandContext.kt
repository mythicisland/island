package net.mythicisland.core.command

import net.minestom.server.command.CommandSender
import net.mythicisland.core.command.argument.CommandArgument

/**
 * The parsed arguments of a single run of a command.
 *
 * An argument is read with the argument itself, not with its name, so the
 * value comes back with the right type:
 *
 * ```
 * val target: Player = context[targetArgument]
 * ```
 */
interface CommandContext {

    /**
     * Who ran the command.
     */
    val sender: CommandSender

    /**
     * What the sender typed, without the leading slash.
     */
    val input: String

    /**
     * Reads an argument. Optional arguments fall back to their default value.
     *
     * @param argument the argument to read.
     * @return the parsed value.
     * @throws IllegalArgumentException if the argument belongs to another
     * syntax, or if the sender left it out, and it has no default value.
     */
    operator fun <T : Any> get(argument: CommandArgument<T>): T

    /**
     * Reads an argument the sender may have left out.
     *
     * @param argument the argument to read.
     * @return the parsed value, or null if the sender left the argument out,
     * and it has no default value.
     * @throws IllegalArgumentException if the argument belongs to another
     * syntax.
     */
    fun <T : Any> find(argument: CommandArgument<T>): T?

}
