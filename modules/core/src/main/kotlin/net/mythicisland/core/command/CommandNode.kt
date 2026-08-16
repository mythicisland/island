package net.mythicisland.core.command

import net.mythicisland.core.command.argument.CommandArgument

/**
 * One syntax of a command, for example `/player do <something>`.
 *
 * Literals like `do` sit in [elements] too, they are just arguments that
 * accept a single fixed word.
 */
class CommandNode(
    val elements: List<CommandArgument<*>>,
    val permission: String?,
    val playerOnly: Boolean,
    val executor: (CommandContext) -> Unit,
)
