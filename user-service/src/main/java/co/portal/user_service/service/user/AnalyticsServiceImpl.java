package co.portal.user_service.service.user;

import co.portal.user_service.dto.ResponseDTO;
import co.portal.user_service.dto.quiz.QuizDTO;
import co.portal.user_service.dto.user.UserDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService{

    private final ObjectMapper objectMapper;

    @Value("${service.url.quiz}")
    private String quizServiceURL;
    @Value("${service.url.submission}")
    private String submissionServiceURL;


    public AnalyticsServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public UserDTO getUserSubmissionAnalyticsById(long userId) {
        return null;
    }

    @Override
    public List<QuizDTO> getUserAttemptedQuizzes(long userId) {
        return Collections.emptyList();
    }

    public List<QuizDTO> getUserQuizzes(int quizId, String token) throws Exception {
        TypeReference<ResponseDTO<List<QuizDTO>>> quizRef = new TypeReference<ResponseDTO<List<QuizDTO>>>() {
        };

        try {
            HttpResponse<String> response = Unirest.get(this.quizServiceURL + "/user/" +quizId)
                    .getHttpRequest()
                    .header("Authorization", token)
                    .asString();

            return this.objectMapper.readValue(response.getBody(), quizRef).getData();

        } catch (Exception e){
            log.info("quiz get exception ", e);
            throw new Exception("Error occured while getting quiz: ", e);
        }
    }

}
