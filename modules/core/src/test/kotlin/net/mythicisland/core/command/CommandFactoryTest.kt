package net.mythicisland.core.command

import net.minestom.server.command.builder.CommandSyntax
import net.mythicisland.core.command.argument.Arguments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import net.minestom.server.command.builder.Command as MinestomCommand

class CommandFactoryTest {

    private val factory = CommandFactory()

    @Test
    fun `builds one syntax per declared branch`() {
        val target = Arguments.player("target")
        val size = Arguments.integer("size")

        val command = factory.create(
            TestCommand("island", aliases = listOf("is")) { builder ->
                builder.literal("visit").argument(target).executes { CommandResult.Success }
                builder.literal("border").argument(size).executes { CommandResult.Success }
            }
        )

        assertEquals("island", command.name)
        assertEquals(listOf("is"), command.aliases.toList())
        assertEquals(
            listOf(listOf("visit", "target"), listOf("border", "size")),
            syntaxIds(command),
        )
    }

    @Test
    fun `expands optional arguments into one syntax per argument count`() {
        val member = Arguments.player("member")
        val reason = Arguments.greedyString("reason").optional()

        val command = factory.create(
            TestCommand("island") { builder ->
                builder.literal("invite").argument(member).argument(reason).executes { CommandResult.Success }
            }
        )

        assertEquals(
            listOf(listOf("invite", "member"), listOf("invite", "member", "reason")),
            syntaxIds(command),
        )
    }

    @Test
    fun `keeps the command without a default executor when no branch is empty`() {
        val command = factory.create(
            TestCommand("island") { builder ->
                builder.literal("visit").executes { CommandResult.Success }
            }
        )

        assertNull(command.defaultExecutor)
    }

    @Test
    fun `fails when two branches execute without arguments`() {
        val page = Arguments.integer("page").optional(1)

        val error = assertFailsWith<IllegalArgumentException> {
            factory.create(
                TestCommand("island") { builder ->
                    builder.executes { CommandResult.Success }
                    builder.argument(page).executes { CommandResult.Success }
                }
            )
        }

        assertTrue(error.message!!.contains("more than one executor without arguments"))
    }

    @Test
    fun `fails when a required argument follows an optional one`() {
        val page = Arguments.integer("page").optional(1)
        val size = Arguments.integer("size")

        assertFailsWith<IllegalArgumentException> {
            factory.create(
                TestCommand("island") { builder ->
                    builder.argument(page).argument(size).executes { CommandResult.Success }
                }
            )
        }
    }

    @Test
    fun `fails when the same argument name is used twice in a syntax`() {
        val first = Arguments.integer("size")
        val second = Arguments.integer("size")

        assertFailsWith<IllegalArgumentException> {
            factory.create(
                TestCommand("island") { builder ->
                    builder.argument(first).argument(second).executes { CommandResult.Success }
                }
            )
        }
    }

    private fun syntaxIds(command: MinestomCommand): List<List<String>> =
        command.syntaxes.map { syntax: CommandSyntax -> syntax.arguments.map { argument -> argument.id } }
}
