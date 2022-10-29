package com.learning.awsspring.services;

import com.learning.awsspring.entities.Customer;
import com.learning.awsspring.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.saveEntity(customer);
    }
}
