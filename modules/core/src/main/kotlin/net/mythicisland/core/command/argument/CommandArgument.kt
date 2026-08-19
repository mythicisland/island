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
    val description: String?,
) {

    constructor(argument: Argument<T>) : this(argument, false, null, false, null)

    /**
     * The name of the argument.
     */
    val name: String
        get() = argument.id

    fun optional(): CommandArgument<T> =
        CommandArgument(argument, true, null, literal, description)

    fun optional(defaultValue: T): CommandArgument<T> =
        CommandArgument(argument, true, { defaultValue }, literal, description)

    /**
     * Adds a description, shown as an argument in the help command.
     *
     * @param description the description to add.
     * @return a copy carrying the description.
     */
    fun describe(description: String): CommandArgument<T> =
        CommandArgument(argument, optional, defaultValue, literal, description)

    fun <R : Any> map(mapper: (T) -> R): CommandArgument<R> {
        val mapped = argument.map { value -> mapper(value) }
        val default = defaultValue
            ?: return CommandArgument(mapped, optional, null, literal, description)
        return CommandArgument(mapped, optional, { mapper(default()) }, literal, description)
    }

    override fun toString(): String =
        "CommandArgument(name=$name, optional=$optional)"

    companion object {
        fun <T : Any> literal(argument: Argument<T>, description: String?): CommandArgument<T> =
            CommandArgument(argument, false, null, true, description)
    }
}
