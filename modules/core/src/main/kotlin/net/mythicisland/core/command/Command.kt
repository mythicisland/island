package net.mythicisland.core.command

interface Command {

    /**
     * The name of the command.
     */
    val name: String

    /**
     * The description of the command.
     */
    val description: String

    /**
     * Aliases of the command.
     */
    val aliases: List<String>
        get() = emptyList()

    /**
     * The category the command is listed under in the help command.
     *
     * @see CommandCategory.HIDDEN
     */
    val category: CommandCategory?
        get() = CommandCategory.DEFAULT

    /**
     * Examples of the command, shown in the help command.
     */
    val examples: List<String>
        get() = emptyList()

    /**
     * Builds the command.
     *
     * @see CommandBuilder
     * @param command the command builder.
     */
    fun build(command: CommandBuilder)

}
