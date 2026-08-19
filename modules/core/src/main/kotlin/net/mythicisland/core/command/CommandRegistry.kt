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
     * Checks if a command is registered.
     *
     * @param name the name to look up.
     * @return true if a command uses this name.
     */
    fun isRegistered(name: String): Boolean

}
