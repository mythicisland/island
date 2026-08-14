package net.mythicisland.common.auth

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Rejects any gRPC call that does not present the secret in the `authorization` metadata header.
 */
class AuthInterceptor(token: String) : ServerInterceptor {

    private val expected = token.trim().toByteArray(StandardCharsets.UTF_8)
    private val authorization: Metadata.Key<String> = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): ServerCall.Listener<ReqT> {
        val provided = headers.get(authorization)
            ?.removePrefix("Bearer ")
            ?.trim()
            ?.toByteArray(StandardCharsets.UTF_8)

        if (provided == null || !MessageDigest.isEqual(provided, expected)) {
            call.close(
                Status.UNAUTHENTICATED.withDescription("Missing or invalid auth token"),
                Metadata(),
            )
            return object : ServerCall.Listener<ReqT>() {}
        }
        return next.startCall(call, headers)
    }
}