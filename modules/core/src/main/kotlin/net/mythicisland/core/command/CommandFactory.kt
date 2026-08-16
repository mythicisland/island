package net.mythicisland.core.command

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.condition.CommandCondition
import net.minestom.server.entity.Player
import net.mythicisland.core.command.impl.CommandBuilderImpl
import net.mythicisland.core.command.impl.CommandContextImpl
import net.minestom.server.command.builder.Command as MinestomCommand

/**
 * Turns a [Command] into the Minestom command it is registered as.
 *
 * Branches with optional arguments are expanded into one Minestom syntax per
 * accepted argument count, the default values are applied by the
 * [net.mythicisland.core.command.impl.CommandContextImpl] afterwards.
 */
internal class CommandFactory(
    private val permissionHandler: CommandPermissionHandler,
    private val noPermissionMessage: Component,
    private val playerOnlyMessage: Component,
) {

    /**
     * Builds the given command.
     *
     * @param command the command to build.
     * @return the Minestom command to register.
     * @throws IllegalArgumentException if the command declares an invalid syntax.
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
                    "Command '${command.name}' declares more than one executor without arguments."
                }
                defaultAssigned = true

                // The default executor is not covered by a condition, so the
                // branch is guarded here instead.
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
            node.executor(CommandContextImpl(sender, context, node.parameters))
        }

    private fun conditionOf(node: CommandNode): CommandCondition =
        CommandCondition { sender, input ->
            allows(sender, input, node.permission, node.playerOnly)
        }

    /**
     * Expands a branch into the argument lists Minestom has to know about, one
     * per accepted argument count.
     */
    private fun expand(elements: List<CommandElement>): List<List<CommandElement>> {
        val optional = elements.indexOfFirst { element ->
            element is CommandParameter && element.declaration.optional
        }
        if (optional < 0) {
            return listOf(elements)
        }
        return (optional..elements.size).map { size -> elements.take(size) }
    }

    /**
     * Checks whether a sender may use a branch.
     *
     * @param input the executed command string, null while the command graph
     * is built for a player. No feedback is sent in that case.
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

        if (permission == null || permissionHandler.hasPermission(sender, permission)) {
            return true
        }

        if (input != null) {
            sender.sendMessage(noPermissionMessage)
        }
        return false
    }
}
