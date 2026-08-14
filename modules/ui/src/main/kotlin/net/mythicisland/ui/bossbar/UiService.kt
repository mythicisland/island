package net.mythicisland.ui.bossbar

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.timer.TaskSchedule
import net.mythicisland.ui.render.UiRenderable
import net.mythicisland.ui.text.VisualElement
import java.util.UUID

/**
 * The boss bar hud service.
 */
class UiService(
    private val miniMessage: MiniMessage = MiniMessage.miniMessage(),
) {

    private val node = EventNode.all("moonrise-ui")
    private val slots = ObjectArrayList<BossBarSlot>()
    private val players = Object2ObjectOpenHashMap<UUID, PlayerHud>()

    /**
     * Registers a new hud line. Lower [order] renders further up.
     */
    fun slot(key: String, order: Int = slots.size): BossBarSlot {
        require(slots.none { it.key == key }) {
            "Bossbar slot '$key' is already registered."
        }

        val slot = BossBarSlot(key, order)
        slots.add(slot)
        slots.sortWith(compareBy(BossBarSlot::order))
        return slot
    }

    fun slots(): List<BossBarSlot> = ObjectArrayList(slots)

    fun set(player: Player, slot: BossBarSlot, content: VisualElement) {
        set(player, slot, content.asComponent())
    }

    fun set(player: Player, slot: BossBarSlot, miniMessage: String) {
        set(player, slot, this.miniMessage.deserialize(miniMessage))
    }

    fun set(player: Player, slot: BossBarSlot, content: Component) {
        hud(player).set(slot, content)
    }

    fun set(player: Player, slot: BossBarSlot, content: UiRenderable) {
        hud(player).set(slot, content)
    }

    fun set(players: Iterable<Player>, slot: BossBarSlot, content: VisualElement) {
        content.asComponent().let { component ->
            players.forEach { set(it, slot, component) }
        }
    }

    fun set(players: Iterable<Player>, slot: BossBarSlot, content: Component) {
        players.forEach { set(it, slot, content) }
    }

    fun set(players: Iterable<Player>, slot: BossBarSlot, content: UiRenderable) {
        players.forEach { set(it, slot, content) }
    }

    fun clear(player: Player, slot: BossBarSlot) {
        hud(player).clear(slot)
    }

    fun clear(players: Iterable<Player>, slot: BossBarSlot) {
        players.forEach { clear(it, slot) }
    }

    fun show(player: Player) {
        hud(player).show()
    }

    fun hide(player: Player) {
        hud(player).hide()
    }

    fun isVisible(player: Player): Boolean =
        hud(player).visible

    fun hud(player: Player): PlayerHud =
        players.getOrPut(player.uuid) {
            PlayerHud(player)
        }.also { it.sync(slots) }

    fun initialize(): UiService {
        node.addListener(PlayerSpawnEvent::class.java) { event ->
            if (event.isFirstSpawn) {
                hud(event.player)
            }
        }

        node.addListener(PlayerDisconnectEvent::class.java) { event ->
            players.remove(event.player.uuid)?.dispose()
        }

        MinecraftServer.getGlobalEventHandler().addChild(node)
        MinecraftServer.getConnectionManager().onlinePlayers.forEach(::hud)

        MinecraftServer.getSchedulerManager()
            .buildTask {
                MinecraftServer.getConnectionManager().onlinePlayers.forEach { player ->
                    hud(player).render()
                }
            }
            .repeat(TaskSchedule.tick(2))
            .schedule()

        return this
    }
}