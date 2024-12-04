//package co.portal.gateway.config;
//
//import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public JwtAuthorizationFilter jwtAuthorizationFilterFactory() {
//        return new JwtAuthorizationFilter();
//    }
//
//    @Bean
//    public RequestHeaderToRequestUri requestHeaderToRequestUri() {
//        return new RequestHeaderToRequestUri();
//    }
//}