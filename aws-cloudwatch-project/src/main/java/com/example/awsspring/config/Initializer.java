package com.example.awsspring.config;

import com.example.awsspring.entities.Customer;
import com.example.awsspring.repositories.CustomerRepository;
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
        log.info("Running Initializer..... for Junit Tests");
        Customer customer = new Customer();
        customer.setText("Junit");
        this.customerRepository.save(customer);
    }
}
