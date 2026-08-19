package net.mythicisland.core.command

import net.mythicisland.core.command.argument.CommandArgument

class CommandNode(
    val elements: List<CommandArgument<*>>,
    val playerOnly: Boolean,
    val executor: CommandExecutor,
) {

    fun usage(name: String): String {
        val usage = StringBuilder("/").append(name)

        for (element in elements) {
            usage.append(' ')
            when {
                element.literal -> usage.append(element.name)
                element.optional -> usage.append('[').append(element.name).append(']')
                else -> usage.append('<').append(element.name).append('>')
            }
        }
        return usage.toString()
    }
}
