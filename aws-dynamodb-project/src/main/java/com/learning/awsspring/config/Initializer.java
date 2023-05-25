package com.learning.awsspring.config;

import static com.learning.awsspring.utils.AppConstants.PROFILE_NOT_TEST;

import com.learning.awsspring.entities.Customer;
import com.learning.awsspring.repositories.CustomerRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({PROFILE_NOT_TEST})
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
        this.customerRepository.saveEntity(customer);
        Customer persistedCustomer = this.customerRepository.getCustomerById(uuid);
        log.debug("persistedCustomer {}", persistedCustomer);
        persistedCustomer.setEmail("rajakolli@gmail.com");
        Customer updatedCustomer = this.customerRepository.update(persistedCustomer);
        log.debug("updatedCustomer {}", updatedCustomer);
    }
}
