package co.portal.logging_service.service;

import co.portal.logging_service.dto.LogRequest;
import co.portal.logging_service.entity.ActivityLogs;
import co.portal.logging_service.repository.ActivityLogsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogServiceImpl implements ActivityLogService{

    private static final Logger log = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    @Autowired
    private ActivityLogsRepository activityLogsRepository;

    @Override
    @RabbitListener(queues = {"db-queue"})
    @Transactional
    public void StoreActivityLogToDB(@Payload  LogRequest message,
                                     @Header("action") String action) {

        log.info("LOGGING SERVICE: RECEIVED {} ", message);

        try {
              ActivityLogs logs = ActivityLogs.builder()
                      .requestBody(message.getRequestBody())
                      .responseBody(message.getResponseBody())
                      .username(message.getUsername())
                      .timestamp(message.getTimestamp())
                      .ipAddress(message.getIpAddress())
                      .requestURI(message.getRequestURI())
                      .statusCode(message.getStatusCode())
                      .build();
//
            log.info("LOGGING SERVICE: deserialized message body --> {}", logs);

            this.activityLogsRepository.save(logs);

        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", e.getMessage(), e);
        }
    }




}
