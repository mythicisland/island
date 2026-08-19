package net.mythicisland.core.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.mythicisland.core.command.argument.Arguments
import net.mythicisland.core.command.defaults.HelpCommand
import net.mythicisland.core.command.impl.CommandManagerImpl
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import net.minestom.server.command.CommandManager as MinestomCommandManager

class HelpCommandTest {

    private val sender = RecordingSender()

    private lateinit var minestom: MinestomCommandManager
    private lateinit var commands: CommandManagerImpl

    @BeforeTest
    fun createServerProcess() {
        MinecraftServer.init()
        minestom = MinecraftServer.getCommandManager()
        commands = CommandManagerImpl()
        commands.register(HelpCommand(commands.getCommandRegistry()))
    }

    @Test
    fun `lists the categories in their order`() {
        val economy = CommandCategory(2, "Economy & Shops")
        val world = CommandCategory(1, "World & Building")

        register("shop", category = economy)
        register("tp", category = world)

        minestom.execute(sender, "help")

        val text = plain(sender.messages.single())
        assertTrue(text.indexOf("World & Building") < text.indexOf("Economy & Shops"))
        assertTrue(text.indexOf("Economy & Shops") < text.indexOf("General"))
    }

    @Test
    fun `leaves out a hidden command`() {
        register("secret", category = CommandCategory.HIDDEN)
        register("shown")

        minestom.execute(sender, "help")

        val text = plain(sender.messages.single())
        assertTrue(text.contains("/shown"))
        assertFalse(text.contains("/secret"))
    }

    @Test
    fun `puts two commands of a category on one line`() {
        val category = CommandCategory(1, "World & Building")
        register("tp", category = category)
        register("tphere", category = category)
        register("spawn", category = category)

        minestom.execute(sender, "help")

        val lines = plain(sender.messages.single()).lines()
        assertTrue(lines.any { line -> line.contains("/tp ") && line.contains("/tphere") })
        assertTrue(lines.any { line -> line.trim() == "/spawn" })
    }

    @Test
    fun `shows the details of a single command`() {
        val mode = Arguments.word("mode").describe("The game mode to set")
        val player = Arguments.player("player").optional().describe("The target player")

        commands.register(
            TestCommand(
                name = "gamemode",
                description = "Changes the game mode of a player",
                examples = listOf("/gamemode creative", "/gm 1"),
            ) { builder ->
                builder.literal("creative", description = "Sets gamemode to Creative")
                    .executes { CommandResult.Success }
                builder.argument(mode).argument(player).executes { CommandResult.Success }
            }
        )

        minestom.execute(sender, "help gamemode")

        val text = plain(sender.messages.single())
        assertTrue(text.startsWith("Changes the game mode of a player"))
        assertTrue(text.contains("/gamemode creative"))
        assertTrue(text.contains("/gamemode <mode> [player]"))
        assertTrue(text.contains("mode: The game mode to set"))
        assertTrue(text.contains("player: The target player"))
        assertTrue(text.contains("creative: Sets gamemode to Creative"))
        assertTrue(text.contains("/gm 1"))
    }

    @Test
    fun `reports an unknown command`() {
        minestom.execute(sender, "help nope")

        assertEquals(
            Component.text("There is no command called 'nope'.", NamedTextColor.RED),
            sender.messages.single(),
        )
    }

    @Test
    fun `carries the details of a command as the hover of its entry`() {
        register("shop")

        minestom.execute(sender, "help")

        val hovered = flatten(sender.messages.single())
            .mapNotNull { component -> component.hoverEvent() }
            .map { hover -> plain(hover.value() as Component) }

        assertTrue(hovered.any { details -> details.contains("A command used by the tests") })
    }

    private fun register(name: String, category: CommandCategory? = CommandCategory.DEFAULT) {
        commands.register(
            TestCommand(name = name, category = category) { builder ->
                builder.executes { CommandResult.Success }
            }
        )
    }

    private fun plain(component: Component): String =
        flatten(component).joinToString("") { part ->
            if (part is TextComponent) part.content() else ""
        }

    private fun flatten(component: Component): List<Component> =
        listOf(component) + component.children().flatMap { child -> flatten(child) }
}
