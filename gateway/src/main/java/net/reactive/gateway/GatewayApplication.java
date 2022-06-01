package net.reactive.gateway;

import lombok.extern.slf4j.Slf4j;
import net.reactive.gateway.client.CrmClient;
import net.reactive.gateway.dto.CustomerOrders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@Slf4j
public class GatewayApplication {

    @Value("${customer.host}")
    private String customerHost;
    @Value("${customer.port}")
    private Integer customerPort;
    @Value("${order.host}")
    private String orderHost;
    @Value("${order.port}")
    private Integer orderPort;


    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder builder) {
        return builder.routes().route(
                routeSpec ->
                        routeSpec.path("/proxy").and().host("*.spring.io") //predicato
                                .filters(rs -> rs
                                        .setPath("/customer")
                                        .filter(((exchange, chain) -> {
                                            log.info("Before");
                                            return chain.filter(exchange)
                                                    .doFinally(signal -> log.info("After"));
                                        }))
                                )
                                .uri(customerHost + ":" + customerPort) //destinazione
        ).build();
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        //opens a connection and stays on so no other handshake is required
        return builder.tcp(orderHost, orderPort);
    }

    @Bean
    RouterFunction<ServerResponse> functionalReactiveHttRoute(CrmClient crm) {
        log.info("Request received");
        //controller created using functional style instead of controller style
        return route().GET("/cos", serverRequest ->
                ServerResponse.ok().body(crm.getCustomerOrders(), CustomerOrders.class)).build();
    }
}

