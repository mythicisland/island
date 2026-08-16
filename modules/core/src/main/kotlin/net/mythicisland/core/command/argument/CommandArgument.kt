package net.mythicisland.core.command.argument

import net.minestom.server.command.builder.arguments.Argument

/**
 * An argument together with the type it parses into.
 *
 * Write it down once as a field of the command and use it to read the value
 * back, instead of looking it up by name like the plain Minestom api does:
 *
 * ```
 * private val size = Arguments.integer("size", min = 1, max = 32)
 *
 * // inside an executor
 * val blocks: Int = context[size]
 * ```
 *
 * An argument never changes, [optional] and [map] return a new one.
 *
 * @param T the type this argument parses into.
 */
class CommandArgument<T : Any> private constructor(
    val argument: Argument<T>,
    val optional: Boolean,
    val defaultValue: (() -> T)?,
) {

    /**
     * Wraps a Minestom argument, for types [Arguments] does not offer.
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
     * Lets the sender leave the argument out. It has to be the last one of the
     * syntax.
     *
     * Read it with `find`, `get` throws when the sender left it out.
     *
     * @return a copy that may be left out.
     */
    fun optional(): CommandArgument<T> =
        CommandArgument(argument, optional = true, defaultValue = null)

    /**
     * Lets the sender leave the argument out and uses [defaultValue] instead.
     * It has to be the last one of the syntax.
     *
     * @param defaultValue the value to use when the argument is missing.
     * @return a copy that may be left out.
     */
    fun optional(defaultValue: T): CommandArgument<T> =
        CommandArgument(argument, optional = true, defaultValue = { defaultValue })

    /**
     * Parses the argument, then converts the value, for example a player into
     * their island.
     *
     * The converter runs while parsing and must not return null.
     *
     * Do not use it on [Arguments.word] or [Arguments.enum], the client then
     * shows a free text field instead of the words it accepts.
     *
     * @param mapper converts the parsed value.
     * @return a copy that gives back the converted type.
     */
    fun <R : Any> map(mapper: (T) -> R): CommandArgument<R> {
        val mapped = argument.map { value -> mapper(value) }
        val default = defaultValue ?: return CommandArgument(mapped, optional, null)
        return CommandArgument(mapped, optional) { mapper(default()) }
    }

    override fun toString(): String =
        "CommandArgument(name=$name, optional=$optional)"
}
