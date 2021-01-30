package net.tinvention.rwa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Flow;

@SpringBootApplication
@Slf4j
public class RwaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RwaApplication.class, args);
    }

    //@Bean
    ApplicationListener<ApplicationReadyEvent> readyO(CustomerRepository customerRepository, DatabaseClient dbc) {
        return new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
                Mono<Integer> createDB = dbc.sql("create table  CUSTOMER (id serial primary key not null, name varchar(255) not null)").fetch().rowsUpdated();

                Flux<String> names = Flux.just("cesare", "pipo", "pluto");
                Flux<Customer> custs = names.map(name -> new Customer(null, name));
                Flux<Customer> saved = custs.flatMap(cust -> customerRepository.save(cust));

                //questo è il momento che scatena l'esecuzione, prima ho solo creato la catena
                //saved.subscribe();
                //però prima devo scatenare altre azioni:

                createDB.thenMany(customerRepository.deleteAll())
                        .thenMany(saved)
                        .thenMany(customerRepository.findAll())
                        .subscribe();

                log.info("Done");

            }
        };
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(CustomerRepository customerRepository, DatabaseClient dbc) {
        return applicationReadyEvent -> {
            log.info("Start");

            Mono<Integer> createDB = dbc.sql("create table  CUSTOMER (id serial primary key not null, name varchar(255) not null)").fetch().rowsUpdated();

            var saved = Flux.just("cesare", "pipo", "pluto")
                    .map(name -> new Customer(null, name))
                    .flatMap(customerRepository::save);

            createDB.thenMany(customerRepository.deleteAll())
                    .thenMany(saved)
                    .thenMany(customerRepository.findAll())
                    .subscribe();

            log.debug("Done");

        };
    }
}

@RestController
@RequiredArgsConstructor
@Slf4j
class CustomerRestController{

    private final CustomerRepository customerRepository;

    private final ApplicationContext applicationContext;

    @GetMapping("/customer")
    Flux<Customer> get(){
        return this.customerRepository.findAll();
    }

    @PostMapping("/stop")
    Mono<Void> stopMe(){
        AvailabilityChangeEvent.publish(this.applicationContext, LivenessState.BROKEN);
        log.info("App stopped");
        return Mono.empty();
    }

}


@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    @Id
    private Integer id;
    private String name;
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}