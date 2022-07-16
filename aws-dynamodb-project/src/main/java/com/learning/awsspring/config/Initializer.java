package com.learning.awsspring.config;

import com.learning.awsspring.entities.Customer;
import com.learning.awsspring.repositories.CustomerRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Initializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
        Customer customer = new Customer();
        UUID uuid = UUID.randomUUID();
        customer.setId(uuid);
        customer.setName("raja");
        customer.setEmail("rajaEmail@gmail.com");
        this.customerRepository.save(customer);
        Customer persistedCustomer = this.customerRepository.getCustomerById(uuid);
        log.debug("persistedCustomer", persistedCustomer);
    }
}
