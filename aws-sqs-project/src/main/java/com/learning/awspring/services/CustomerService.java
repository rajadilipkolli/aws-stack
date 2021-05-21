package com.learning.awspring.services;

import com.learning.awspring.domain.CustomerDTO;
import com.learning.awspring.entities.Customer;
import com.learning.awspring.repositories.CustomerRepository;
import com.learning.awspring.web.mapper.CustomerMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer saveCustomer(CustomerDTO customerDTO) {
        Customer customer = this.customerMapper.toEntity(customerDTO);
        return customerRepository.save(customer);
    }

    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer> updateCustomer(Long id, CustomerDTO customerDTO) {
        return findCustomerById(id)
                .map(
                        customerObj -> {
                            Customer customer = this.customerMapper.toEntity(customerDTO);
                            customer.setId(customerObj.getId());
                            return customerRepository.save(customer);
                        });
    }
}
