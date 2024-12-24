package co.portal.question_service.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class UnirestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper() {

            private final com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();


            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    jacksonObjectMapper.registerModule(new JavaTimeModule());
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    jacksonObjectMapper.registerModule(new JavaTimeModule());
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        };

        return mapper;
    }

    @PostConstruct
    public void configureUnirest() {
        Unirest.setObjectMapper(new ObjectMapper() {

            private final com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();


            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    jacksonObjectMapper.registerModule(new JavaTimeModule());
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    jacksonObjectMapper.registerModule(new JavaTimeModule());
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
}

    // Helper method to extract the "Authorization" header from the current request context
    private String getAuthorizationHeader() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null) {
                return authorizationHeader; // Return the Bearer token
            }
        }
        return null; // Return null if no header is present
    }

    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }
}
