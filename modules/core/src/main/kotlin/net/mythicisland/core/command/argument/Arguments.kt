package net.mythicisland.core.command.argument

import net.minestom.server.command.builder.arguments.ArgumentEnum
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player
import java.util.UUID

/**
 * The argument types a command can use.
 */
object Arguments {

    const val PLAYER_NOT_FOUND_ERROR: Int = 100

    /**
     * A single word without spaces.
     *
     * @param name the argument name.
     * @param allowed the words that are accepted. If empty, every word works.
     * @return the argument.
     */
    fun word(name: String, vararg allowed: String): CommandArgument<String> {
        val argument = ArgumentType.Word(name)
        if (allowed.isNotEmpty()) {
            argument.from(*allowed)
        }
        return CommandArgument(argument)
    }

    /**
     * A single word, or several words inside quotes.
     *
     * @param name the argument name.
     * @return the argument.
     */
    fun string(name: String): CommandArgument<String> =
        CommandArgument(ArgumentType.String(name))

    /**
     * A greedy string.
     *
     * @param name the argument name.
     * @return the argument.
     */
    fun greedyString(name: String): CommandArgument<String> =
        CommandArgument(ArgumentType.StringArray(name).map { words -> words.joinToString(" ") })

    /**
     * A whole number.
     *
     * @param name the argument name.
     * @param min the smallest value that is accepted.
     * @param max the biggest value that is accepted.
     * @return the argument.
     */
    fun integer(name: String, min: Int? = null, max: Int? = null): CommandArgument<Int> {
        val argument = ArgumentType.Integer(name)
        if (min != null) {
            argument.min(min)
        }
        if (max != null) {
            argument.max(max)
        }
        return CommandArgument(argument)
    }

    /**
     * A decimal number.
     *
     * @param name the argument name.
     * @param min the smallest value that is accepted.
     * @param max the biggest value that is accepted.
     * @return the argument.
     */
    fun double(name: String, min: Double? = null, max: Double? = null): CommandArgument<Double> {
        val argument = ArgumentType.Double(name)
        if (min != null) {
            argument.min(min)
        }
        if (max != null) {
            argument.max(max)
        }
        return CommandArgument(argument)
    }

    /**
     * `true` or `false`.
     *
     * @param name the argument name.
     * @return the argument.
     */
    fun boolean(name: String): CommandArgument<Boolean> =
        CommandArgument(ArgumentType.Boolean(name))

    /**
     * An uuid.
     *
     * @param name the argument name.
     * @return the argument.
     */
    fun uuid(name: String): CommandArgument<UUID> =
        CommandArgument(ArgumentType.UUID(name))

    /**
     * One value of an enum, written in lower case.
     *
     * @param name the argument name.
     * @return the argument.
     */
    inline fun <reified E : Enum<E>> enum(name: String): CommandArgument<E> {
        val argument = ArgumentEnum(name, E::class.java)
        argument.setFormat(ArgumentEnum.Format.LOWER_CASED)
        return CommandArgument(argument)
    }

    /**
     * One player who is online.
     *
     * @param name the argument name.
     * @return the argument.
     */
    fun player(name: String): CommandArgument<Player> {
        val argument = ArgumentType.Entity(name)
        argument.singleEntity(true)
        argument.onlyPlayers(true)

        val mapped = argument.map { sender, finder ->
            finder.findFirstPlayer(sender)
                ?: throw ArgumentSyntaxException("Player not found", name, PLAYER_NOT_FOUND_ERROR)
        }
        return CommandArgument(mapped)
    }

}