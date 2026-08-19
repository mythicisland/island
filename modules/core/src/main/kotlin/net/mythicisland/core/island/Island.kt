package net.mythicisland.core.island

import app.simplecloud.api.CloudApi
import app.simplecloud.api.runtime.SimpleCloudRuntime
import net.minestom.server.Auth
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.InstanceManager
import net.minestom.server.timer.Scheduler
import net.mythicisland.core.IslandServer
import net.mythicisland.core.command.CommandManager
import net.mythicisland.core.command.defaults.TestCommand
import net.mythicisland.core.command.impl.CommandManagerImpl
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class Island : IslandServer {

    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val api: CloudApi = CloudApi.create()
    private val minecraftServer: MinecraftServer = createMinecraftServer()
    private val commandManager: CommandManager = CommandManagerImpl()

    override fun start() {
        logger.info("Starting server instance ${getServerName()} at ${getServerHost()}:${getServerPort()}")
        MinecraftServer.setBrandName("Island")
        MinecraftServer.setCompressionThreshold(-1)

        commandManager.register(TestCommand())

        MinestomTerminal.start()
    }

    override fun stop() {
        MinestomTerminal.stop()
        MinecraftServer.stopCleanly()
    }

    override fun getServerId(): String {
        return SimpleCloudRuntime.serverId()
    }

    override fun getServerName(): String {
        return SimpleCloudRuntime.serverName()
    }

    override fun getServerHost(): String {
        return System.getenv("SERVER_ADDRESS")
    }

    override fun getServerPort(): Int {
        return System.getenv("SERVER_PORT").toInt()
    }

    override fun getCloudApi(): CloudApi {
        return this.api
    }

    override fun getCommandManager(): CommandManager {
        return commandManager
    }

    override fun getInstanceManager(): InstanceManager {
        return MinecraftServer.getInstanceManager()
    }

    override fun getScheduler(): Scheduler {
        return MinecraftServer.getSchedulerManager()
    }

    fun getMinecraftServer(): MinecraftServer {
        return minecraftServer
    }

    private fun createMinecraftServer(): MinecraftServer {
        val secret = System.getenv("VELOCITY_SECRET")
        val auth = if (secret != null && secret.isNotEmpty()) Auth.Velocity(secret) else Auth.Online()
        return MinecraftServer.init(auth)
    }

}