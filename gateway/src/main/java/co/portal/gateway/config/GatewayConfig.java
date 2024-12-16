package co.portal.gateway.config;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.service.RabbitMQService;
import co.portal.gateway.utils.JwtUtils;
import co.portal.gateway.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
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

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("POST GLOBAL FILTER!");

                        ActivityLog activityLog = ActivityLog.builder()
                                .responseBody(exchange.getResponse())
                                .statusCode(exchange.getResponse().getStatusCode().toString())
                                .build();

                        rabbitMQService.publishLog(activityLog, Utils.LogsActions.UPDATE.name());

                    }));
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



    private GatewayFilter APILogFilter(){
        return (ServerWebExchange exchange, GatewayFilterChain chain) -> {
            ActivityLog activityLog = ActivityLog.builder()
                    .requestURI(exchange.getRequest().getURI().toString())
                    .requestBody(exchange.getRequest().getBody())
                    .responseBody(exchange.getResponse())
                    .statusCode(exchange.getResponse().getStatusCode().toString())
                    .username(exchange.getRequest().getHeaders().getFirst("loggedInUsername"))
                    .build();

            log.info("SENDING ACTIVITY TO LOGGING SERVICE TO STORE IN DB {}", activityLog);
//
            rabbitMQService.publishLog(activityLog, Utils.LogsActions.CREATE.name());

            return chain.filter(exchange);
        };

    }

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