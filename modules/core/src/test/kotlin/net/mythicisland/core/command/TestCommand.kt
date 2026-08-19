package net.mythicisland.core.command

class TestCommand(
    override val name: String,
    override val aliases: List<String> = emptyList(),
    private val declaration: (CommandBuilder) -> Unit,
) : Command {

    override fun build(command: CommandBuilder) {
        declaration(command)
    }
}
