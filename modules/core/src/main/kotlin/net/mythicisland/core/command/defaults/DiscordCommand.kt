package net.mythicisland.core.command.defaults

import net.mythicisland.core.command.Command
import net.mythicisland.core.command.CommandBuilder
import net.mythicisland.core.command.CommandResult
import net.mythicisland.core.mm

class DiscordCommand : Command {

    override val name = "discord"

    override val description = "Shows an invite to our discord server"

    override fun build(command: CommandBuilder) {
        command.executes { context ->
            context.sender.sendMessage(mm.deserialize("<click:open_url:'https://discord.gg/5WM3sqJqy'><gradient:#7289da:#51629c:#51629c>Click to join our</gradient> <#7289da><bold>Discord</bold><#51629c>!</click>"))
            CommandResult.Success
        }
    }

}