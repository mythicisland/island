package net.mythicisland.core.command

class TestCommand(
    override val name: String,
    override val description: String = "A command used by the tests",
    override val aliases: List<String> = emptyList(),
    override val category: CommandCategory? = CommandCategory.DEFAULT,
    override val examples: List<String> = emptyList(),
    private val declaration: (CommandBuilder) -> Unit,
) : Command {

    override fun build(command: CommandBuilder) {
        declaration(command)
    }
}
