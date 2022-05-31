package net.reactive.customer;

import net.reactive.customer.dto.Customer;
import net.reactive.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
class CustomerControllerApplicationITests {

    @MockBean
    CustomerRepository repository;

    @Autowired
    private WebTestClient webClient;

    @Test
    void testFluxEndpoint() {

        Customer customer = Customer.builder()
                .id(-1)
                .name("Cesare")
                .build();

        Mockito.when(repository.findAll()).thenReturn(Flux.just(customer));

        webClient.get().uri("/customer")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].name").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(-1)
                .jsonPath("$[0].name").isEqualTo("Cesare");

        Mockito.verify(repository, times(1)).findAll();

    }

}
