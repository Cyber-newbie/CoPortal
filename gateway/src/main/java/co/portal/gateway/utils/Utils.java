package co.portal.gateway.utils;

import co.portal.gateway.dto.ActivityLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

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

    public String getUsernameFromResponse(String responseBody) {
         ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readTree(responseBody).path("user").path("username").asText();
        } catch (Exception e) {
            log.error("Error extracting username from response body", e);
            return null;
        }
    }


}
