package net.mythicisland.core.command

import net.minestom.server.command.ConsoleSender
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.mythicisland.core.command.argument.Arguments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ArgumentsTest {

    private enum class Visibility { PUBLIC, PRIVATE }

    private val sender = ConsoleSender()

    @Test
    fun `integer rejects values outside of its bounds`() {
        val size = Arguments.integer("size", min = 1, max = 32)

        assertEquals(12, size.argument.parse(sender, "12"))
        assertFailsWith<ArgumentSyntaxException> { size.argument.parse(sender, "33") }
        assertFailsWith<ArgumentSyntaxException> { size.argument.parse(sender, "0") }
    }

    @Test
    fun `word only accepts the allowed values`() {
        val action = Arguments.word("action", "add", "remove")

        assertEquals("add", action.argument.parse(sender, "add"))
        assertFailsWith<ArgumentSyntaxException> { action.argument.parse(sender, "delete") }
    }

    @Test
    fun `greedy string keeps the spaces of the input`() {
        val reason = Arguments.greedyString("reason")

        assertEquals("griefed my island", reason.argument.parse(sender, "griefed my island"))
    }

    @Test
    fun `enum parses its constants in lower case`() {
        val visibility = Arguments.enum<Visibility>("visibility")

        assertEquals(Visibility.PRIVATE, visibility.argument.parse(sender, "private"))
        assertFailsWith<ArgumentSyntaxException> { visibility.argument.parse(sender, "PRIVATE") }
    }

    @Test
    fun `map converts the parsed value`() {
        val level = Arguments.integer("level").map { value -> "level-$value" }

        assertEquals("level-3", level.argument.parse(sender, "3"))
    }

    @Test
    fun `optional carries the default value`() {
        val page = Arguments.integer("page")

        assertNull(page.defaultValue)
        assertNull(page.optional().defaultValue)
        assertEquals(1, page.optional(1).defaultValue?.invoke())
    }

    @Test
    fun `optional keeps the default value across a mapping`() {
        val page = Arguments.integer("page").optional(2).map { value -> value * 10 }

        assertEquals(20, page.defaultValue?.invoke())
    }
}
