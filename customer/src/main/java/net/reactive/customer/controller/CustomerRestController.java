package net.reactive.customer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.reactive.customer.dto.Customer;
import net.reactive.customer.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public
class CustomerRestController {

    private final CustomerService customerService;

    @GetMapping("/customer")
    Flux<Customer> get() {
        return customerService.findAll();
    }

    @PostMapping("/stop")
    Mono<Void> stopApplication() {
        customerService.stopApplication();
        return Mono.empty();
    }

}
