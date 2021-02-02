package net.tinvention.gateway;

import io.rsocket.RSocket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@Slf4j
public class GatewayApplication {

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
                                .uri("http://localhost:8080") //destinazione
        ).build();
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        //opens a connection and stays on so no other handshake is required
        return builder.tcp("localhost", 8081);
    }

    @Bean
    RouterFunction<ServerResponse> functionalReactiveHttRoute(CrmClient crm) {
        //controller created using functional style instead of controller style
        return route().GET("/cos", new HandlerFunction<ServerResponse>() {
            @Override
            public Mono<ServerResponse> handle(ServerRequest serverRequest) {
                return ServerResponse.ok().body(crm.getCustomerOrders(), CustomerOrders.class);
            }
        }).build();
    }
}

@Data
@AllArgsConstructor
class Order {
    private Integer orderId, CustomerId;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    private Integer id;
    private String name;
}

@Component
@AllArgsConstructor
class CrmClient {
    private final WebClient webClient;

    //Non blocking rsocket requester
    private final RSocketRequester rSocketRequester;

    Flux<Customer> getCustomers() {
        //makes a call to service and turns in a reactive stream of customers
        return this.webClient.get().uri("http://localhost:8080/customer").retrieve().bodyToFlux(Customer.class)
                .retry(20)
                .onErrorResume(ex -> Flux.empty())
                .timeout(Duration.ofSeconds(10));
    }

    Flux<Order> getOrdersOf(Integer customerId) {
        //retreives orders from rsocket
        return this.rSocketRequester
                .route("orders.{customerId}", customerId)
                .retrieveFlux(Order.class);
    }

    Flux<CustomerOrders> getCustomerOrders() {
        //launch multiple thread asyncronous with no need of thread, semaphore and so on
        Flux<Tuple2<Customer, List<Order>>> res = getCustomers().flatMap(customer ->
                Mono.zip(Mono.just(customer), getOrdersOf(customer.getId()).collectList()));

        return res.map(tuple -> new CustomerOrders(tuple.getT1(), tuple.getT2()));
    }

}

@Data
@RequiredArgsConstructor
class CustomerOrders {
    private final Customer customer;
    private final List<Order> orders;
}