package co.portal.gateway.utils;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.dto.RequestInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class Utils {
    private static final HttpStatus[] noResponseBodyStatusCodes = {
            HttpStatus.CONTINUE,
            HttpStatus.SWITCHING_PROTOCOLS,
            HttpStatus.PROCESSING,
            HttpStatus.TOO_EARLY,
            HttpStatus.CREATED,
            HttpStatus.NO_CONTENT,
            HttpStatus.RESET_CONTENT,
            HttpStatus.NOT_MODIFIED
    };
    private final HttpStatus[] responseBodyStatusCode = {
            HttpStatus.OK,
            HttpStatus.ACCEPTED,
            HttpStatus.PARTIAL_CONTENT,
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.CONFLICT,
            HttpStatus.UNPROCESSABLE_ENTITY,
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.NOT_IMPLEMENTED,
            HttpStatus.BAD_GATEWAY,
            HttpStatus.SERVICE_UNAVAILABLE,
            HttpStatus.GATEWAY_TIMEOUT
    };

    public Boolean containsResponseBody(HttpStatus status) {
        return Arrays.asList(responseBodyStatusCode).contains(status);
    }

    public Boolean hasNoResponseBody(HttpStatus status) {
        return Arrays.asList(noResponseBodyStatusCodes).contains(status);
    }

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
