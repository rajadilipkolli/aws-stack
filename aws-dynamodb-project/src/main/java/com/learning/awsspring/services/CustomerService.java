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

    public Optional<Customer> findCustomerById(UUID id, String emailId) {
        return Optional.ofNullable(this.customerRepository.getCustomerById(id, emailId));
    }

    public void deleteCustomerById(UUID id, String emailId) {
        this.customerRepository.deleteCustomerById(id, emailId);
    }
}
