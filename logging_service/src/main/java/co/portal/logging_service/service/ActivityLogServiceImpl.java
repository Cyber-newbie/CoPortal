package co.portal.logging_service.service;

import co.portal.logging_service.entity.ActivityLogs;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogServiceImpl implements ActivityLogService{

    private static final Logger log = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    @Override
    public void StoreActivityLogToDB(Message message) {
        log.info("LOGGING SERVICE: message headers--> {}", message.getMessageProperties());
        log.info("LOGGING SERVICE: message body-->{}", message.getBody());
    }




}
