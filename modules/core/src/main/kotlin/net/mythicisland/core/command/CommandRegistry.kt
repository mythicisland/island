package net.mythicisland.core.command

/**
 * Holds every command the server knows.
 */
interface CommandRegistry {

    /**
     * Gets all registered commands.
     *
     * @return a list of all registered commands.
     */
    fun getCommands(): List<Command>

    /**
     * Gets a command by its name or one of its aliases, case-insensitive.
     *
     * @param name the name to look up.
     * @return the command, null if no command uses the name.
     */
    fun getCommand(name: String): Command?

    /**
     * Gets whether a name or alias is already taken.
     *
     * @param name the name to look up.
     * @return true if a command uses the name.
     */
    fun isRegistered(name: String): Boolean

}
