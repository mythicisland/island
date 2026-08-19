package net.mythicisland.core.command

interface Command {

    /**
     * The name of the command.
     */
    val name: String

    /**
     * Aliases of the command.
     */
    val aliases: List<String>
        get() = emptyList()

    /**
     * Builds the command.
     *
     * @see CommandBuilder
     * @param command the command builder.
     */
    fun build(command: CommandBuilder)

}
