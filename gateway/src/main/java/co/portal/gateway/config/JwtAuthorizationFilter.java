package co.portal.gateway.config;


import co.portal.gateway.utils.JwtUtils;
import io.jsonwebtoken.Jwt;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthorizationFilter extends AbstractGatewayFilterFactory<JwtAuthorizationFilter.Config> {

    public static class Config {}

    private JwtUtils jwtUtils;


    // Inject JwtDecoder to validate and decode the token
    public JwtAuthorizationFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return null;
    }



    public JwtAuthorizationFilter(){}
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