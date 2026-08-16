package net.mythicisland.core.command

/**
 * A [Command] whose syntaxes come from a lambda, so a test only shows what it
 * actually tests.
 */
class TestCommand(
    override val name: String,
    override val aliases: List<String> = emptyList(),
    override val permission: String? = null,
    private val declaration: (CommandBuilder) -> Unit,
) : Command {

    override fun build(command: CommandBuilder) {
        declaration(command)
    }
}
