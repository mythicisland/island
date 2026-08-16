package net.mythicisland.core.command

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.condition.CommandCondition
import net.minestom.server.entity.Player
import net.mythicisland.core.command.argument.CommandArgument
import net.mythicisland.core.command.impl.CommandBuilderImpl
import net.mythicisland.core.command.impl.CommandContextImpl
import net.minestom.server.command.builder.Command as MinestomCommand

/**
 * Turns a [Command] into the Minestom command it is registered as.
 */
class CommandFactory(
    private val permissions: (CommandSender, String) -> Boolean,
    private val noPermissionMessage: Component,
    private val playerOnlyMessage: Component,
) {

    /**
     * Builds a command.
     *
     * @param command the command to build.
     * @return the Minestom command to register.
     * @throws IllegalArgumentException if one of the syntaxes is invalid.
     */
    fun create(command: Command): MinestomCommand {
        val nodes = mutableListOf<CommandNode>()
        command.build(CommandBuilderImpl(nodes))

        val result = MinestomCommand(command.name, *command.aliases.toTypedArray())
        val permission = command.permission
        if (permission != null) {
            result.condition = CommandCondition { sender, input ->
                allows(sender, input, permission, playerOnly = false)
            }
        }

        var defaultAssigned = false
        for (node in nodes) {
            val executor = executorOf(node)
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

                // Minestom runs the default executor without checking a
                // condition, so the check happens here.
                result.defaultExecutor = CommandExecutor { sender, context ->
                    if (condition.canUse(sender, context.input)) {
                        executor.apply(sender, context)
                    }
                }
            }
        }
        return result
    }

    private fun executorOf(node: CommandNode): CommandExecutor =
        CommandExecutor { sender, context ->
            node.executor(CommandContextImpl(sender, context, node.elements))
        }

    private fun conditionOf(node: CommandNode): CommandCondition =
        CommandCondition { sender, input ->
            allows(sender, input, node.permission, node.playerOnly)
        }

    /**
     * Splits a syntax into the argument lists Minestom needs, one per argument
     * count the sender may type.
     */
    private fun expand(elements: List<CommandArgument<*>>): List<List<CommandArgument<*>>> {
        val optional = elements.indexOfFirst { element -> element.optional }
        if (optional < 0) {
            return listOf(elements)
        }
        return (optional..elements.size).map { size -> elements.take(size) }
    }

    /**
     * Checks whether a sender may use a syntax.
     *
     * @param input what the sender typed, null while the command list is built
     * for a player. Nothing is sent back in that case.
     */
    private fun allows(
        sender: CommandSender,
        input: String?,
        permission: String?,
        playerOnly: Boolean,
    ): Boolean {
        if (playerOnly && sender !is Player) {
            if (input != null) {
                sender.sendMessage(playerOnlyMessage)
            }
            return false
        }

        if (permission == null || permissions(sender, permission)) {
            return true
        }

        if (input != null) {
            sender.sendMessage(noPermissionMessage)
        }
        return false
    }
}
