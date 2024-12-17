package co.portal.gateway.config;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.service.RabbitMQService;
import co.portal.gateway.utils.JwtUtils;
import co.portal.gateway.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Configuration
@Slf4j
public class GatewayConfig {

//    @Autowired
//    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RabbitMQService rabbitMQService;

//    @Bean
//    public GlobalFilter postGlobalFilter() {
//        return (exchange, chain) -> {
//
//
//            ServerHttpResponse originalResponse = exchange.getResponse();
//
//            // log the response body
//            log.info("POST Filter and response status: {}", originalResponse.getStatusCode());
//            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
//                @Override
//                public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
//                    log.info("Writingggg with");
//                    return Utils.captureResponseBody(body)
//                            .flatMap(responseBody -> {
//                                log.info("Captured Response Body: {}", responseBody);
//
//                                HttpStatus statusCode = exchange.getResponse().getStatusCode();
//
//                                // Build and send the ActivityLog to RabbitMQ
//                                ActivityLog activityLog = ActivityLog.builder()
//                                        .responseBody(responseBody)
//                                        .statusCode(statusCode != null ? statusCode.name() : "UNKNOWN")
//                                        .build();
//
//                                rabbitMQService.publishLog(activityLog, Utils.LogsActions.UPDATE.name());
//
//                                // Write the response body back to the client
//                                byte[] content = responseBody.getBytes(StandardCharsets.UTF_8);
//                                return super.writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(content)));
//                            })
//                            .onErrorResume(e -> {
//                                log.error("Error capturing response body", e);
//                                return super.writeWith(body);
//                            });
//                }
//
//                @Override
//                public Mono<Void> writeAndFlushWith(org.reactivestreams.Publisher<? extends Publisher<? extends DataBuffer>> body) {
//                    log.info("Writingggg flush");
//                    return Mono.from(body) // Unwrap the outer Publisher
//                            .flatMapMany(dataBufferPublisher ->
//                                    Utils.captureResponseBody(dataBufferPublisher)
//                                            .flatMap(responseBody -> {
//                                                log.info("Captured Response Body: {}", responseBody);
//
//                                                HttpStatus statusCode = exchange.getResponse().getStatusCode();
//
//                                                // Build and send the ActivityLog to RabbitMQ
//                                                ActivityLog activityLog = ActivityLog.builder()
//                                                        .responseBody(responseBody)
//                                                        .statusCode(statusCode != null ? statusCode.name() : "UNKNOWN")
//                                                        .build();
//
//                                                rabbitMQService.publishLog(activityLog, Utils.LogsActions.UPDATE.name());
//
//                                                // Convert the modified response body to DataBuffer
//                                                byte[] content = responseBody.getBytes(StandardCharsets.UTF_8);
//                                                DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(content);
//                                                return Mono.just(dataBuffer);
//                                            })
//                                            .onErrorResume(e -> {
//                                                log.error("Error capturing response body", e);
//                                                return Mono.from(dataBufferPublisher); // Fallback to the original body
//                                            })
//                            )
//                            .map(Mono::just) // Wrap each DataBuffer into a Publisher
//                            .as(super::writeAndFlushWith); // Pass to parent writeAndFlushWith
//                }
//            };
//            log.info("POST Filter ENDED");
//            return chain.filter(exchange.mutate().response(decoratedResponse).build());
//        };
//    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {

            ServerHttpResponse originalResponse = exchange.getResponse();

            // Log the initial response status
            log.info("POST Filter and response status: {}", originalResponse.getStatusCode());

            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                    log.info("Entering writeWith method");

                    if (body == null) {
                        log.warn("Response body is null");
                        return super.writeWith(Mono.empty());
                    }

                    return Utils.captureResponseBody(body)
                            .flatMap(responseBody -> {
                                log.info("Captured Response Body: {}", responseBody);

                                HttpStatus statusCode = exchange.getResponse().getStatusCode();

                                // Build and send the ActivityLog to RabbitMQ
                                ActivityLog activityLog = ActivityLog.builder()
                                        .responseBody(responseBody)
                                        .statusCode(statusCode != null ? statusCode.name() : "UNKNOWN")
                                        .build();

                                rabbitMQService.publishLog(activityLog, Utils.LogsActions.UPDATE.name());

                                // Write the response body back to the client
                                byte[] content = responseBody.getBytes(StandardCharsets.UTF_8);
                                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(content);
                                return super.writeWith(Mono.just(buffer));
                            })
                            .onErrorResume(e -> {
                                log.error("Error capturing response body", e);
                                return super.writeWith(body);
                            });
                }

                @Override
                public Mono<Void> writeAndFlushWith(org.reactivestreams.Publisher<? extends org.reactivestreams.Publisher<? extends DataBuffer>> body) {
                    log.info("Entering writeAndFlushWith method");

                    if (body == null) {
                        log.warn("Response body is null in writeAndFlushWith");
                        return super.writeAndFlushWith(Mono.empty());
                    }

                    return Mono.from(body)
                            .flatMapMany(dataBufferPublisher ->
                                    Utils.captureResponseBody(dataBufferPublisher)
                                            .flatMap(responseBody -> {
                                                log.info("Captured Response Body in writeAndFlushWith: {}", responseBody);

                                                HttpStatus statusCode = exchange.getResponse().getStatusCode();

                                                // Build and send the ActivityLog to RabbitMQ
                                                ActivityLog activityLog = ActivityLog.builder()
                                                        .responseBody(responseBody)
                                                        .statusCode(statusCode != null ? statusCode.name() : "UNKNOWN")
                                                        .build();

                                                rabbitMQService.publishLog(activityLog, Utils.LogsActions.UPDATE.name());

                                                byte[] content = responseBody.getBytes(StandardCharsets.UTF_8);
                                                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(content);
                                                return Mono.just(buffer);
                                            })
                                            .onErrorResume(e -> {
                                                log.error("Error capturing response body in writeAndFlushWith", e);
                                                return Mono.from(dataBufferPublisher);
                                            })
                            )
                            .map(Mono::just)
                            .as(super::writeAndFlushWith);
                }
            };

            log.info("POST Filter ENDED");

            return chain.filter(exchange.mutate().response(decoratedResponse).build())
                    .doOnSuccess(aVoid -> log.info("Filter chain completed successfully"))
                    .doOnError(error -> log.error("Error in filter chain", error));
        };
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route for quiz-service
                .route("quiz-service", r -> r.path("/quiz/admin/**")
//                                .filters(f -> f.filters(createJwtAuthorizationFilter(), APILogFilter()))
                        .filters(f -> f.filter(createJwtAuthorizationFilter("quiz-service", Collections.singletonList("ADMIN"))))
                        .uri("lb://QUIZ-SERVICE")
                )
                .route("quiz-service", r -> r.path("/quiz/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("quiz-service", Arrays.asList("USER"))))
                        .uri("lb://QUIZ-SERVICE")
                )
                // Route for question-service
                .route("question-service", r -> r.path("/question/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("question-service", Arrays.asList("ADMIN", "INSTRUCTOR"))))
                        .uri("lb://QUESTION-SERVICE")
                )
                // Route for submission-service
                .route("submission-service", r -> r.path("/submission/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("submission-service", Arrays.asList("USER"))))
                        .uri("lb://SUBMISSION-SERVICE")
                )
                .route("user-service", r -> r.path("/auth/admin/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("user-service", Arrays.asList("ADMIN"))))
                        .uri("lb://USER-SERVICE")
                )
                // Route for user-service
                .route("user-service", r -> r.path("/auth/**")
                        .uri("lb://USER-SERVICE")
                )
                .build();
    }

    private GatewayFilter createJwtAuthorizationFilter(String routeId, List<String> roles) {
        return (exchange, chain) -> {
            String token = Objects.requireNonNull(exchange.getRequest().
                    getHeaders().getFirst("Authorization")).substring(7);
            if (token != null) {
                if (jwtUtils.validateToken(token)) {
                    String username = jwtUtils.extractUsername(token);
                    List<String> userRoles = jwtUtils.extractRoles(token);

                    // Add username to the request headers
                    exchange.getRequest().mutate().header("loggedInUsername", username).build();

                    // Check if the user has the required roles for this route
                    if (hasRequiredRole(roles, userRoles)) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);  // Forbidden if roles do not match
                    }
                } else {
                    respondWithMessage(exchange, HttpStatus.BAD_REQUEST, "Invalid or expired token");  // Unauthorized if token is invalid
                }
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if no token is present
            }

            return exchange.getResponse().setComplete();  // End the request-response cycle
        };
    }

    private ServerHttpResponseDecorator getDecoratedResponse(String path, ServerHttpResponse response,  DataBufferFactory dataBufferFactory) {
        return new ServerHttpResponseDecorator(response) {

            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {

                if (body instanceof Flux) {

                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                        DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffers.readableByteCount()];
                        joinedBuffers.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);//MODIFY RESPONSE and Return the Modified response
                        log.info("response body: {}", responseBody);

                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })).onErrorResume(err -> {

                        log.error("error while decorating Response: {}",err.getMessage());
                        return Mono.empty();
                    });

                }
                return super.writeWith(body);
            }
        };
    }

//    private GatewayFilter APILogFilter(){
//        return (ServerWebExchange exchange, GatewayFilterChain chain) -> {
//            ActivityLog activityLog = ActivityLog.builder()
//                    .requestURI(exchange.getRequest().getURI().toString())
//                    .requestBody(exchange.getRequest().getBody())
//                    .responseBody(exchange.getResponse())
//                    .statusCode(exchange.getResponse().getStatusCode().toString())
//                    .username(exchange.getRequest().getHeaders().getFirst("loggedInUsername"))
//                    .build();
//
//            log.info("SENDING ACTIVITY TO LOGGING SERVICE TO STORE IN DB {}", activityLog);
////
//            rabbitMQService.publishLog(activityLog, Utils.LogsActions.CREATE.name());
//
//            return chain.filter(exchange);
//        };
//
//    }

    private boolean hasRequiredRole(List<String> requiredRoles, List<String> userRoles) {
        return userRoles.stream().anyMatch(userRole ->
                requiredRoles.stream().anyMatch(requiredRole ->
                        userRole.equals("ROLE_" + requiredRole)
                )
        );
    }

    private Mono<Void> respondWithMessage(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}