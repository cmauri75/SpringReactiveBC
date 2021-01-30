package net.tinvention.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SpringBootApplication
public class OrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }

}

@Data
@AllArgsConstructor
class Order {
    private Integer orderId, CustomerId;

}

@Controller
class OrderSocketController {

    private final Map<Integer, Collection<Order>> db = new ConcurrentHashMap<>();

    OrderSocketController() {
        for (var customerId = 1; customerId < 10; customerId++) {
            var orderList = new ArrayList<Order>();
            for (var orderId = 1; orderId < Math.random() * 100; orderId++) {
                orderList.add(new Order(orderId,customerId));
            }
            db.put(customerId, orderList);
        }
    }

    @MessageMapping ("orders.{customerId}")
    Flux<Order> getOrdersOf(@DestinationVariable Integer customerId){
        return Flux.fromIterable(db.get(customerId));
    }
}