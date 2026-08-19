package net.mythicisland.core.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.condition.CommandCondition
import net.minestom.server.entity.Player
import net.mythicisland.core.command.argument.CommandArgument
import net.mythicisland.core.command.impl.CommandBuilderImpl
import net.mythicisland.core.command.impl.CommandContextImpl
import net.minestom.server.command.builder.Command as MinestomCommand
import net.minestom.server.command.builder.CommandExecutor as MinestomExecutor

class CommandFactory {

    /**
     * Builds a command.
     *
     * @param command the command to build.
     * @return the Minestom command to register.
     * @throws IllegalArgumentException if the command is built in a way that cannot work.
     */
    fun create(command: Command): MinestomCommand =
        create(command, nodesOf(command))

    /**
     * Collects the ways a command can be typed.
     *
     * @param command the command to build.
     * @return the nodes of the command.
     */
    fun nodesOf(command: Command): List<CommandNode> {
        val nodes = mutableListOf<CommandNode>()
        command.build(CommandBuilderImpl(nodes))
        return nodes
    }

    /**
     * Builds a command from nodes that were already collected.
     *
     * @param command the command to build.
     * @param nodes the nodes of the command.
     * @return the Minestom command to register.
     * @throws IllegalArgumentException if the command is built in a way that cannot work.
     */
    fun create(command: Command, nodes: List<CommandNode>): MinestomCommand {
        val result = MinestomCommand(command.name, *command.aliases.toTypedArray())

        var defaultAssigned = false
        for (node in nodes) {
            val executor = executorOf(command, node)
            val condition = conditionOf(node)

            for (elements in expand(node.elements)) {
                if (elements.isNotEmpty()) {
                    val arguments = elements.map { element -> element.argument }.toTypedArray()
                    result.addConditionalSyntax(condition, executor, *arguments)
                    continue
                }

                require(!defaultAssigned) {
                    "Command '${command.name}' has more than one executor without arguments."
                }
                defaultAssigned = true

                // Minestom runs the default executor without asking the
                // condition, so it is checked here instead.
                result.defaultExecutor = MinestomExecutor { sender, context ->
                    if (condition.canUse(sender, context.input)) {
                        executor.apply(sender, context)
                    }
                }
            }
        }
        return result
    }

    private fun executorOf(command: Command, node: CommandNode): MinestomExecutor =
        MinestomExecutor { sender, context ->
            val result = node.executor.execute(CommandContextImpl(sender, context, node.elements))

            report(sender, command, node, result)
        }

    private fun report(
        sender: CommandSender,
        command: Command,
        node: CommandNode,
        result: CommandResult,
    ) {
        when (result) {
            is CommandResult.Success -> Unit
            is CommandResult.Failure -> sender.sendMessage(result.message)
            is CommandResult.Syntax -> sender.sendMessage(
                Component.text("Use it like this: ${node.usage(command.name)}", NamedTextColor.RED),
            )
        }
    }

    private fun conditionOf(node: CommandNode): CommandCondition =
        CommandCondition { sender, input ->
            allows(sender, input, node.playerOnly)
        }

    private fun expand(elements: List<CommandArgument<*>>): List<List<CommandArgument<*>>> {
        val optional = elements.indexOfFirst { element -> element.optional }
        if (optional < 0) {
            return listOf(elements)
        }
        return (optional..elements.size).map { size -> elements.take(size) }
    }

    private fun allows(sender: CommandSender, input: String?, playerOnly: Boolean): Boolean {
        if (playerOnly && sender !is Player) {
            if (input != null) {
                sender.sendMessage(PLAYER_ONLY_MESSAGE)
            }
            return false
        }
        return true
    }

    companion object {
        private val PLAYER_ONLY_MESSAGE: Component = Component.text(
            "This command can only be used by players.",
            NamedTextColor.RED,
        )
    }
}
