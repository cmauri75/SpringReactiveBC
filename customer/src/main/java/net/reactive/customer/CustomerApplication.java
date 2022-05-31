package net.reactive.customer;

import lombok.extern.slf4j.Slf4j;
import net.reactive.customer.dto.Customer;
import net.reactive.customer.repository.CustomerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    /**
     * NB: liquibase can't be used because new h2 db instance is created every time a new client connects
     */
    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(CustomerRepository customerRepository, DatabaseClient dbc) {
        return applicationReadyEvent -> {
            Mono<Integer> createDB = dbc.sql("create table  CUSTOMER (id serial primary key not null, name varchar(255) not null)").fetch().rowsUpdated();

            var saved = Flux.just("Cesare", "Mario", "Gianni", "Klevench")
                    .map(name -> new Customer(null, name))
                    .flatMap(customerRepository::save);

            try {
                createDB.thenMany(customerRepository.deleteAll())
                        .thenMany(saved)
                        .thenMany(customerRepository.findAll())
                        .subscribe();
            } catch (NullPointerException e) {
                log.warn("Unable to initialize DB");
                log.debug("",e);
            }
        };
    }
}








