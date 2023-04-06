package com.learning.awspring.services;

import com.learning.awspring.domain.CustomerDTO;
import com.learning.awspring.entities.Customer;
import com.learning.awspring.repositories.CustomerRepository;
import com.learning.awspring.web.mapper.CustomerMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer saveCustomer(CustomerDTO customerDTO) {
        var customer = this.customerMapper.toEntity(customerDTO);
        return customerRepository.save(customer);
    }

    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer> updateCustomer(Long id, CustomerDTO customerDTO) {
        return findCustomerById(id)
                .map(
                        customerObj -> {
                            var customer = this.customerMapper.toEntity(customerDTO);
                            customer.setId(customerObj.getId());
                            return customerRepository.save(customer);
                        });
    }
}
