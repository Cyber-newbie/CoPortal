package paybank.astro.service.quiz;

import paybank.astro.dto.quiz.QuizRequest;
import paybank.astro.entity.Question;
import paybank.astro.entity.Quiz;
import paybank.astro.entity.User;

import java.util.List;

public interface QuizService {

    public Quiz createQuiz(QuizRequest quiz) throws Exception;

    public Quiz getQuiz(int id) throws Exception;

    public List<Quiz> getAllQuizes() throws Exception;

    public Quiz getQuizQuestions(String quizId) throws Exception;

    public Quiz updateQuiz(int quizId, QuizRequest request) throws Exception;

    public void deleteQuiz(int id) throws Exception;



//    public User getUser() throws Exception ;
}
