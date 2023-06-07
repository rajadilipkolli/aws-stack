package com.learning.awsspring.services;

import com.learning.awsspring.entities.Customer;
import com.learning.awsspring.repositories.CustomerRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.saveEntity(customer);
    }

    public List<Customer> findAllCustomers() {
        return this.customerRepository.findAll();
    }

    public Optional<Customer> findCustomerById(UUID id) {
        return Optional.ofNullable(this.customerRepository.getCustomerById(id));
    }

    public void deleteCustomerById(UUID id) {
        this.customerRepository.deleteCustomerById(id);
    }
}
