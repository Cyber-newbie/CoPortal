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
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
public class RequestInterceptFilter implements GlobalFilter, Ordered {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private Utils utils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String username = Optional.ofNullable(
                exchange.getRequest().getHeaders().getFirst("loggedInUsername"))
                .orElse("UNKNOWN");
        ServerHttpRequest originalRequest =  exchange.getRequest();
        String requestURI = exchange.getRequest().getURI().toString();
        String method = exchange.getRequest().getMethodValue();
        String clientIp = Optional.ofNullable
                (exchange.getRequest().getRemoteAddress().toString()).orElse("UNKNOWN");

        log.info("Incoming Request: [{}] {} from IP: {}", method, requestURI, clientIp);

        // Create and send the ActivityLog to the logging service
        ActivityLog activityLog = ActivityLog.builder()
                .username(username)
                .requestURI(requestURI)
                .responseBody(null)
                .statusCode(null)
                .timestamp(LocalDate.now())
                .build();


//        rabbitMQService.publishLog(activityLog, "CREATE");

        if (method.equals(HttpMethod.POST.name()) || method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.PATCH.name())) {
            return DataBufferUtils.join(originalRequest.getBody())
                    .flatMap(dataBuffer -> {
                        // Convert DataBuffer to String
                        String requestBody = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString();
                        log.info("Incoming Request: [{}] {} with Body: {}", method, requestURI, requestBody);
                        activityLog.setRequestBody(requestBody);
                        rabbitMQService.publishLog(activityLog, "CREATE");
                        // Rewind the DataBuffer so it can be read again
                        DataBuffer newDataBuffer = dataBuffer.factory().wrap(dataBuffer.asByteBuffer());

                        // Create a decorated request with the cached body
                        ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(originalRequest) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return Flux.just(newDataBuffer);
                            }
                        };

                        // Proceed with the chain using the new request
                        return chain.filter(exchange.mutate().request(decoratedRequest).build());
                    });
        } else {
            log.info("Incoming Request: [{}] {}", method, requestURI);
            return chain.filter(exchange);
        }

    }




    @Override
    public int getOrder() {
        return -2;
    }
}