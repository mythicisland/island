package net.mythicisland.common.util.protobuf

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Converts this instant to a [Timestamp].
 */
fun Instant.toTimestamp(): Timestamp =
    Timestamp.newBuilder()
        .setSeconds(epochSecond)
        .setNanos(nano)
        .build()

/**
 * Converts this [Timestamp] to an [Instant].
 */
fun Timestamp.toInstant(): Instant =
    Instant.ofEpochSecond(seconds, nanos.toLong())

/**
 * Converts this local date time to a [Timestamp].
 */
fun LocalDateTime.toTimestamp(zone: ZoneId = ZoneId.systemDefault()): Timestamp =
    atZone(zone).toInstant().toTimestamp()

/**
 * Converts this [Timestamp] to a [LocalDateTime] in [zone].
 */
fun Timestamp.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    LocalDateTime.ofInstant(toInstant(), zone)

/**
 * Converts these epoch milliseconds to a [Timestamp].
 */
fun Long.epochMillisToTimestamp(): Timestamp =
    Instant.ofEpochMilli(this).toTimestamp()

/**
 * The epoch milliseconds of this [Timestamp].
 */
fun Timestamp.toEpochMillis(): Long =
    toInstant().toEpochMilli()