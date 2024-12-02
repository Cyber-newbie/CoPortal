package paybank.astro.util.quiz;

import org.springframework.stereotype.Service;
import paybank.astro.entity.Quiz;

import java.time.LocalDateTime;

@Service
public class QuizUtils {

    public Boolean checkQuizDeadline(LocalDateTime deadline, LocalDateTime submissionTime ) {
        return deadline.isAfter(submissionTime) || deadline.equals(submissionTime);
    }

}
