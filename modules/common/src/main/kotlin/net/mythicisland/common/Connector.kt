package net.mythicisland.common

import app.simplecloud.api.CloudApi
import app.simplecloud.api.CloudApiOptions
import net.mythicisland.common.nats.NatsConnectionHandler
import net.mythicisland.common.nats.NatsConnectionManager
import net.mythicisland.common.nats.NatsErrorListener

object Connector {

    /**
     * Creates a NATS connection manager.
     */
    fun connectToNats(
        natsUrl: String,
        natsUser: String,
        natsSecret: String,
        errorListener: NatsErrorListener = NatsErrorListener(),
        connectionHandler: NatsConnectionHandler = NatsConnectionHandler()
    ): NatsConnectionManager {
        return NatsConnectionManager(
            natsUrl,
            natsUser,
            natsSecret,
            errorListener,
            connectionHandler,
        )
    }

    /**
     * Creates a Cloud API instance.
     */
    fun connectToController(
        networkId: String,
        networkSecret: String,
        controllerUrl: String,
        controllerNatsUrl: String
    ): CloudApi {
        return CloudApi.create(
            CloudApiOptions.builder()
                .networkId(networkId)
                .networkSecret(networkSecret)
                .controllerUrl(controllerUrl)
                .natsUrl(controllerNatsUrl)
                .build()
        )
    }

}