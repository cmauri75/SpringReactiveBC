package net.reactive.orders.controller;

import net.reactive.orders.dto.Order;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class OrderSocketController {
    private final Map<Integer, Collection<Order>> db = new ConcurrentHashMap<>();

    @PostConstruct
    private void initializeOrders() {
        for (var customerId = 1; customerId < 10; customerId++) {
            var orderList = new ArrayList<Order>();
            for (var orderId = 1; orderId < Math.random() * 100; orderId++) {
                orderList.add(new Order(orderId, customerId));
            }
            db.put(customerId, orderList);
        }
    }

    /**
     * rsocket endpoint
     *
     * @param customerId
     * @return
     */
    @MessageMapping("orders.{customerId}")
    Flux<Order> getOrdersOf(@DestinationVariable Integer customerId) {
        return Flux.fromIterable(db.get(customerId));

    }
}
