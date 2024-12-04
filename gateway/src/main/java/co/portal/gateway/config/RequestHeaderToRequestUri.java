package co.portal.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RequestHeaderToRequestUri extends AbstractGatewayFilterFactory<> {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String role = exchange.getRequest().getHeaders().getFirst("role");

        if (role == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Role is missing"));
        }

        String path = exchange.getRequest().getURI().getPath();


        // Example of handling routing based on role
        if (path.startsWith("/user-service/") && "user".equals(role)) {
            // User is allowed to access user-service if they have 'user' role
            return chain.filter(exchange);
        } else {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions"));
        }

    }
}
