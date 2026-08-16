package net.mythicisland.core.command.impl

import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import net.mythicisland.core.command.argument.CommandArgument
import net.mythicisland.core.command.CommandBuilder
import net.mythicisland.core.command.CommandContext
import net.mythicisland.core.command.CommandElement
import net.mythicisland.core.command.CommandLiteral
import net.mythicisland.core.command.CommandNode
import net.mythicisland.core.command.CommandParameter
import kotlin.collections.plus

/**
 * Default [net.mythicisland.core.command.CommandBuilder], collecting the finished branches of one command.
 *
 * Every builder method returns a new instance sharing the same [nodes] list,
 * which keeps the declared branches independent of each other.
 */
internal class CommandBuilderImpl private constructor(
    private val nodes: MutableList<CommandNode>,
    private val elements: List<CommandElement>,
    private val permission: String?,
) : CommandBuilder {

    constructor(nodes: MutableList<CommandNode>) : this(nodes, emptyList(), null)

    override fun literal(vararg names: String): CommandBuilder {
        require(names.isNotEmpty()) { "A literal needs at least one name." }

        // A single name maps to a real literal, multiple names to a word
        // restricted to them, which the client also renders as literals.
        val argument = if (names.size == 1) {
            ArgumentType.Literal(names[0])
        } else {
            ArgumentType.Word(names[0]).from(*names)
        }

        return CommandBuilderImpl(nodes, elements + CommandLiteral(argument), permission)
    }

    override fun argument(argument: CommandArgument<*>): CommandBuilder =
        CommandBuilderImpl(nodes, elements + CommandParameter(argument), permission)

    override fun permission(permission: String): CommandBuilder =
        CommandBuilderImpl(nodes, elements, permission)

    override fun executes(executor: (CommandContext) -> Unit) {
        add(playerOnly = false, executor = executor)
    }

    override fun executesPlayer(executor: (Player, CommandContext) -> Unit) {
        // The sender type is guaranteed by the condition of the syntax.
        add(playerOnly = true) { context -> executor(context.sender as Player, context) }
    }

    private fun add(playerOnly: Boolean, executor: (CommandContext) -> Unit) {
        val node = CommandNode(elements, permission, playerOnly, executor)
        validate(node)
        nodes.add(node)
    }

    private fun validate(node: CommandNode) {
        val names = mutableSetOf<String>()
        for (parameter in node.parameters) {
            require(names.add(parameter.name)) {
                "Argument '${parameter.name}' is used more than once in the same syntax."
            }
        }

        var optionalSeen = false
        for (element in node.elements) {
            val optional = element is CommandParameter && element.declaration.optional
            if (optionalSeen) {
                require(optional) {
                    "Optional arguments have to be the last ones of a syntax, " +
                        "'${element.argument.id}' follows an optional argument."
                }
            }
            optionalSeen = optionalSeen || optional
        }
    }
}