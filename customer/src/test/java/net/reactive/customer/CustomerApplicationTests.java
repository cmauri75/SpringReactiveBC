package net.reactive.customer;

import net.reactive.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerApplicationTests {

    @Autowired
    CustomerService customerService;

    @Test
    void testService() {
        var data = customerService.findAll();

        StepVerifier
                .create(data)
                .assertNext(message -> {
                    assertThat(message.getId()).isEqualTo(1);
                    assertThat(message.getName()).isEqualTo("Cesare");
                })
                .expectNextMatches(customer -> customer.getName().equals("Mario"))
                .expectNextMatches(customer -> customer.getName().equals("Gianni"))
                .assertNext(message -> {
                    assertThat(message.getId()).isEqualTo(4);
                    assertThat(message.getName()).isEqualTo("Klevench");
                })
                .verifyComplete();
    }

}
