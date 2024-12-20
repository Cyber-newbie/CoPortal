package co.portal.gateway.config;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.dto.RequestInfo;
import co.portal.gateway.service.RabbitMQService;
import co.portal.gateway.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class RequestInterceptFilter implements GlobalFilter, Ordered {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private Utils utils;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        log.info("GL0BAL FILTER INVOKED");

        ServerHttpRequest originalRequest = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();
        AtomicReference<ServerHttpRequest> decoratedRequest = new AtomicReference<>(originalRequest);
        ActivityLog activityLog = new ActivityLog();
        RequestInfo requestInfo = utils.getRequestInfo(originalRequest);

        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        //not capturing request if it's a GET
        if(requestInfo.getMethod().matches("GET")){
            ServerHttpResponseDecorator decoratedResponse = responseDecorator(originalResponse, originalRequest, null);

            // Proceed with the chain using the decorated request and response
            return chain.filter(exchange.mutate()
                    .request(decoratedRequest.get())
                    .response(decoratedResponse)
                    .build());
        } else {

        // Capture and log the request body
        return DataBufferUtils.join(originalRequest.getBody())
                .flatMap(requestDataBuffer -> {
                    String requestBody = StandardCharsets.UTF_8.decode(requestDataBuffer.asByteBuffer()).toString().trim();
                    log.info("Incoming Request: [{}] {} with Body: {}", requestInfo.getMethod(), requestInfo.getRequestURI(), requestBody);

                    // Create and send ActivityLog for request

                    activityLog.setRequestBody(requestBody);
//                    rabbitMQService.publishLog(activityLog, "request");

                    // Recreate the DataBuffer for the request body
                    decoratedRequest.set(new ServerHttpRequestDecorator(originalRequest) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(bufferFactory.wrap(requestBody.getBytes(StandardCharsets.UTF_8)));
                        }
                    });


                    // Capture and log the response body
                    ServerHttpResponseDecorator decoratedResponse = responseDecorator(originalResponse, originalRequest, requestBody);

                    // Proceed with the chain using the decorated request and response
                    return chain.filter(exchange.mutate()
                            .request(decoratedRequest.get())
                            .response(decoratedResponse)
                            .build());
                });

        }

    }


    //decorates response body to capture it and also receives string request body and log to publish via rabbitmq
    private ServerHttpResponseDecorator responseDecorator(ServerHttpResponse originalResponse,
                                                          ServerHttpRequest originalRequest,
                                                          String requestBody ){
        return new ServerHttpResponseDecorator(originalResponse) {
            final DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return super.writeWith(Flux.from(body).flatMap(responseDataBuffer -> {
                    String responseBody = StandardCharsets.UTF_8
                            .decode(responseDataBuffer.asByteBuffer())
                            .toString().trim();
                    log.info("Outgoing Response: Status Code [{}], Body: {}", originalResponse.getStatusCode(), responseBody);

                    // Create and send ActivityLog for response
                    ActivityLog activityLog = utils.prepareActivityLog(originalRequest, originalResponse, responseBody, requestBody);

                    rabbitMQService.publishLog(activityLog, "CREATE");

                    // Recreate the DataBuffer for the response body
                    DataBuffer newResponseBuffer = bufferFactory.wrap(responseBody.getBytes(StandardCharsets.UTF_8));
                    return Flux.just(newResponseBuffer);
                }));
            }
        };
    }

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        ServerHttpRequest originalRequest =  exchange.getRequest();
//        RequestInfo requestInfo = utils.getRequestInfo(originalRequest);
//        ActivityLog activityLog = new ActivityLog();
//
//        // Create and send the ActivityLog to the logging service
//
////        rabbitMQService.publishLog(activityLog, "CREATE");
//
//        if (requestInfo.getMethod().equals(HttpMethod.POST.name()) || requestInfo.getMethod().equals(HttpMethod.PUT.name()) || requestInfo.getMethod().equals(HttpMethod.PATCH.name())) {
//            return DataBufferUtils.join(originalRequest.getBody())
//                    .flatMap(dataBuffer -> {
//                        // Convert DataBuffer to String
//                        String requestBody = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString().trim();
//                        log.info("Incoming Request: [{}] {} from IP: {} with body: {}", requestInfo.getMethod(), requestInfo.getRequestURI(), requestInfo.getClientIp(), requestBody);
//
//                        activityLog.setRequestBody(requestBody);
//                        activityLog.setTimestamp(LocalDate.now());
//                        activityLog.setIpAddress(requestInfo.getClientIp());
//                        activityLog.setRequestURI(requestInfo.getRequestURI());
//
////                        rabbitMQService.publishLog(activityLog, "CREATE");
//                        // Rewind the DataBuffer so it can be read again
//                        DataBuffer newDataBuffer = dataBuffer.factory().wrap(dataBuffer.asByteBuffer());
//
//                        // Create a decorated request with the cached body
//                        ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(originalRequest) {
//                            @Override
//                            public Flux<DataBuffer> getBody() {
//                                return Flux.just(newDataBuffer);
//                            }
//                        };
//
//                        // Proceed with the chain using the new request
//                        return chain.filter(exchange.mutate().request(decoratedRequest).build()).then(
//                                Mono.fromRunnable(() -> logResponse(exchange, activityLog))
//                        );
//                    });
//        } else {
//            log.info("Incoming Request: [{}] {}", requestInfo.getMethod(), requestInfo.getRequestURI());
//            return chain.filter(exchange).then(
//                    Mono.fromRunnable(() -> logResponse(exchange, activityLog))
//            );
//        }
//
//    }


    private void logResponse(ServerWebExchange exchange, ActivityLog activityLog){

        log.info("GATEWAY RESPONSE FILTER");

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        // Create a decorated response to intercept and log the body
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                // Convert Publisher to Flux to use reactive operators
                return super.writeWith(Flux.from(body).flatMap(dataBuffer -> {
                    // Convert the DataBuffer to a String
                    String responseBody = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString();
                    String statusCode = originalResponse.getStatusCode() != null ?
                            originalResponse.getStatusCode().toString() : null;

                    log.info("Response Body: {}", responseBody);
                    activityLog.setResponseBody(responseBody);
                    activityLog.setStatusCode(statusCode);
                    activityLog.setUsername(utils.getUsernameFromResponse(responseBody));


                    rabbitMQService.publishLog(activityLog, "CREATE");

                    // Re-create a DataBuffer with the same content to forward to the client
                    DataBuffer newBuffer = bufferFactory.wrap(responseBody.getBytes(StandardCharsets.UTF_8));
                    return Flux.just(newBuffer);
                }));
            }
        };

        // Proceed with the chain using the decorated response
         exchange.mutate().response(decoratedResponse).build();
    }



    @Override
    public int getOrder() {
        return -1;
    }
}