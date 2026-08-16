package net.mythicisland.core.command

import net.minestom.server.command.ConsoleSender
import net.minestom.server.command.builder.CommandResult
import net.mythicisland.core.command.argument.Arguments
import net.mythicisland.core.command.impl.CommandManagerImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import net.minestom.server.command.CommandManager as MinestomCommandManager

class CommandExecutionTest {

    private val minestom = MinestomCommandManager()
    private val sender = ConsoleSender()

    private var permitted = true
    private val commands = CommandManagerImpl(
        minestom,
        CommandPermissionHandler { _, _ -> permitted },
    )

    @Test
    fun `passes the parsed arguments to the executor`() {
        val size = Arguments.integer("size", min = 1, max = 32)
        var received: Int? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("border").argument(size).executes { context -> received = context[size] }
            }
        )

        val result = minestom.execute(sender, "island border 12")

        assertEquals(CommandResult.Type.SUCCESS, result.type)
        assertEquals(12, received)
    }

    @Test
    fun `rejects an argument outside of its bounds`() {
        val size = Arguments.integer("size", min = 1, max = 32)
        var executed = false

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("border").argument(size).executes { executed = true }
            }
        )

        minestom.execute(sender, "island border 64")

        assertFalse(executed)
    }

    @Test
    fun `applies the default value of an omitted argument`() {
        val page = Arguments.integer("page", min = 1).optional(1)
        var received: Int? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("members").argument(page).executes { context -> received = context[page] }
            }
        )

        minestom.execute(sender, "island members")
        assertEquals(1, received)

        minestom.execute(sender, "island members 4")
        assertEquals(4, received)
    }

    @Test
    fun `reports an omitted argument without a default as missing`() {
        val reason = Arguments.greedyString("reason").optional()
        var received: String? = "unset"

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("close").argument(reason).executes { context -> received = context.find(reason) }
            }
        )

        minestom.execute(sender, "island close")

        assertNull(received)
    }

    @Test
    fun `rejects reading an argument of another branch`() {
        val size = Arguments.integer("size")
        val page = Arguments.integer("page")
        var failure: Throwable? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("border").argument(size).executes { context ->
                    try {
                        context[page]
                    } catch (exception: IllegalArgumentException) {
                        failure = exception
                    }
                }
            }
        )

        minestom.execute(sender, "island border 4")

        assertNotNull(failure)
    }

    @Test
    fun `rejects reading an omitted argument without a default`() {
        val reason = Arguments.greedyString("reason").optional()
        var failure: Throwable? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("close").argument(reason).executes { context ->
                    try {
                        context[reason]
                    } catch (exception: IllegalArgumentException) {
                        failure = exception
                    }
                }
            }
        )

        minestom.execute(sender, "island close")

        assertNotNull(failure)
    }

    @Test
    fun `keeps the spaces of a greedy argument`() {
        val reason = Arguments.greedyString("reason")
        var received: String? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("close").argument(reason).executes { context -> received = context[reason] }
            }
        )

        minestom.execute(sender, "island close broke the rules")

        assertEquals("broke the rules", received)
    }

    @Test
    fun `resolves the command by its alias`() {
        var executed = false

        commands.register(
            TestCommand("island", aliases = listOf("is")) { builder ->
                builder.literal("home").executes { executed = true }
            }
        )

        assertEquals(CommandResult.Type.SUCCESS, minestom.execute(sender, "is home").type)
        assertTrue(executed)
    }

    @Test
    fun `blocks a branch the sender has no permission for`() {
        var executed = false

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("delete").permission("island.delete").executes { executed = true }
            }
        )

        permitted = false
        minestom.execute(sender, "island delete")
        assertFalse(executed)

        permitted = true
        minestom.execute(sender, "island delete")
        assertTrue(executed)
    }

    @Test
    fun `blocks a player only branch for the console`() {
        var executed = false

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("home").executesPlayer { _, _ -> executed = true }
            }
        )

        minestom.execute(sender, "island home")

        assertFalse(executed)
    }

    @Test
    fun `runs the branch without arguments as the default executor`() {
        var executed = false

        commands.register(
            TestCommand("island") { builder ->
                builder.executes { executed = true }
            }
        )

        minestom.execute(sender, "island")

        assertTrue(executed)
    }

    @Test
    fun `stops executing an unregistered command`() {
        var executed = false
        val command = TestCommand("island") { builder ->
            builder.literal("home").executes { executed = true }
        }

        commands.register(command)
        commands.unregister(command)

        assertEquals(CommandResult.Type.UNKNOWN, minestom.execute(sender, "island home").type)
        assertFalse(executed)
        assertTrue(commands.getRegisteredCommands().isEmpty())
    }

    @Test
    fun `looks the registered command up by name and alias`() {
        val command = TestCommand("island", aliases = listOf("is")) { builder ->
            builder.literal("home").executes { }
        }

        commands.register(command)

        val registry = commands.getCommandRegistry()
        assertEquals(command, registry.getCommand("island"))
        assertEquals(command, registry.getCommand("IS"))
        assertNull(registry.getCommand("lobby"))
    }
}
