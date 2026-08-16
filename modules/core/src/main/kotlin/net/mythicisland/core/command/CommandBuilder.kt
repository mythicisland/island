package net.mythicisland.core.command

import net.minestom.server.entity.Player
import net.mythicisland.core.command.argument.CommandArgument

/**
 * Builds one syntax of a [Command].
 *
 * Every method returns a new builder, the chain it was called on stays
 * untouched. That way all branches of a command are declared as flat chains
 * next to each other instead of nested blocks:
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
 * A chain is only registered once one of the execute methods is called.
 */
interface CommandBuilder {

    /**
     * Appends a fixed word to the syntax.
     *
     * Passing multiple names makes all of them valid at this position, the
     * client shows them as separate branches.
     *
     * @param names the accepted words, at least one.
     * @return a new builder containing the literal.
     */
    fun literal(vararg names: String): CommandBuilder

    /**
     * Appends an argument to the syntax.
     *
     * Optional arguments have to be appended last, see
     * [net.mythicisland.core.command.argument.CommandArgument.optional].
     *
     * @param argument the argument declaration.
     * @return a new builder containing the argument.
     */
    fun argument(argument: CommandArgument<*>): CommandBuilder

    /**
     * Requires a permission for this syntax, on top of [Command.permission].
     *
     * Senders without the permission neither see the syntax in their
     * auto completion nor can they execute it.
     *
     * @param permission the required permission.
     * @return a new builder requiring the permission.
     */
    fun permission(permission: String): CommandBuilder

    /**
     * Registers this chain with an executor accepting every sender.
     *
     * @param executor called once the syntax was parsed successfully.
     */
    fun executes(executor: (CommandContext) -> Unit)

    /**
     * Registers this chain with an executor only accepting players.
     *
     * The console neither sees the syntax nor can it execute it.
     *
     * @param executor called once the syntax was parsed successfully.
     */
    fun executesPlayer(executor: (Player, CommandContext) -> Unit)

}
