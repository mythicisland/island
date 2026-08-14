package net.mythicisland.common.configurate

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type
import java.time.Duration

/**
 * Configurate [TypeSerializer] for [Duration].
 */
object DurationSerializer : TypeSerializer<Duration> {

    // Matches patterns like "2d", "1h30m", "10s", "1d2h30m10s"
    private val PATTERN = Regex("""^(?:(\d+)d)?(?:(\d+)h)?(?:(\d+)m)?(?:(\d+)s)?$""")

    override fun deserialize(type: Type, node: ConfigurationNode): Duration {
        val raw = node.string?.trim()
            ?: throw SerializationException("Expected a duration string (e.g. \"1h30m\", \"30s\")")

        if (raw.isEmpty()) throw SerializationException("Duration string must not be empty")

        if (raw.uppercase().startsWith("P")) {
            return try {
                Duration.parse(raw)
            } catch (e: Exception) {
                throw SerializationException("Invalid ISO-8601 duration '$raw': ${e.message}")
            }
        }

        val match = PATTERN.matchEntire(raw)
            ?: throw SerializationException("Invalid duration '$raw'. Expected format: 1d2h30m10s")

        val (days, hours, minutes, seconds) = match.destructured
        if (days.isEmpty() && hours.isEmpty() && minutes.isEmpty() && seconds.isEmpty()) {
            throw SerializationException("Duration '$raw' contains no recognizable units (use d/h/m/s)")
        }

        return Duration.ofDays(days.toLongOrNull() ?: 0)
            .plusHours(hours.toLongOrNull() ?: 0)
            .plusMinutes(minutes.toLongOrNull() ?: 0)
            .plusSeconds(seconds.toLongOrNull() ?: 0)
    }

    override fun serialize(type: Type, obj: Duration?, node: ConfigurationNode) {
        node.set(obj?.toCompact())
    }

    /** Converts a [Duration] to a compact string like `"1d2h30m10s"`. */
    private fun Duration.toCompact(): String {
        if (isZero) return "0s"
        val d = toDaysPart()
        val h = toHoursPart()
        val m = toMinutesPart()
        val s = toSecondsPart()
        return buildString {
            if (d > 0) append("${d}d")
            if (h > 0) append("${h}h")
            if (m > 0) append("${m}m")
            if (s > 0) append("${s}s")
        }
    }
}