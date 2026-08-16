package net.mythicisland.core.command

/**
 * A command with all the syntaxes it accepts.
 *
 * The syntaxes are written in [build], one chain per syntax:
 *
 * ```
 * class IslandCommand : Command {
 *
 *     private val target = Arguments.player("target")
 *
 *     override val name: String = "island"
 *     override val aliases: List<String> = listOf("is")
 *
 *     override fun build(command: CommandBuilder) {
 *         command.literal("visit")
 *             .argument(target)
 *             .executesPlayer { player, context -> visit(player, context[target]) }
 *     }
 * }
 * ```
 *
 * The [CommandManager] turns this into a Minestom command, so a command never
 * talks to Minestom itself.
 */
interface Command {

    /**
     * The name of the command, without the leading slash.
     */
    val name: String

    /**
     * Other names the command also listens to.
     */
    val aliases: List<String>
        get() = emptyList()

    /**
     * The permission needed for the whole command, null if everyone may use
     * it. Single syntaxes can ask for more with [CommandBuilder.permission].
     */
    val permission: String?
        get() = null

    /**
     * Writes the syntaxes of this command. Called once while registering.
     *
     * @param command an empty chain to start from. Every call on it returns a
     * new chain, so the syntaxes cannot get in each other's way.
     */
    fun build(command: CommandBuilder)

}
