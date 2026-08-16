package net.mythicisland.core.command

/**
 * A [Command] whose syntaxes are declared by a lambda, to keep the tests to
 * the declaration under test.
 */
internal class TestCommand(
    override val name: String,
    override val aliases: List<String> = emptyList(),
    override val permission: String? = null,
    private val declaration: (CommandBuilder) -> Unit,
) : Command {

    override fun build(command: CommandBuilder) {
        declaration(command)
    }
}
