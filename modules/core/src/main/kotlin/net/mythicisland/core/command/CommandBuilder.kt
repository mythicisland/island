package net.mythicisland.core.command

import net.mythicisland.core.command.argument.CommandArgument

interface CommandBuilder {

    fun literal(vararg names: String, description: String? = null): CommandBuilder

    fun argument(argument: CommandArgument<*>): CommandBuilder

    fun playerOnly(): CommandBuilder

    fun executes(executor: CommandExecutor)

}
