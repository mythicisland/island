package net.mythicisland.core.command

interface CommandRegistry {

    /**
     * Gets all registered commands.
     *
     * @return a list of all registered commands.
     */
    fun getCommands(): List<Command>

    /**
     * Gets a command by its name or one of its aliases.
     *
     * @param name the name to look up.
     * @return the command, or null if no command uses this name.
     */
    fun getCommand(name: String): Command?

    /**
     * Gets all nodes from a command.
     *
     * @param command the command to look up.
     * @return the nodes of the command, empty if it is not registered.
     */
    fun getNodes(command: Command): List<CommandNode>

    /**
     * Checks if a command is registered.
     *
     * @param name the name to look up.
     * @return true if a command uses this name.
     */
    fun isRegistered(name: String): Boolean

}
