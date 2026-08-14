package net.mythicisland.core.island

import org.apache.logging.log4j.LogManager
import kotlin.system.exitProcess

object IslandServerInitializer {

    private val logger = LogManager.getLogger(IslandServerInitializer::class.java)

    fun initialize(server: Island) {
        try {
            logger.info("Initializing ${server.getServerId()} (${server.getServerName()})")
            server.start()
        } catch (e: Exception) {
            logger.error("Failed to initialize server, shutting down...", e)
            server.stop()
            exitProcess(1)
        }

        server.getMinecraftServer().start(server.getServerHost(), server.getServerPort())

        Runtime.getRuntime().addShutdownHook(Thread {
            server.stop()
        })
    }
}