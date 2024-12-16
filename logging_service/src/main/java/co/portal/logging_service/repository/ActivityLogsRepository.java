package co.portal.logging_service.repository;

import co.portal.logging_service.entity.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Integer> {
}
