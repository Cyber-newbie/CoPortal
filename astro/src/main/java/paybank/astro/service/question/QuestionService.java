package paybank.astro.service.question;

import paybank.astro.dto.submission.UserAnswers;
import paybank.astro.entity.Question;
import paybank.astro.entity.Quiz;

import java.util.List;

public interface QuestionService {

    public int checkQuestionAgainstUserAnswers(Quiz quiz, List<UserAnswers> userAnswers);
}
