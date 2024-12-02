package paybank.astro.service.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paybank.astro.dto.submission.UserAnswers;
import paybank.astro.entity.Question;
import paybank.astro.entity.Quiz;
import paybank.astro.repository.QuestionRepository;
import paybank.astro.repository.QuizRepository;
import paybank.astro.service.quiz.QuizServiceImpl;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private QuestionRepository questionRepository;
    private QuizRepository quizRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public int checkQuestionAgainstUserAnswers(Quiz quiz, List<UserAnswers> userAnswers) {
        int totalQuizMarks = Integer.parseInt(quiz.getMaxMarks());
        int numberOfQuestions = Integer.parseInt(quiz.getNumberOfQuestions());
        int eachQuestionMark = Math.round((float) totalQuizMarks / numberOfQuestions);

        int totalScoreSecured = 0;
        // Iterate over questions and calculate the score
        for (Question question : quiz.getQuestions()) {
            UserAnswers userAnswer = userAnswers.stream()
                    .filter(answer -> answer.getQuestionId() == question.getId())
                    .findFirst()
                    .orElse(null);

            if (userAnswer != null && userAnswer.getAnswer().equals(question.getAnswer())) {
                totalScoreSecured += eachQuestionMark;
            }
        }

        return totalScoreSecured;
    }
}
