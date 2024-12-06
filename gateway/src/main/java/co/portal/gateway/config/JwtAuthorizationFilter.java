//package co.portal.gateway.config;
//
//
//import co.portal.gateway.utils.JwtUtils;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@NoArgsConstructor
//public class JwtAuthorizationFilter extends AbstractGatewayFilterFactory<JwtAuthorizationFilter.Config> {
//
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    @Getter
//    @Setter
//    public static class Config {
//        private List<String> roles;  // List of roles to be passed in the route configuration
//
//
//    }
//
//    // Constructor to initialize the filter with JwtUtils
//    public JwtAuthorizationFilter(JwtUtils jwtUtils) {
//        super(Config.class);
//        this.jwtUtils = jwtUtils;
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//            if (token != null && token.startsWith("Bearer ")) {
//                // Validate token
//                if (jwtUtils.validateToken(token)) {
//                    // Extract username and roles from the token
//                    String username = jwtUtils.extractUsername(token);
//                    List<String> roles = jwtUtils.extractRoles(token);  // Assuming you have this method in JwtUtils
//
//                    // Add username to the request headers
//                    exchange.getRequest().mutate().header("loggedInUsername", username).build();
//
//                    // Get the list of allowed roles from the config
//                    List<String> requiredRoles = config.getRoles();  // Get the list of roles from the config
//
//                    // Check if the user has any of the required roles
//                    if (hasRequiredRole(requiredRoles, roles)) {
//                        return chain.filter(exchange);
//                    } else {
//                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);  // Forbidden if the user doesn't have the required role
//                    }
//                } else {
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if token is invalid
//                }
//            } else {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // Unauthorized if no token is provided
//            }
//
//            return exchange.getResponse().setComplete();  // End the request-response cycle
//        };
//    }
//
//    // Method to check if the user has any of the required roles
//    private boolean hasRequiredRole(List<String> requiredRoles, List<String> userRoles) {
//        return userRoles.stream().anyMatch(userRole ->
//                requiredRoles.stream().anyMatch(requiredRole ->
//                        userRole.equals("ROLE_" + requiredRole)
//                )
//        );
//    }
//}