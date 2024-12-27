package com.learning.awsspring.web.controllers;

import com.learning.awsspring.entities.Customer;
import com.learning.awsspring.services.CustomerService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.findAllCustomers();
    }

    @GetMapping("/{id}/{email}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable(name = "id") UUID uuid, @PathVariable(name = "email") String emailId) {
        return customerService
                .findCustomerById(uuid, emailId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(
            @RequestBody @Validated Customer customer, UriComponentsBuilder uriComponentsBuilder) {
        Customer persistedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.created(
                        uriComponentsBuilder
                                .path("/api/customers/{id}/{email}")
                                .buildAndExpand(
                                        persistedCustomer.getId().toString(),
                                        persistedCustomer.getEmail())
                                .toUri())
                .body(persistedCustomer);
    }

    @DeleteMapping("/{id}/{email}")
    public ResponseEntity<Customer> deleteCustomer(
            @PathVariable(name = "id") UUID uuid, @PathVariable(name = "email") String emailId) {
        return customerService
                .findCustomerById(uuid, emailId)
                .map(
                        customer -> {
                            customerService.deleteCustomerById(uuid, emailId);
                            return ResponseEntity.ok(customer);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
