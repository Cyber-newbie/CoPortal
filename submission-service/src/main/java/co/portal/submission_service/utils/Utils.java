package co.portal.submission_service.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class Utils {

    public int extractTimeInSeconds(LocalTime time){

        int hours = time.getHour();
        int minutes = time.getMinute();
        int seconds = time.getSecond();
        return hours * 3600 + minutes * 60 + seconds;

    }

    public Boolean checkQuizDeadline(LocalDateTime deadline, LocalDateTime submissionTime ) {

        return deadline.isAfter(submissionTime) || deadline.equals(submissionTime);
    }

    public String getAuthorizationHeader() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return request.getHeader("Authorization");
        }
        return null;
    }

}
