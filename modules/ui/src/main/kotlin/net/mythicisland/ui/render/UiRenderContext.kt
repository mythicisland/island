package net.mythicisland.ui.render

import net.minestom.server.entity.Player

data class UiRenderContext(
    val player: Player,
    val tick: Long,
)