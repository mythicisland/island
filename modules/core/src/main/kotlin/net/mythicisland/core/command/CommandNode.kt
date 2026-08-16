package net.mythicisland.core.command

import net.minestom.server.command.builder.arguments.Argument
import net.mythicisland.core.command.argument.CommandArgument

/**
 * A single position within a syntax.
 */
internal sealed interface CommandElement {

    /**
     * The Minestom argument backing this position.
     */
    val argument: Argument<*>
}

/**
 * A fixed word, never exposed to the [CommandContext].
 */
internal class CommandLiteral(override val argument: Argument<*>) : CommandElement

/**
 * A declared argument, readable from the [CommandContext].
 */
internal class CommandParameter(val declaration: CommandArgument<*>) : CommandElement {

    override val argument: Argument<*>
        get() = declaration.argument
}

/**
 * One finished branch of a command, built by the [CommandBuilder].
 */
internal class CommandNode(
    val elements: List<CommandElement>,
    val permission: String?,
    val playerOnly: Boolean,
    val executor: (CommandContext) -> Unit,
) {

    /**
     * The arguments of this branch, in declaration order.
     */
    val parameters: List<CommandArgument<*>> = elements
        .filterIsInstance<CommandParameter>()
        .map { element -> element.declaration }
}
