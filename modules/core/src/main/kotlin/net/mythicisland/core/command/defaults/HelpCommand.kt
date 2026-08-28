package net.mythicisland.core.command.defaults

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandBuilder
import net.mythicisland.core.command.CommandNode
import net.mythicisland.core.command.CommandRegistry
import net.mythicisland.core.command.CommandResult
import net.mythicisland.core.command.argument.Arguments

class HelpCommand(private val registry: CommandRegistry) : Command {

    override val name = "help"

    override val description = "Shows the commands of the server"

    override val aliases = listOf("?")

    override val examples = listOf("/help", "/help gamemode")

    private val target = Arguments.word("command").describe("The command to show the details of")

    override fun build(command: CommandBuilder) {
        command.executes { context ->
            context.sender.sendMessage(list())
            CommandResult.Success
        }

        command.argument(target).executes { context ->
            val commands = registry.getCommand(context[target])
                ?: return@executes CommandResult.failure("There is no command called '${context[target]}'.")

            context.sender.sendMessage(details(commands))
            CommandResult.Success
        }
    }

    /**
     * Builds the command list.
     */
    private fun list(): Component {
        val commands = registry.getCommands().filter { command -> command.category != null }
        if (commands.isEmpty()) {
            return Component.text("No commands are registered.", NamedTextColor.GRAY)
        }

        // The names are padded to the longest one, so the second column of every category starts at the same place.
        val width = commands.maxOf { command -> command.name.length } + 3
        val categories = commands
            .groupBy { command -> checkNotNull(command.category) }
            .entries
            .sortedWith(compareBy({ entry -> entry.key.order }, { entry -> entry.key.name }))

        val list = Component.text()
        for ((index, category) in categories.withIndex()) {
            if (index > 0) {
                list.append(Component.newline())
            }

            list.append(Component.text(category.key.name, NamedTextColor.GOLD, TextDecoration.BOLD))
            for (row in category.value.chunked(2)) {
                list.append(Component.newline()).append(row(row, width))
            }
        }
        return list.build()
    }

    private fun row(row: List<Command>, width: Int): Component {
        val line = Component.text()

        for ((column, command) in row.withIndex()) {
            line.append(entry(command))

            if (column < row.lastIndex) {
                val padding = width - command.name.length - 1
                line.append(Component.text(" ".repeat(maxOf(padding, 1))))
            }
        }
        return line.build()
    }

    private fun entry(command: Command): Component =
        Component.text("/${command.name}", NamedTextColor.AQUA)
            .hoverEvent(HoverEvent.showText(details(command)))
            .clickEvent(ClickEvent.suggestCommand("/${command.name}"))

    private fun details(command: Command): Component {
        val nodes = registry.getNodes(command)
        val details = Component.text()
            .append(Component.text(command.description, NamedTextColor.WHITE))

        usage(details, command, nodes)
        section(details, "ᴀʀɢᴜᴍᴇɴᴛѕ:", described(nodes, literal = false))
        section(details, "ѕᴜʙᴄᴏᴍᴍᴀɴᴅѕ:", described(nodes, literal = true))
        section(details, "ᴇxᴀᴍᴘʟᴇѕ:", command.examples.map { example -> example to null })

        return details.build()
    }

    private fun usage(
        details: TextComponent.Builder,
        command: Command,
        nodes: List<CommandNode>,
    ) {
        val usages = nodes.map { node -> node.usage(command.name) }
        if (usages.isEmpty()) {
            return
        }

        // A single way to type the command fits behind the label, several
        // ways get a line each.
        if (usages.size == 1) {
            details.append(Component.newline())
                .append(label("ᴜѕᴀɢᴇ: "))
                .append(Component.text(usages.first(), NamedTextColor.YELLOW))
            return
        }

        details.append(Component.newline()).append(label("ᴜѕᴀɢᴇ:"))
        for (usage in usages) {
            details.append(Component.newline())
                .append(Component.text(usage, NamedTextColor.YELLOW))
        }
    }

    private fun section(
        details: TextComponent.Builder,
        title: String,
        entries: List<Pair<String, String?>>,
    ) {
        if (entries.isEmpty()) {
            return
        }

        details.append(Component.newline()).append(label(title))
        for ((name, description) in entries) {
            details.append(Component.newline())
                .append(Component.text(name, NamedTextColor.YELLOW))

            if (description != null) {
                details.append(Component.text(": ", NamedTextColor.GRAY))
                    .append(Component.text(description, NamedTextColor.GRAY))
            }
        }
    }

    private fun described(nodes: List<CommandNode>, literal: Boolean): List<Pair<String, String?>> =
        nodes.asSequence()
            .flatMap { node -> node.elements }
            .filter { element -> element.literal == literal && element.description != null }
            .distinctBy { element -> element.name }
            .map { element -> element.name to element.description }
            .toList()

    private fun label(text: String): Component =
        Component.text(text, NamedTextColor.GOLD)
}
