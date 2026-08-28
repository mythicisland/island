package net.mythicisland.core.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.mythicisland.core.command.argument.Arguments
import net.mythicisland.core.command.impl.CommandManagerImpl
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import net.minestom.server.command.CommandManager as MinestomCommandManager
import net.minestom.server.command.builder.CommandResult as MinestomResult

class CommandExecutionTest {

    private val sender = RecordingSender()

    private lateinit var minestom: MinestomCommandManager
    private lateinit var commands: CommandManagerImpl

    @BeforeTest
    fun createServerProcess() {
        MinecraftServer.init()
        minestom = MinecraftServer.getCommandManager()
        commands = CommandManagerImpl()
    }

    @Test
    fun `passes the parsed arguments to the executor`() {
        val size = Arguments.integer("size", min = 1, max = 32)
        var received: Int? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("border").argument(size).executes { context ->
                    received = context[size]
                    CommandResult.Success
                }
            }
        )

        val result = minestom.execute(sender, "island border 12")

        assertEquals(MinestomResult.Type.SUCCESS, result.type)
        assertEquals(12, received)
    }

    @Test
    fun `does not run the executor for an argument outside of its bounds`() {
        val size = Arguments.integer("size", min = 1, max = 32)
        var executed = false

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("border").argument(size).executes {
                    executed = true
                    CommandResult.Success
                }
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
                builder.literal("members").argument(page).executes { context ->
                    received = context[page]
                    CommandResult.Success
                }
            }
        )

        minestom.execute(sender, "island members")
        assertEquals(1, received)

        minestom.execute(sender, "island members 4")
        assertEquals(4, received)
    }

    @Test
    fun `finds an omitted argument without a default as null`() {
        val reason = Arguments.greedyString("reason").optional()
        var received: String? = "unset"

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("close").argument(reason).executes { context ->
                    received = context.find(reason)
                    CommandResult.Success
                }
            }
        )

        minestom.execute(sender, "island close")

        assertNull(received)
    }

    @Test
    fun `fails when reading an argument of another branch`() {
        val size = Arguments.integer("size")
        val page = Arguments.integer("page")
        var failure: Throwable? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("border").argument(size).executes { context ->
                    failure = runCatching { context[page] }.exceptionOrNull()
                    CommandResult.Success
                }
            }
        )

        minestom.execute(sender, "island border 4")

        assertIs<IllegalArgumentException>(failure)
    }

    @Test
    fun `fails when reading an omitted argument without a default`() {
        val reason = Arguments.greedyString("reason").optional()
        var failure: Throwable? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("close").argument(reason).executes { context ->
                    failure = runCatching { context[reason] }.exceptionOrNull()
                    CommandResult.Success
                }
            }
        )

        minestom.execute(sender, "island close")

        assertIs<IllegalArgumentException>(failure)
    }

    @Test
    fun `keeps the spaces of a greedy argument`() {
        val reason = Arguments.greedyString("reason")
        var received: String? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("close").argument(reason).executes { context ->
                    received = context[reason]
                    CommandResult.Success
                }
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
                builder.literal("home").executes {
                    executed = true
                    CommandResult.Success
                }
            }
        )

        assertEquals(MinestomResult.Type.SUCCESS, minestom.execute(sender, "is home").type)
        assertTrue(executed)
    }

    @Test
    fun `sends nothing when the executor reports success`() {
        commands.register(
            TestCommand("island") { builder ->
                builder.literal("home").executes { CommandResult.Success }
            }
        )

        minestom.execute(sender, "island home")

        assertTrue(sender.messages.isEmpty())
    }

    @Test
    fun `sends the usage when the executor reports a syntax error`() {
        val size = Arguments.integer("size")
        val reason = Arguments.greedyString("reason").optional()

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("border").argument(size).argument(reason).executes { CommandResult.Syntax }
            }
        )

        minestom.execute(sender, "island border 4")

        assertEquals(
            Component.text("Use it like this: /island border <size> [reason]", NamedTextColor.RED),
            sender.messages.single(),
        )
    }

    @Test
    fun `sends the message of a failure`() {
        commands.register(
            TestCommand("island") { builder ->
                builder.literal("home").executes { CommandResult.failure("You have no island.") }
            }
        )

        minestom.execute(sender, "island home")

        assertEquals(
            Component.text("You have no island.", NamedTextColor.RED),
            sender.messages.single(),
        )
    }

    @Test
    fun `blocks a player only branch for the console`() {
        var executed = false

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("home").playerOnly().executes {
                    executed = true
                    CommandResult.Success
                }
            }
        )

        minestom.execute(sender, "island home")

        assertFalse(executed)
        assertEquals(
            Component.text("This command can only be used by players.", NamedTextColor.RED),
            sender.messages.single(),
        )
    }

    @Test
    fun `fails when the console reads the player of a branch that is open to all`() {
        var failure: Throwable? = null

        commands.register(
            TestCommand("island") { builder ->
                builder.literal("home").executes { context ->
                    failure = runCatching { context.player }.exceptionOrNull()
                    CommandResult.Success
                }
            }
        )

        minestom.execute(sender, "island home")

        assertIs<IllegalStateException>(failure)
    }

    @Test
    fun `runs the branch without arguments as the default executor`() {
        var executed = false

        commands.register(
            TestCommand("island") { builder ->
                builder.executes {
                    executed = true
                    CommandResult.Success
                }
            }
        )

        minestom.execute(sender, "island")

        assertTrue(executed)
    }

    @Test
    fun `stops executing an unregistered command`() {
        var executed = false
        val command = TestCommand("island") { builder ->
            builder.literal("home").executes {
                executed = true
                CommandResult.Success
            }
        }

        commands.register(command)
        commands.unregister(command)

        assertEquals(MinestomResult.Type.UNKNOWN, minestom.execute(sender, "island home").type)
        assertFalse(executed)
        assertTrue(commands.getRegisteredCommands().isEmpty())
    }

    @Test
    fun `looks the registered command up by name and alias`() {
        val command = TestCommand("island", aliases = listOf("is")) { builder ->
            builder.literal("home").executes { CommandResult.Success }
        }

        commands.register(command)

        val registry = commands.getCommandRegistry()
        assertEquals(command, registry.getCommand("island"))
        assertEquals(command, registry.getCommand("IS"))
        assertNull(registry.getCommand("lobby"))
    }

    @Test
    fun `fails when a name is registered twice`() {
        commands.register(
            TestCommand("island") { builder ->
                builder.literal("home").executes { CommandResult.Success }
            }
        )

        assertFailsWith<IllegalStateException> {
            commands.register(
                TestCommand("island") { builder ->
                    builder.literal("visit").executes { CommandResult.Success }
                }
            )
        }
    }
}
