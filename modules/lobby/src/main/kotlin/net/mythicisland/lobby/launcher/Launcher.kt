package net.mythicisland.lobby.launcher

import net.mythicisland.core.island.IslandServerInitializer
import net.mythicisland.lobby.Lobby

fun main() {
    IslandServerInitializer.initialize(Lobby())
}