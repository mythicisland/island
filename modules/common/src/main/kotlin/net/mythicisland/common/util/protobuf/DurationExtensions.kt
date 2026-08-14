package net.mythicisland.common.util.protobuf

import java.time.Duration
import com.google.protobuf.Duration as ProtobufDuration

/**
 * Converts this duration to a [ProtobufDuration].
 */
fun Duration.toTimestamp(): ProtobufDuration =
    ProtobufDuration.newBuilder()
        .setSeconds(seconds)
        .setNanos(nano)
        .build()

/**
 * Converts this protobuf [ProtobufDuration] to a [Duration].
 */
fun ProtobufDuration.toDuration(): Duration =
    Duration.ofSeconds(seconds, nanos.toLong())

/**
 * Converts these milliseconds to a protobuf [ProtobufDuration].
 */
fun Long.millisToProtobufDuration(): ProtobufDuration =
    Duration.ofMillis(this).toTimestamp()

/**
 * Converts these seconds to a protobuf [ProtobufDuration].
 */
fun Long.secondsToProtobufDuration(): ProtobufDuration =
    Duration.ofSeconds(this).toTimestamp()

/**
 * The total milliseconds of this protobuf [ProtobufDuration].
 */
fun ProtobufDuration.toMillis(): Long =
    toDuration().toMillis()