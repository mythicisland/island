package net.mythicisland.ui.render

import net.minestom.server.adventure.MinestomAdventure
import net.minestom.server.entity.Player
import java.util.Locale

data class UiRenderContext(
    val player: Player,
    val tick: Long,
) {

    /**
     * The locale translations are resolved for, the same one the platform uses
     * when it translates an outgoing packet for this player.
     */
    val locale: Locale
        get() = player.locale ?: MinestomAdventure.getDefaultLocale()
}
