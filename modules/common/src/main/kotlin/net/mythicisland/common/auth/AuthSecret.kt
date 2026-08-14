package net.mythicisland.common.auth

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions
import java.security.SecureRandom
import java.util.HexFormat

/**
 * Secret used to authenticate gRPC calls.
 */
object AuthSecret {

    private val random = SecureRandom()

    fun loadOrCreate(path: Path): String {
        if (Files.exists(path)) {
            val existing = Files.readString(path, StandardCharsets.UTF_8).trim()
            if (existing.isNotEmpty()) return existing
        }
        return generateAndStore(path)
    }

    private fun generateAndStore(path: Path): String {
        val bytes = ByteArray(32).also(random::nextBytes)
        val secret = HexFormat.of().formatHex(bytes)

        path.parent?.let { parent ->
            if (!Files.exists(parent)) Files.createDirectories(parent)
            restrictDir(parent)
        }

        Files.writeString(path, secret, StandardCharsets.UTF_8)
        restrictFile(path)
        return secret
    }

    private fun restrictFile(path: Path) = trySetPosix(path, "rw-------")

    private fun restrictDir(path: Path) = trySetPosix(path, "rwx------")

    private fun trySetPosix(path: Path, perms: String) {
        try {
            Files.setPosixFilePermissions(
                path,
                PosixFilePermissions.fromString(perms),
            )
        } catch (_: UnsupportedOperationException) { }
    }
}