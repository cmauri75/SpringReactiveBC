package net.reactive.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.reactive.gateway.dto.Customer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@SpringBootTest
@AutoConfigureWebTestClient
/**
 * Calls to rest service are made instantiating a mockwebserver (wiremock is an alternative)
 * Calls to rsocket are done using a mockcontroller in test scope
 */
class GatewayApplicationTests {

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${customer.port}")
    private Integer customerPort;

    @Autowired
    private WebTestClient webClient;


    public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() throws IOException {
        mockBackEnd.start(customerPort.intValue());
    }

    @Test
    void testCos() throws JsonProcessingException {
        //Prepare mock calls
        Customer[] custs = {new Customer(2, "Pippo")};
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(custs))
                .addHeader("Content-Type", "application/json"));

        webClient.get().uri("/cos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].customer").isNotEmpty()
                .jsonPath("$[0].customer.id").isEqualTo(2)
                .jsonPath("$[0].customer.name").isEqualTo("Pippo")
                .jsonPath("$[0].orders").isNotEmpty()
                .jsonPath("$[0].orders").isArray()
                .jsonPath("$[0].orders[0].orderId").isEqualTo(2)
                .jsonPath("$[0].orders[0].customerId").isEqualTo(5);
    }

}
