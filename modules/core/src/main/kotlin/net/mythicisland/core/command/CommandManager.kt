package net.mythicisland.core.command

/**
 * Registers the commands of the server.
 */
interface CommandManager {

    /**
     * Gets the [CommandRegistry].
     *
     * @return the Command Registry.
     */
    fun getCommandRegistry(): CommandRegistry

    /**
     * Gets all registered commands.
     *
     * @return a list of all registered commands.
     */
    fun getRegisteredCommands(): List<Command>

    /**
     * Registers a command.
     *
     * @param command the Command to register.
     * @throws IllegalStateException if the name or one of the aliases is taken.
     * @throws IllegalArgumentException if one of the syntaxes is invalid.
     */
    fun register(command: Command)

    /**
     * Removes a command again. Does nothing if it was never registered.
     *
     * @param command the Command to unregister
     */
    fun unregister(command: Command)

}
