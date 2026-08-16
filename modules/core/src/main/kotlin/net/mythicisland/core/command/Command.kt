package net.mythicisland.core.command

/**
 * A command definition.
 *
 * A command describes its name, its aliases and every syntax it accepts. The
 * syntaxes are declared in [build] as flat chains, one chain per executable
 * branch:
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
 * Implementations are turned into a Minestom command by the [CommandManager],
 * they never talk to Minestom themselves.
 */
interface Command {

    /**
     * The primary name of the command, without the leading slash.
     */
    val name: String

    /**
     * The alternative names of the command.
     */
    val aliases: List<String>
        get() = emptyList()

    /**
     * The permission required to use any syntax of this command, null if the
     * command is available to everyone. Single branches can require additional
     * permissions through [CommandBuilder.permission].
     */
    val permission: String?
        get() = null

    /**
     * Declares every syntax of this command.
     *
     * Called once while the command is registered. The given builder is the
     * empty root chain, every call on it returns a new chain, so multiple
     * branches can be declared next to each other without interfering.
     *
     * @param command the root chain of this command.
     */
    fun build(command: CommandBuilder)

}
