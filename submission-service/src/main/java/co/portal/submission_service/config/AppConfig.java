package co.portal.submission_service.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class AppConfig {

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

        log.info("configuring Unirest mapper");

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


    @Bean
    public RestTemplate restTemplate() {
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setReadTimeout(5000);
//        factory.setConnectTimeout(5000);

        return new RestTemplate();
    }
}
