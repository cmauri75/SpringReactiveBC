package net.reactive.gateway.client;

import lombok.RequiredArgsConstructor;
import net.reactive.gateway.dto.Customer;
import net.reactive.gateway.dto.CustomerOrders;
import net.reactive.gateway.dto.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CrmClient {

    @Value("${customer.host}")
    String customerHost;
    @Value("${customer.port}")
    Integer customerPort;

    private final WebClient webClient;

    private final RSocketRequester rSocketRequester;

    Flux<Customer> getCustomers() {
        //makes a call to service and turns in a reactive stream of customers
        return this.webClient.get().uri(customerHost+":"+customerPort+"/customer").retrieve().bodyToFlux(Customer.class)
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

    public Flux<CustomerOrders> getCustomerOrders() {
        //launch multiple thread asynchronously with no need of thread, semaphore and so on
        Flux<Tuple2<Customer, List<Order>>> res = getCustomers().flatMap(customer ->
                Mono.zip(Mono.just(customer), getOrdersOf(customer.getId()).collectList()));

        return res.map(tuple -> new CustomerOrders(tuple.getT1(), tuple.getT2()));
    }

}
