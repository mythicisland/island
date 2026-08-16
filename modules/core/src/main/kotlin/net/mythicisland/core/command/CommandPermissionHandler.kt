package net.mythicisland.core.command

import net.minestom.server.command.CommandSender

/**
 * Decides whether a sender holds a permission.
 *
 * Minestom does not ship a permission system, so the command api asks this
 * handler whenever a [Command] or a single syntax requires one.
 */
fun interface CommandPermissionHandler {

    /**
     * Checks a permission.
     *
     * Called on every execution and additionally whenever the command graph is
     * sent to a player, so it should be cheap and must not send messages.
     *
     * @param sender the sender to check.
     * @param permission the required permission.
     * @return true if the sender is allowed.
     */
    fun hasPermission(sender: CommandSender, permission: String): Boolean

}
