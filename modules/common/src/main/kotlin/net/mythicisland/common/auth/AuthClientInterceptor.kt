package net.mythicisland.common.auth

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

/**
 * Attaches the secret as `authorization: Bearer <token>` to every outgoing call.
 */
class AuthClientInterceptor(token: String) : ClientInterceptor {

    private val header = "Bearer ${token.trim()}"
    private val authorization: Metadata.Key<String> = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel,
    ): ClientCall<ReqT, RespT> {
        return object : SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                headers.put(authorization, header)
                super.start(responseListener, headers)
            }
        }
    }
}