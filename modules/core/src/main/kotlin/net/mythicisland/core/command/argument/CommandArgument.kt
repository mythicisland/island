package net.mythicisland.core.command.argument

import net.minestom.server.command.builder.arguments.Argument

/**
 * A typed argument declaration.
 *
 * An argument is declared once, usually as a field of the command, and is used
 * as the key to read the parsed value back from the [net.mythicisland.core.command.CommandContext]. That
 * removes the string lookups of the plain Minestom api:
 *
 * ```
 * private val size = Arguments.integer("size", min = 1, max = 32)
 *
 * // inside an executor
 * val blocks: Int = context[size]
 * ```
 *
 * Instances are immutable, [optional] and [map] return new declarations.
 *
 * @param T the type this argument parses into.
 */
class CommandArgument<T : Any> private constructor(
    internal val argument: Argument<T>,
    internal val optional: Boolean,
    internal val defaultValue: (() -> T)?,
) {

    /**
     * Wraps a Minestom argument. Used by [Arguments] and as the escape hatch
     * for argument types this library does not provide out of the box.
     *
     * @param argument the Minestom argument to wrap.
     */
    constructor(argument: Argument<T>) : this(argument, false, null)

    /**
     * The name of the argument, shown to the client above the chat bar.
     */
    val name: String
        get() = argument.id

    /**
     * Marks the argument as optional without a default value.
     *
     * Optional arguments have to be the last ones of a syntax. Reading them
     * with [net.mythicisland.core.command.CommandContext.get] throws when the sender left them out, use
     * [net.mythicisland.core.command.CommandContext.find] instead.
     *
     * @return a new optional declaration.
     */
    fun optional(): CommandArgument<T> =
        CommandArgument(argument, optional = true, defaultValue = null)

    /**
     * Marks the argument as optional with a default value.
     *
     * The default is applied when the sender left the argument out, so
     * [net.mythicisland.core.command.CommandContext.get] always returns a value.
     *
     * @param defaultValue the value used when the argument is missing.
     * @return a new optional declaration.
     */
    fun optional(defaultValue: T): CommandArgument<T> =
        CommandArgument(argument, optional = true, defaultValue = { defaultValue })

    /**
     * Converts the parsed value into another type, for example to resolve a
     * player name into a domain object.
     *
     * The mapper runs during parsing and must not return null. Note that
     * mapping an argument with a restricted set of values (see
     * [Arguments.word] and [Arguments.enum]) makes the client render it as a
     * free text argument instead of literals.
     *
     * @param mapper converts the parsed value.
     * @return a new declaration producing the mapped type.
     */
    fun <R : Any> map(mapper: (T) -> R): CommandArgument<R> {
        val mapped = argument.map { value -> mapper(value) }
        val default = defaultValue ?: return CommandArgument(mapped, optional, null)
        return CommandArgument(mapped, optional) { mapper(default()) }
    }

    override fun toString(): String =
        "CommandArgument(name=$name, optional=$optional)"
}