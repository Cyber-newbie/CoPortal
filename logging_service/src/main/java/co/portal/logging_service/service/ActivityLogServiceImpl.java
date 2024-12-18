package co.portal.logging_service.service;

import co.portal.logging_service.dto.LogRequest;
import co.portal.logging_service.entity.ActivityLogs;
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

@Service
public class ActivityLogServiceImpl implements ActivityLogService{

    private static final Logger log = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    @Override
    @RabbitListener(queues = {"db-queue"})
    public void StoreActivityLogToDB(@Payload  LogRequest message,
                                     @Header("action") String action) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            log.info("LOGGING SERVICE: {} message headers --> {}", message, action);
//            ActivityLogs activityLog = objectMapper.readValue(message.getBody(), ActivityLogs.class);
//            log.info("LOGGING SERVICE: deserialized message body --> {}", activityLog);
        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", e.getMessage(), e);
        }
    }




}
