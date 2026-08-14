package net.mythicisland.common.util

import java.util.UUID

fun String.asUUID(): UUID = UUID.fromString(this)