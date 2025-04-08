package co.portal.submission_service.services.calculation;

import co.portal.submission_service.dto.SubmissionQuizDTO;
import co.portal.submission_service.dto.user.UserDTO;
import co.portal.submission_service.entity.Analytics;
import co.portal.submission_service.entity.Quiz;
import org.springframework.beans.factory.annotation.Value;

public interface CalculationService {

    public Analytics calculate(SubmissionQuizDTO submissions, long userId);

}
