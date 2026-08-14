package net.mythicisland.common.auth

import io.grpc.CallCredentials
import io.grpc.Metadata
import java.util.concurrent.Executor

class AuthCredentials(val token: String) : CallCredentials() {
    override fun applyRequestMetadata(
        requestInfo: RequestInfo?,
        appExecutor: Executor?,
        applier: MetadataApplier?
    ) {
        val meta = Metadata()
        val key = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
        meta.put(key, this.token)
        applier?.apply(meta)
    }
}