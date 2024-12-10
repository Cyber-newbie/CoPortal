package co.portal.quiz_service.service;


import co.portal.quiz_service.dto.QuizRequest;
import co.portal.quiz_service.entity.Quiz;

import java.util.List;

public interface QuizService {

    public Quiz createQuiz(QuizRequest quiz, String username) throws Exception;

    public Quiz getQuiz(int id) throws Exception;

    public List<Quiz> getAllQuizes() throws Exception;


    public Quiz updateQuiz(int quizId, QuizRequest request) throws Exception;

    public void deleteQuiz(int id) throws Exception;


//    public User getUser() throws Exception ;
}
