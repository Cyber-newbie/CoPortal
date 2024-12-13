package co.portal.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestInterceptorConfig implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println("Incoming request: " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI());

        // Proceed with the filter chain
        return chain.filter(exchange).doOnSuccess(aVoid -> {
            System.out.println("Response status: " + exchange.getResponse().getStatusCode());
        });
    }

}