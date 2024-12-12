package co.portal.question_service.service;


import co.portal.question_service.dto.QuestionRequest;
import co.portal.question_service.dto.QuizDTO;
import co.portal.question_service.dto.Response;
import co.portal.question_service.dto.UserAnswers;
import co.portal.question_service.entity.Question;

import java.util.List;

public interface QuestionService {

    public int checkQuestionAgainstUserAnswers(QuizDTO quiz, List<UserAnswers> userAnswers);

    public Response createQuizQuestions(List<QuestionRequest> request, Integer quizId) throws Exception;

    public List<Question> getQuizQuestions(int quizId);
}
