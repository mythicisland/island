package net.mythicisland.core.command

fun interface CommandExecutor {

    /**
     * Runs the command.
     *
     * @param context the values the sender typed.
     * @return what happened, see [CommandResult].
     */
    fun execute(context: CommandContext): CommandResult

}
