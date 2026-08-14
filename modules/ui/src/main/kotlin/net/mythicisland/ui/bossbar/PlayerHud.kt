package net.mythicisland.ui.bossbar

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.mythicisland.ui.render.UiRenderContext
import net.mythicisland.ui.render.UiRenderable

/**
 * The hud of a player.
 */
class PlayerHud internal constructor(
    private val player: Player,
) {

    private val bars = Object2ObjectLinkedOpenHashMap<BossBarSlot, BossBar>()
    private val content = Object2ObjectOpenHashMap<BossBarSlot, UiRenderable>()

    var visible: Boolean = true
        private set

    internal fun sync(slots: List<BossBarSlot>) {
        slots.forEach { slot ->
            bars.getOrPut(slot) {
                emptyBar().also {
                    if (visible) {
                        player.showBossBar(it)
                    }
                }
            }
        }
    }

    internal fun set(slot: BossBarSlot, content: Component) {
        this.content.remove(slot)
        bar(slot).name(content)
    }

    internal fun set(slot: BossBarSlot, content: UiRenderable) {
        this.content[slot] = content
        bar(slot).name(content.render(context()))
    }

    internal fun clear(slot: BossBarSlot) {
        content.remove(slot)
        bar(slot).name(Component.empty())
    }

    internal fun show() {
        visible = true
        bars.values.forEach(player::showBossBar)
    }

    internal fun hide() {
        visible = false
        bars.values.forEach(player::hideBossBar)
    }

    internal fun render() {
        if (!visible || content.isEmpty()) {
            return
        }

        val context = context()
        content.forEach { (slot, content) ->
            bars.getValue(slot).name(content.render(context))
        }
    }

    internal fun dispose() {
        bars.values.forEach(player::hideBossBar)
        bars.clear()
        content.clear()
    }

    private fun bar(slot: BossBarSlot): BossBar =
        requireNotNull(bars[slot]) {
            "Bossbar slot '${slot.key}' is not registered."
        }

    private fun context(): UiRenderContext = UiRenderContext(player, player.instance?.worldAge ?: 0)

    private fun emptyBar(): BossBar =
        BossBar.bossBar(
            Component.empty(),
            0f,
            BossBar.Color.BLUE,
            BossBar.Overlay.PROGRESS,
        )
}