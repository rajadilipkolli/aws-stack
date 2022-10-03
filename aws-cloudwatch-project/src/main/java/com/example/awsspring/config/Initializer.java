package com.example.awsspring.config;

import com.example.awsspring.entities.Customer;
import com.example.awsspring.repositories.CustomerRepository;
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
        log.info("Running Initializer..... for Junit Tests");
        Customer customer = new Customer();
        customer.setText("Junit");
        this.customerRepository.save(customer);
    }
}
