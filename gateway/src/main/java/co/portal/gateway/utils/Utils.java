package co.portal.gateway.utils;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.dto.RequestInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class Utils {

    public enum LogsActions {

        CREATE,
        UPDATE,

    }

    public ActivityLog getActivityLog(String responseBody, String statusCode){
        return ActivityLog.builder()
                .username(getUsernameFromResponse(responseBody))
                .timestamp(LocalDate.now())
                .responseBody(responseBody)
                .statusCode(statusCode)
                .build();
    }

    public RequestInfo getRequestInfo(ServerHttpRequest request){

        String requestURI = request.getURI().toString();
        String method = request.getMethodValue();
        String clientIp = Optional.of
                (request.getRemoteAddress().getAddress().getHostAddress().toString()).orElse("UNKNOWN");

        return RequestInfo.builder()
                .clientIp(clientIp)
                .method(method)
                .requestURI(requestURI)
                .build();
    }



    public String getUsernameFromResponse(String responseBody) {
         ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readTree(responseBody).path("user").path("username").asText();
        } catch (Exception e) {
            log.error("Error extracting username from response body", e);
            return null;
        }
    }

    public ActivityLog prepareActivityLog(ServerHttpRequest request,
                                          ServerHttpResponse response,
                                          String responseBody,
                                          String requestBody){

        RequestInfo requestInfo = getRequestInfo(request);



        String username = Optional.ofNullable(request.getHeaders().getFirst("loggedInUsername")).orElse("UNKNOWN") ;
        String statusCode = Optional.of(Objects.requireNonNull(response.getStatusCode()).toString()).orElse("UNKNOWN");

        return ActivityLog.builder()
                .username(username)
                .ipAddress(requestInfo.getClientIp())
                .requestURI(requestInfo.getRequestURI())
                .statusCode(statusCode)
                .timestamp(LocalDate.now())
                .responseBody(responseBody)
                .requestBody(requestBody)
                .build();
    }

}
