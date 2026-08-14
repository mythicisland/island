package net.mythicisland.ui.bossbar

/**
 * One boss bar line of the hud. Slots are rendered top to bottom by [order].
 */
class BossBarSlot internal constructor(
    val key: String,
    val order: Int,
) {

    override fun equals(other: Any?): Boolean =
        other is BossBarSlot && key == other.key

    override fun hashCode(): Int =
        key.hashCode()

    override fun toString(): String =
        "BossBarSlot(key=$key, order=$order)"
}