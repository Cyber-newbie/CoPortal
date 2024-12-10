package co.portal.quiz_service.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuizUtils {

    public Boolean checkQuizDeadline(LocalDateTime deadline, LocalDateTime submissionTime ) {
        return deadline.isAfter(submissionTime) || deadline.equals(submissionTime);
    }

}
