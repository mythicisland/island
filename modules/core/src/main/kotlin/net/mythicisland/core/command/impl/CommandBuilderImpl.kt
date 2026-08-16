package net.mythicisland.core.command.impl

import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import net.mythicisland.core.command.CommandBuilder
import net.mythicisland.core.command.CommandContext
import net.mythicisland.core.command.CommandNode
import net.mythicisland.core.command.argument.CommandArgument

/**
 * Default [CommandBuilder].
 *
 * Every method returns a new builder that shares the same [nodes] list, which
 * is why the syntaxes of a command cannot get in each other's way.
 */
class CommandBuilderImpl private constructor(
    private val nodes: MutableList<CommandNode>,
    private val elements: List<CommandArgument<*>>,
    private val permission: String?,
) : CommandBuilder {

    constructor(nodes: MutableList<CommandNode>) : this(nodes, emptyList(), null)

    override fun literal(vararg names: String): CommandBuilder {
        require(names.isNotEmpty()) { "A literal needs at least one name." }

        // One name becomes a real literal, several names become a word that
        // only accepts them, which the client also draws as literals.
        val argument = if (names.size == 1) {
            ArgumentType.Literal(names[0])
        } else {
            ArgumentType.Word(names[0]).from(*names)
        }

        return CommandBuilderImpl(nodes, elements + CommandArgument(argument), permission)
    }

    override fun argument(argument: CommandArgument<*>): CommandBuilder =
        CommandBuilderImpl(nodes, elements + argument, permission)

    override fun permission(permission: String): CommandBuilder =
        CommandBuilderImpl(nodes, elements, permission)

    override fun executes(executor: (CommandContext) -> Unit) {
        add(playerOnly = false, executor = executor)
    }

    override fun executesPlayer(executor: (Player, CommandContext) -> Unit) {
        // Only players get here, the condition of the syntax makes sure of it.
        add(playerOnly = true) { context -> executor(context.sender as Player, context) }
    }

    private fun add(playerOnly: Boolean, executor: (CommandContext) -> Unit) {
        val node = CommandNode(elements, permission, playerOnly, executor)
        validate(node)
        nodes.add(node)
    }

    /**
     * Stops syntaxes that would not work the way they read.
     *
     * Names have to be unique because Minestom keeps the parsed values under
     * them, literals included. Optional arguments have to come last because
     * the syntax is split into one Minestom syntax per argument count.
     */
    private fun validate(node: CommandNode) {
        val names = mutableSetOf<String>()
        var optionalSeen = false

        for (element in node.elements) {
            require(names.add(element.name)) {
                "Argument '${element.name}' is used more than once in the same syntax."
            }

            if (optionalSeen) {
                require(element.optional) {
                    "Optional arguments have to come last, " +
                        "'${element.name}' stands behind an optional one."
                }
            }
            optionalSeen = optionalSeen || element.optional
        }
    }
}
