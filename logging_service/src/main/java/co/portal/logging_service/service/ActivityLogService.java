package co.portal.logging_service.service;

import co.portal.logging_service.dto.LogRequest;
import co.portal.logging_service.entity.ActivityLogs;
import org.springframework.amqp.core.Message;

public interface ActivityLogService {

    public void StoreActivityLogToDB(LogRequest message, String action);

}
