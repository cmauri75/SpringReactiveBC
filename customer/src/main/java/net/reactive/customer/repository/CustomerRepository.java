package net.reactive.customer.repository;

import net.reactive.customer.dto.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}
