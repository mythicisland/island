package net.mythicisland.core.command.argument

import net.minestom.server.command.builder.arguments.ArgumentEnum
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player
import java.util.UUID

/**
 * Creates the built in argument declarations.
 *
 * Every argument keeps the Minestom argument type underneath, so the client
 * still gets the correct syntax highlighting and completions. Types that are
 * missing here can be added by wrapping a Minestom argument with
 * [CommandArgument].
 */
object Arguments {

    /**
     * Error code reported when a player argument matched no online player.
     */
    const val PLAYER_NOT_FOUND_ERROR: Int = 100

    /**
     * A single word without spaces.
     *
     * @param name the argument name.
     * @param allowed the accepted words, all words are accepted when empty.
     * @return the argument declaration.
     */
    fun word(name: String, vararg allowed: String): CommandArgument<String> {
        val argument = ArgumentType.Word(name)
        if (allowed.isNotEmpty()) {
            argument.from(*allowed)
        }
        return CommandArgument(argument)
    }

    /**
     * A word, or multiple words when quoted.
     *
     * @param name the argument name.
     * @return the argument declaration.
     */
    fun string(name: String): CommandArgument<String> =
        CommandArgument(ArgumentType.String(name))

    /**
     * All the remaining input of the command, spaces included. Has to be the
     * last argument of a syntax.
     *
     * @param name the argument name.
     * @return the argument declaration.
     */
    fun greedyString(name: String): CommandArgument<String> =
        CommandArgument(ArgumentType.StringArray(name).map { words -> words.joinToString(" ") })

    /**
     * A whole number.
     *
     * @param name the argument name.
     * @param min the smallest accepted value, unbounded when null.
     * @param max the biggest accepted value, unbounded when null.
     * @return the argument declaration.
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
     * @param min the smallest accepted value, unbounded when null.
     * @param max the biggest accepted value, unbounded when null.
     * @return the argument declaration.
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
     * A boolean, written as `true` or `false`.
     *
     * @param name the argument name.
     * @return the argument declaration.
     */
    fun boolean(name: String): CommandArgument<Boolean> =
        CommandArgument(ArgumentType.Boolean(name))

    /**
     * A uuid in its dashed representation.
     *
     * @param name the argument name.
     * @return the argument declaration.
     */
    fun uuid(name: String): CommandArgument<UUID> =
        CommandArgument(ArgumentType.UUID(name))

    /**
     * An enum constant, written in lower case. The client offers every
     * constant as a completion.
     *
     * @param name the argument name.
     * @return the argument declaration.
     */
    inline fun <reified E : Enum<E>> enum(name: String): CommandArgument<E> {
        val argument = ArgumentEnum(name, E::class.java)
        argument.setFormat(ArgumentEnum.Format.LOWER_CASED)
        return CommandArgument(argument)
    }

    /**
     * A single online player, addressed by name or by a target selector.
     *
     * Parsing fails when the selector matches no player.
     *
     * @param name the argument name.
     * @return the argument declaration.
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