package net.mythicisland.core.command

/**
 * Interface to registering commands.
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
     * Registers a command in the registry.
     *
     * The command is built once here, so a broken declaration fails on
     * registration instead of on the first execution.
     *
     * @param command the Command to register.
     * @throws IllegalStateException if the name or one of the aliases is taken.
     * @throws IllegalArgumentException if the command declares an invalid syntax.
     */
    fun register(command: Command)

    /**
     * Deletes a command from the registry.
     *
     * Does nothing if the command was not registered.
     *
     * @param command the Command to unregister
     */
    fun unregister(command: Command)

}
