package net.mythicisland.core.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

sealed interface CommandResult {

    data object Success : CommandResult

    data object Syntax : CommandResult

    data class Failure(val message: Component) : CommandResult

    companion object {
        fun failure(message: String): Failure =
            Failure(Component.text(message, NamedTextColor.RED))
    }
}
