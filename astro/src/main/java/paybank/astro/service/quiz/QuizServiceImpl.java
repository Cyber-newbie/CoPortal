package paybank.astro.service.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paybank.astro.dto.question.QuestionRequest;
import paybank.astro.dto.quiz.QuizRequest;
import paybank.astro.entity.Category;
import paybank.astro.entity.Question;
import paybank.astro.entity.Quiz;
import paybank.astro.entity.User;
import paybank.astro.repository.CategoryRepository;
import paybank.astro.repository.QuizRepository;
import paybank.astro.repository.UserRepository;
import paybank.astro.service.BaseService;
import paybank.astro.util.quiz.QuizUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizServiceImpl extends BaseService implements QuizService  {

    private QuizRepository quizRepository;
    private CategoryRepository categoryRepository;
    private QuizUtils quizUtils;


    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository, UserRepository userRepository,
                           CategoryRepository categoryRepository, QuizUtils quizUtils ) {
        super(userRepository);
        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
        this.quizUtils = quizUtils;
    }

    @Override
    @Transactional
    public Quiz createQuiz(QuizRequest quiz) throws Exception {

        List<Question> questionsList = new ArrayList<>();
        Quiz newQuiz = new Quiz();
        User user = this.getLoggedInUser();

        newQuiz.setTitle(quiz.getTitle());
        newQuiz.setDescription(quiz.getDescription());
        newQuiz.setMaxMarks(quiz.getMaxMarks());
        newQuiz.setNumberOfQuestions(quiz.getNumberOfQuestions());
        newQuiz.setActive(quiz.getActive());
        newQuiz.setUser(user);

        //get and set category
        Optional<Category> category = this.categoryRepository.findById(quiz.getCategoryId());
        category.ifPresent(newQuiz::setCategory);
        //create questions
        for(QuestionRequest ques : quiz.getQuestions()){
            Question newQuestion = new Question();

            newQuestion.setQuiz(newQuiz);
            newQuestion.setQuestion(ques.getQuestion());
            newQuestion.setAnswer(ques.getAnswer());

            newQuestion.setOption1(ques.getOption1());
            newQuestion.setOption2(ques.getOption2());
            newQuestion.setOption3(ques.getOption3());
            newQuestion.setOption4(ques.getOption4());

            questionsList.add(newQuestion);
        }

        //add questionList to quiz
        newQuiz.setQuestions(questionsList);

        quizRepository.save(newQuiz);

        return newQuiz;
    }

    @Override
    public Quiz getQuiz(int id) throws Exception {
        return this.quizRepository.findById(id).orElseThrow(() -> new Exception("Quiz not found"));
    }

    @Override
    public List<Quiz> getAllQuizes() throws Exception {
        return this.quizRepository.findAllWithSubmissions();
    }

    @Override
    public Quiz getQuizQuestions(String quizId) throws Exception {
        return this.quizRepository.getQuizQuestions(Integer.parseInt(quizId));
    }

    @Override
    public Quiz updateQuiz(int quizId, QuizRequest request) throws Exception {

        List<Question> questionsList = new ArrayList<>();

        Quiz quiz = this.getQuiz(quizId);
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setMaxMarks(request.getMaxMarks());
        quiz.setNumberOfQuestions(request.getNumberOfQuestions());
        quiz.setActive(request.getActive());

        //create questions
        for(QuestionRequest ques : request.getQuestions()){
            Question newQuestion = new Question();

            newQuestion.setQuiz(quiz);
            newQuestion.setQuestion(ques.getQuestion());
            newQuestion.setAnswer(ques.getAnswer());

            newQuestion.setOption1(ques.getOption1());
            newQuestion.setOption2(ques.getOption2());
            newQuestion.setOption3(ques.getOption3());
            newQuestion.setOption4(ques.getOption4());

            questionsList.add(newQuestion);
        }

        quiz.setQuestions(questionsList);



        return null;
    }

    @Override
    public void deleteQuiz(int id) throws Exception {

    }




//
//    @Override
//    public User getUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            String username =  ((UserDetails) authentication.getPrincipal()).getUsername();
//            return this.userRepository.findByUsername(username);
//        }
//        throw new IllegalStateException("No logged-in user found");
//    }

}
