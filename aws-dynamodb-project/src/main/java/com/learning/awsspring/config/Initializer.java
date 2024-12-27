package com.learning.awsspring.config;

import com.learning.awsspring.entities.Customer;
import com.learning.awsspring.repositories.CustomerRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    private final CustomerRepository customerRepository;

    public Initializer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
        Customer customer = new Customer();
        UUID uuid = UUID.randomUUID();
        customer.setId(uuid);
        customer.setName("raja");
        customer.setEmail("rajaEmail@gmail.com");
        this.customerRepository.saveEntity(customer);
        Customer persistedCustomer =
                this.customerRepository.getCustomerById(uuid, customer.getEmail());
        log.debug("persistedCustomer {}", persistedCustomer);
        persistedCustomer.setName("rajakolli");
        Customer updatedCustomer = this.customerRepository.update(persistedCustomer);
        log.debug("updatedCustomer {}", updatedCustomer);
    }
}
