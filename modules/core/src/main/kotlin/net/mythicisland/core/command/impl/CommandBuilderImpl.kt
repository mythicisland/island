package net.mythicisland.core.command.impl

import net.minestom.server.command.builder.arguments.ArgumentType
import net.mythicisland.core.command.CommandBuilder
import net.mythicisland.core.command.CommandExecutor
import net.mythicisland.core.command.CommandNode
import net.mythicisland.core.command.argument.CommandArgument

class CommandBuilderImpl private constructor(
    private val nodes: MutableList<CommandNode>,
    private val elements: List<CommandArgument<*>>,
    private val playerOnly: Boolean,
) : CommandBuilder {

    constructor(nodes: MutableList<CommandNode>) : this(nodes, emptyList(), false)

    override fun literal(vararg names: String, description: String?): CommandBuilder {
        require(names.isNotEmpty()) { "A literal needs at least one name." }

        // One name becomes a real literal. Several names become a word that
        // only accepts those, which the client also draws as literals.
        val argument = if (names.size == 1) {
            ArgumentType.Literal(names[0])
        } else {
            ArgumentType.Word(names[0]).from(*names)
        }

        val literal = CommandArgument.literal(argument, description)
        return CommandBuilderImpl(nodes, elements + literal, playerOnly)
    }

    override fun argument(argument: CommandArgument<*>): CommandBuilder =
        CommandBuilderImpl(nodes, elements + argument, playerOnly)

    override fun playerOnly(): CommandBuilder =
        CommandBuilderImpl(nodes, elements, playerOnly = true)

    override fun executes(executor: CommandExecutor) {
        val node = CommandNode(elements, playerOnly, executor)
        validate(node)
        nodes.add(node)
    }

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
