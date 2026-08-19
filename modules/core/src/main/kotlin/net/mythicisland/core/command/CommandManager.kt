package net.mythicisland.core.command

interface CommandManager {

    /**
     * Gets the command registry.
     *
     * @return the command registry.
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
     * @param command the command to register.
     * @throws IllegalStateException if the name or one of the aliases is already used.
     * @throws IllegalArgumentException if the command is built in a way that cannot work.
     */
    fun register(command: Command)

    /**
     * Unregisters a command.
     *
     * @param command the command to unregister.
     */
    fun unregister(command: Command)

}
