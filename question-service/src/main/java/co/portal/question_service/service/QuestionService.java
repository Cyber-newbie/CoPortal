package co.portal.question_service.service;


import co.portal.question_service.dto.QuestionRequest;
import co.portal.question_service.dto.QuizDTO;
import co.portal.question_service.dto.Response;
import co.portal.question_service.dto.UserAnswers;
import co.portal.question_service.entity.Question;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.List;

public interface QuestionService {

    public Response<QuizDTO> getQuizInfo(Integer quizId, String token) throws Exception;

    public int checkQuestionAgainstUserAnswers(int quizId, List<UserAnswers> userAnswers, String token) throws Exception;

    public List<Question> createQuizQuestions(List<QuestionRequest> request, Integer quizId) throws Exception;

    public List<Question> getQuizQuestions(int quizId);
}
