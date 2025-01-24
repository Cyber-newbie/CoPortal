package co.portal.submission_service.aspect;


import co.portal.submission_service.aspect.annotation.GetUserObject;
import co.portal.submission_service.dto.user.UserDTO;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Aspect
public class SubmissionAspect {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Around("@annotation(co.portal.submission_service.aspect.annotation.GetUserObject)")
    public Object injectUserObject(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("submission aspect ran!");

        HttpServletRequest request = ((ServletRequestAttributes) Objects.
                requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();

        UserDTO user = getLoggedInUser(request);
        Signature sig = joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();


        Object[] userInjected = Arrays.stream(args)
                .map(arg -> (arg instanceof UserDTO || arg == null) ? user : arg)
                .toArray();

        log.info("args user injected {}", Arrays.toString(args));

        return joinPoint.proceed(userInjected);
    }

    public UserDTO getLoggedInUser(HttpServletRequest request) {
        String username =  request.getHeader("LoggedInUsername");

        try {
            HttpResponse<UserDTO> user = Unirest.get(userServiceUrl + "/" + username).asObject(UserDTO.class);
            log.info("LOGGED IN USER {}" ,  user.getBody());
            return user.getBody();
        } catch (RestClientException | UnirestException e) {
            throw new RuntimeException(e);
        }
    }


}
