package net.mythicisland.core.command

import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.mythicisland.core.command.argument.CommandArgument

interface CommandContext {

    /**
     * Who ran the command.
     */
    val sender: CommandSender

    /**
     * The player who ran the command.
     *
     * @throws IllegalStateException if the sender is not a player.
     */
    val player: Player

    /**
     * What the sender typed.
     */
    val input: String

    /**
     * Reads an argument. If it was left out, its default value is used.
     *
     * @param argument the argument to read.
     * @return the value.
     * @throws IllegalArgumentException if the argument belongs to another
     * chain, or if it was left out and has no default value.
     */
    operator fun <T : Any> get(argument: CommandArgument<T>): T

    /**
     * Reads an argument that the sender may have left out.
     *
     * @param argument the argument to read.
     * @return the value, or null if it was left out and has no default value.
     * @throws IllegalArgumentException if the argument belongs to another
     * chain.
     */
    fun <T : Any> find(argument: CommandArgument<T>): T?

}
