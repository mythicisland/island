package net.mythicisland.core.command

import net.minestom.server.entity.Player
import net.mythicisland.core.command.argument.CommandArgument

/**
 * Writes one syntax of a [Command].
 *
 * Every method returns a new builder and leaves the old one alone, so the
 * syntaxes of a command are written flat next to each other instead of nested:
 *
 * ```
 * command.literal("visit")
 *     .argument(target)
 *     .executesPlayer { player, context -> visit(player, context[target]) }
 *
 * command.literal("border")
 *     .argument(size)
 *     .permission("island.border")
 *     .executesPlayer { player, context -> resize(player, context[size]) }
 * ```
 *
 * The syntax is registered once one of the execute methods is called.
 */
interface CommandBuilder {

    /**
     * Adds a word, like the `do` in `/player do <something>`.
     *
     * With several names all of them are accepted, the client shows each of
     * them separately.
     *
     * @param names the accepted words, at least one.
     * @return a new builder with the word added.
     */
    fun literal(vararg names: String): CommandBuilder

    /**
     * Adds an argument. Optional ones have to come last.
     *
     * @param argument the argument to add.
     * @return a new builder with the argument added.
     */
    fun argument(argument: CommandArgument<*>): CommandBuilder

    /**
     * Asks for a permission on top of [Command.permission].
     *
     * Without it a sender neither sees the syntax in the autocompletion nor
     * can they run it.
     *
     * @param permission the needed permission.
     * @return a new builder asking for the permission.
     */
    fun permission(permission: String): CommandBuilder

    /**
     * Registers the syntax with code that any sender may run.
     *
     * @param executor runs once the syntax was parsed.
     */
    fun executes(executor: (CommandContext) -> Unit)

    /**
     * Registers the syntax with code that only players may run.
     *
     * The console neither sees the syntax nor can it run it.
     *
     * @param executor runs once the syntax was parsed.
     */
    fun executesPlayer(executor: (Player, CommandContext) -> Unit)

}
