package net.reactive.gateway;

import net.reactive.gateway.dto.Order;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class MockOrderSocketController {

    /**
     * rsocket endpoint
     *
     * @param customerId
     * @return
     */
    @MessageMapping("orders.{customerId}")
    Flux<Order> getOrdersOf(@DestinationVariable Integer customerId) {
        List<Order> res = List.of(new Order(2,5));
        return Flux.fromIterable(res);
    }
}
