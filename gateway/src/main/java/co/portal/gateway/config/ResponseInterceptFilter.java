package co.portal.gateway.config;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.service.RabbitMQService;
import co.portal.gateway.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ResponseInterceptFilter implements GlobalFilter, Ordered {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private Utils utils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("GATEWAY RESPONSE FILTER");

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        // Create a decorated response to intercept and log the body
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                // Convert Publisher to Flux to use reactive operators
                return super.writeWith(Flux.from(body).flatMap(dataBuffer -> {
                    // Convert the DataBuffer to a String
                    String responseBody = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString();
                    log.info("Response Body: {}", responseBody);

                    String statusCode = originalResponse.getStatusCode() != null ?
                            originalResponse.getStatusCode().toString() : null;

                    ActivityLog activityLog = utils.getActivityLog(responseBody, statusCode);

                    rabbitMQService.publishLog(activityLog, "UPDATE");

                    // Re-create a DataBuffer with the same content to forward to the client
                    DataBuffer newBuffer = bufferFactory.wrap(responseBody.getBytes(StandardCharsets.UTF_8));
                    return Flux.just(newBuffer);
                }));
            }
        };

        // Proceed with the chain using the decorated response
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }


    @Override
    public int getOrder() {
        return -1;
    }
}




