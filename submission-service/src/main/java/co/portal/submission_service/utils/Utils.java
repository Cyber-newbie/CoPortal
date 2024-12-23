package co.portal.submission_service.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Utils {

    public Boolean checkQuizDeadline(LocalDateTime deadline, LocalDateTime submissionTime ) {
        return deadline.isAfter(submissionTime) || deadline.equals(submissionTime);
    }

}
