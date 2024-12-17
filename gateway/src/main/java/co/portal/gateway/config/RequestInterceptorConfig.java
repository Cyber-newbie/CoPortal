package co.portal.gateway.config;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class RequestInterceptorConfig implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        // Wrap the response in a decorator
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(
                            fluxBody.map(dataBuffer -> {
                                // Read the content of the DataBuffer
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                String responseBody = new String(content, StandardCharsets.UTF_8);

                                // Log the response body
                                log.info("Captured Response Body: {}", responseBody);

                                // Return the original DataBuffer so the response is not corrupted
                                return bufferFactory.wrap(content);
                            })
                    );
                }
                // If the body is not a Flux, proceed without modification
                return super.writeWith(body);
            }
        };

        // Replace the original response with the decorated response
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
}

