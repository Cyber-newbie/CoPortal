package co.portal.quiz_service.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthorizationInterceptor implements ClientHttpRequestInterceptor {

    private final HttpServletRequest request;

    public AuthorizationInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public ClientHttpResponse intercept
            (HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
        }

        return execution.execute(httpRequest, body);
    }
}
