package net.mythicisland.core

import app.simplecloud.api.CloudApi
import net.minestom.server.instance.InstanceManager
import net.minestom.server.timer.Scheduler

interface IslandServer {

    /**
     * Gets the ID from the simplecloud server.
     */
    fun getServerId(): String

    /**
     * Gets the Name from the simplecloud server (e.g. battle-1).
     */
    fun getServerName(): String

    /**
     * Gets the address from this server.
     */
    fun getServerHost(): String

    /**
     * Gets the port from this server.
     */
    fun getServerPort(): Int

    /**
     * Gets the [CloudApi] instance from this server.
     */
    fun getCloudApi(): CloudApi

    /**
     * Gets the Global [Scheduler] from this server.
     */
    fun getScheduler(): Scheduler

    /**
     * Gets the [InstanceManager] from this server.
     */
    fun getInstanceManager(): InstanceManager

    /**
     * Method called on startup.
     */
    fun start()

    /**
     * Method called on shutdown.
     */
    fun stop()

}