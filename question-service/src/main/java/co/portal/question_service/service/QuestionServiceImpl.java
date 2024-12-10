package co.portal.question_service.service;

import co.portal.question_service.dto.QuizDTO;
import co.portal.question_service.dto.UserAnswers;
import co.portal.question_service.entity.Question;
import co.portal.question_service.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public int checkQuestionAgainstUserAnswers(QuizDTO quiz, List<UserAnswers> userAnswers) {
        int totalQuizMarks = Integer.parseInt(quiz.getMaxMarks());
        int numberOfQuestions = Integer.parseInt(quiz.getNumberOfQuestions());
        int eachQuestionMark = Math.round((float) totalQuizMarks / numberOfQuestions);

        //get quiz questions
        List<Question> quizQuestions = getQuizQuestions(quiz.getId());
        int totalScoreSecured = 0;
        // Iterate over questions and calculate the score
        for (Question question : quizQuestions) {
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

    @Override
    public List<Question> getQuizQuestions(int quizId) {
        return questionRepository.findByQuizId(quizId);
    }
}
