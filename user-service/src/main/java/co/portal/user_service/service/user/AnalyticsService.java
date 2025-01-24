package co.portal.user_service.service.user;

import co.portal.user_service.dto.quiz.QuizDTO;
import co.portal.user_service.dto.user.UserDTO;

import java.util.List;

public interface AnalyticsService {

    UserDTO getUserSubmissionAnalyticsById(long userId);

    List<QuizDTO> getUserAttemptedQuizzes(long userId);
}
