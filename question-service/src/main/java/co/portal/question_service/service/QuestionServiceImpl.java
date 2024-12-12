package co.portal.question_service.service;

import co.portal.question_service.dto.QuestionRequest;
import co.portal.question_service.dto.QuizDTO;
import co.portal.question_service.dto.Response;
import co.portal.question_service.dto.UserAnswers;
import co.portal.question_service.entity.Question;
import co.portal.question_service.entity.QuizQuestion;
import co.portal.question_service.entity.QuizQuestionId;
import co.portal.question_service.exception.QuestionSaveException;
import co.portal.question_service.repository.QuestionRepository;
import co.portal.question_service.repository.QuizQuestionRepository;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class QuestionServiceImpl implements QuestionService {

    private QuestionRepository questionRepository;
    private QuizQuestionRepository quizQuestionRepository;
    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, QuizQuestionRepository quizQuestionRepository)
    {
        this.questionRepository = questionRepository;
        this.quizQuestionRepository = quizQuestionRepository;
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
    public Response createQuizQuestions(List<QuestionRequest> request, Integer quizId) throws Exception {
        // Create lists to hold new questions and existing question IDs
        List<Question> questionsList = new ArrayList<>();
        List<Integer> existingQuestionIds = new ArrayList<>();

        for (QuestionRequest ques : request) {

                Optional<Question> question = this.questionRepository.findById(ques.getQuestionId());
                if (question.isPresent()) {
                    // Add the existing question's ID
                    existingQuestionIds.add(question.get().getId());
                } else {
                    // If the question ID does not exist, create a new question
                    Question newQuestion = Question.builder()
                            .question(ques.getQuestion())
                            .answer(ques.getAnswer())
                            .image(ques.getImage())
                            .option1(ques.getOption1())
                            .option2(ques.getOption2())
                            .option3(ques.getOption3())
                            .option4(ques.getOption4())
                            .build();
                    questionsList.add(newQuestion);
                }

        }

        try {
            // Save new questions and get the saved entities with IDs
            List<Question> savedQuestions = questionRepository.saveAll(questionsList);

            // Collect IDs of newly saved questions
            List<Integer> newQuestionIds = savedQuestions.stream()
                    .map(Question::getId)
                    .collect(Collectors.toList());

            // Combine all question IDs (existing + new)
            existingQuestionIds.addAll(newQuestionIds);

            // Create QuizQuestion entries with all question IDs
            List<QuizQuestion> quizQuestionsList = existingQuestionIds.stream()
                    .map(questionId -> new QuizQuestion(quizId, questionId))
                    .collect(Collectors.toList());

            // Save the quiz-question associations
            quizQuestionRepository.saveAll(quizQuestionsList);

            return new Response(savedQuestions);
        } catch (Exception e) {
            throw new QuestionSaveException("Failed to save questions", e);
        }
    }


    @Override
    public List<Question> getQuizQuestions(int quizId) {
        return null;
//                questionRepository.findByQuizId(quizId);

    }
}
