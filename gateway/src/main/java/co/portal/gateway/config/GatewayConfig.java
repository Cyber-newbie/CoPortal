package co.portal.gateway.config;

import co.portal.gateway.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
public class GatewayConfig {

//    @Autowired
//    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route for quiz-service
                .route("quiz-service", r -> r.path("/quiz/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("quiz-service", Arrays.asList("ADMIN", "INSTRUCTOR"))))
                        .uri("lb://quiz-service")
                )
                // Route for question-service
                .route("question-service", r -> r.path("/question/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("question-service", Arrays.asList("ADMIN"))))
                        .uri("lb://question-service")
                )
                // Route for submission-service
                .route("submission-service", r -> r.path("/submission/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("submission-service", Arrays.asList("USER"))))
                        .uri("lb://submission-service")
                )
                .route("user-service", r -> r.path("/auth/admin/**")
                        .filters(f -> f.filter(createJwtAuthorizationFilter("user-service", Arrays.asList("ADMIN"))))
                        .uri("lb://submission-service")
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
            log.info("Incoming request: {} {}", exchange.getRequest().getMethod().name(), exchange.getRequest().getPath());
            log.info("token: {}", token);
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
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if token is invalid
                }
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if no token is present
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
}