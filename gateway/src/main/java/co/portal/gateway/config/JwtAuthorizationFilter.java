package co.portal.gateway.config;


import co.portal.gateway.utils.JwtUtils;
import io.jsonwebtoken.Jwt;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthorizationFilter extends AbstractGatewayFilterFactory<JwtAuthorizationFilter.Config> {

    @Getter
    @Setter
    public static class Config {
        private List<String> roles;

    }

    private JwtUtils jwtUtils;


    // Inject JwtDecoder to validate and decode the token
    public JwtAuthorizationFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().get("Authorization").get(0);

            if (exchange.getRequest().getHeaders().containsKey("Authorization") && token.startsWith("Bearer ")) {

                // Validate token
                if (jwtUtils.validateToken(token)) {
                    // Extract username and roles from the token
                    String username = jwtUtils.extractUsername(token);
                    List<String> roles = jwtUtils.extractRoles(token);  // Assuming you have this method in JwtUtils

                    // Add username to the request headers
                    exchange.getRequest().mutate().header("loggedInUsername", username);

                    // Get the list of allowed roles from the config
                    List<String> requiredRoles = config.getRoles();  // Get the list of roles from the config

                    // Check if the user has any of the required roles
                    if (hasRequiredRole(requiredRoles, roles)) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);  // Forbidden if the user doesn't have the required role
                    }
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if token is invalid
                }

            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if no token is provided
            }

            return exchange.getResponse().setComplete();
        };
    }

    // Method to check if the user has any of the required roles
    private boolean hasRequiredRole(List<String> requiredRoles, List<String> userRoles) {
        return userRoles.stream().anyMatch(userRole ->
                requiredRoles.stream().anyMatch(requiredRole ->
                        userRole.equals("ROLE_" + requiredRole)
                )
        );
    }

//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//        if (token == null || !token.startsWith("Bearer ")) {
//            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is missing or invalid"));
//        }
//
//        token = token.substring(7); // Remove "Bearer " part
//
//        try {
//            String role =  jwtUtils.extractRoles(token).get(0);// Decode and validate the token
//
//            // Set the roles into the request headers for routing decisions
//            exchange.getRequest().mutate().header("role", role).build();
//        } catch (Exception e) {
//            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT Token"));
//        }
//
//        return chain.filter(exchange);
//    }
}