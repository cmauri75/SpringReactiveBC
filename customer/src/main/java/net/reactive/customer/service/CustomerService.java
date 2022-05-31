package net.reactive.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.reactive.customer.dto.Customer;
import net.reactive.customer.repository.CustomerRepository;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ApplicationContext applicationContext;

    public Flux<Customer> findAll() {
        return customerRepository.findAll();
    }

    public void stopApplication() {
        AvailabilityChangeEvent.publish(applicationContext, LivenessState.BROKEN);
        log.info("App stopped");
    }
}
