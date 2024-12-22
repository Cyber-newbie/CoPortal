package co.portal.gateway.config;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.service.RabbitMQService;
import co.portal.gateway.utils.JwtUtils;
import co.portal.gateway.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${allowedIPs.admin}")
    private List<String> allowedIPs;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route for quiz-service
                .route("quiz-service", r -> r.path("/quiz/admin/**")
//                                .filters(f -> f.filters(createJwtAuthorizationFilter(), APILogFilter()))
                        .filters(f -> f.filter(createJwtAuthorizationFilter("quiz-service", Arrays.asList("ADMIN", "INSTRUCTOR"))))
                        .uri("lb://QUIZ-SERVICE")
                )
                .route("quiz-service", r -> r.path("/category/admin/**")
                                .filters(f -> f.filter(createJwtAuthorizationFilter("quiz-service", Arrays.asList("ADMIN"))))
                                .uri("lb://QUIZ-SERVICE")
                )
                .route("quiz-service", r -> r.path("/quiz/user/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("quiz-service", Arrays.asList("USER"))))
                        .uri("lb://QUIZ-SERVICE")
                )
                // Route for question-service
                .route("question-service", r -> r.path("/question/**")
                        .filters(f -> f.filter(
                                createJwtAuthorizationFilter("question-service", Arrays.asList("ADMIN", "INSTRUCTOR"))))
                        .uri("lb://QUESTION-SERVICE")
                )
                // Route for submission-service
                .route("submission-service", r -> r.path("/submission/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("submission-service", Arrays.asList("USER"))))
                        .uri("lb://SUBMISSION-SERVICE")
                )
                .route("user-service", r -> r.path("/auth/admin/**")
                        .filters(f -> f.filters(createJwtAuthorizationFilter("user-service", Arrays.asList("ADMIN")),
                                adminIPAddressFilter()))
                        .uri("lb://USER-SERVICE")
                )
                // Route for user-service
                .route("user-service", r -> r.path("/auth/**")
                        .uri("lb://USER-SERVICE")
                )
                .build();
    }

    private GatewayFilter adminIPAddressFilter(){

        return (exchange, chain) -> {
                log.info("ADMIN IP ADDRESS GATEWAY FILTER {}", allowedIPs);
                String clientIPaddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress().toString();

                log.info("client ip : {}", clientIPaddress);
                if(allowedIPs.contains(clientIPaddress)){
                    return chain.filter(exchange);
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                }
                return exchange.getResponse().setComplete();
        };
    }

    private GatewayFilter createJwtAuthorizationFilter(String routeId, List<String> roles) {
            log.info("JWT filter invoked");
        return (exchange, chain) -> {
                String token;

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Authorization header missing or invalid");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                 return exchange.getResponse().setComplete();
            }

            token = authHeader.substring(7);


            if (jwtUtils.validateToken(token)) {
                    String username = jwtUtils.extractUsername(token);
                    List<String> userRoles = jwtUtils.extractRoles(token);

                    // Add username to the request headers
                    exchange.getRequest().mutate().header("loggedInUsername", username).build();

                    // Check if the user has the required roles for this route
                    if (hasRequiredRole(roles, userRoles)) {
                        return chain.filter(exchange);
                    } else {
                        log.info("required roles {}", roles);
                        log.info("user roles {}", userRoles);
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);  // Forbidden if roles do not match
                    }
                } else {
                    respondWithMessage(exchange, HttpStatus.BAD_REQUEST, "Invalid or expired token");  // Unauthorized if token is invalid
                }


            return exchange.getResponse().setComplete();  // End the request-response cycle
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