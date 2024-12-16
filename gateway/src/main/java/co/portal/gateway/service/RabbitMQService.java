package co.portal.gateway.service;

import co.portal.gateway.dto.ActivityLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishLog(ActivityLog payload, String action)  {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend("db-exchange", "db", jsonPayload, message -> {
                message.getMessageProperties().getHeaders().put("action", action);
                return message;
            });

            log.info("SENDING DATA TO LOGGING SERVICE: {}", jsonPayload);
        } catch (Exception e) {
            log.info("Could not send message to queue {}", e.getMessage());
        }
    }
}
