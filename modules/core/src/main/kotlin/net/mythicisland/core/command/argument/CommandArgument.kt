package net.mythicisland.core.command.argument

import net.minestom.server.command.builder.arguments.Argument

/**
 * An argument together with the type it reads.
 */
class CommandArgument<T : Any> private constructor(
    val argument: Argument<T>,
    val optional: Boolean,
    val defaultValue: (() -> T)?,
    val literal: Boolean,
) {

    constructor(argument: Argument<T>) : this(argument, false, null, false)

    /**
     * The name of the argument.
     */
    val name: String
        get() = argument.id

    fun optional(): CommandArgument<T> =
        CommandArgument(argument, optional = true, defaultValue = null, literal = literal)

    fun optional(defaultValue: T): CommandArgument<T> =
        CommandArgument(argument, optional = true, defaultValue = { defaultValue }, literal = literal)

    fun <R : Any> map(mapper: (T) -> R): CommandArgument<R> {
        val mapped = argument.map { value -> mapper(value) }
        val default = defaultValue
            ?: return CommandArgument(mapped, optional, null, literal)
        return CommandArgument(mapped, optional, { mapper(default()) }, literal)
    }

    override fun toString(): String =
        "CommandArgument(name=$name, optional=$optional)"

    companion object {
        fun <T : Any> literal(argument: Argument<T>): CommandArgument<T> =
            CommandArgument(argument, optional = false, defaultValue = null, literal = true)
    }
}
