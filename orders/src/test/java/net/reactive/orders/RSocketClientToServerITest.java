package net.reactive.orders;

import lombok.extern.slf4j.Slf4j;
import net.reactive.orders.dto.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Slf4j
public class RSocketClientToServerITest {
    private static RSocketRequester requester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder, @Value("${spring.rsocket.server.port}") Integer port) {
        requester = builder
                .tcp("localhost", port);
    }

    @AfterAll
    public static void tearDownOnce() {
        requester.rsocketClient().dispose();
    }

    @Test
    void testRequestGetsResponse() {
        // Send a request message (1)
        Flux<Order> result = requester
                .route("orders.1")
                .retrieveFlux(Order.class);

        // Verify that the response message contains the expected data (2)
        StepVerifier
                .create(result)
                .thenConsumeWhile(x -> true, message -> {
                    log.debug("Consuming {}", message);
                    assertThat(message.customerId()).isEqualTo(1);
                    assertThat(message.orderId()).isPositive();
                    assertThat(message.orderId()).isLessThan(100);
                })
                .verifyComplete();
    }
}
