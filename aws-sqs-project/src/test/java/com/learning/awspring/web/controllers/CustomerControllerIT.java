package com.learning.awspring.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.domain.CustomerDTO;
import com.learning.awspring.entities.Customer;
import com.learning.awspring.repositories.CustomerRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class CustomerControllerIT extends AbstractIntegrationTest {

    @Autowired private CustomerRepository customerRepository;

    private List<Customer> customerList = null;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        customerList = new ArrayList<>();
        customerList.add(new Customer(null, "First Customer"));
        customerList.add(new Customer(null, "Second Customer"));
        customerList.add(new Customer(null, "Third Customer"));
        customerList = customerRepository.saveAll(customerList);
    }

    @Test
    void shouldFetchAllCustomers() throws Exception {
        this.mockMvc
                .perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(customerList.size())));
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        Customer customer = customerList.get(0);
        Long customerId = customer.getId();

        this.mockMvc
                .perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customer.getText())));
    }

    @Test
    void shouldCreateNewCustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO("New Customer");
        this.mockMvc
                .perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text", is(customerDTO.text())));
    }

    @Test
    void shouldReturn400WhenCreateNewCustomerWithoutText() throws Exception {
        Customer customer = new Customer(null, null);

        this.mockMvc
                .perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/customers")))
                .andReturn();
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        Customer customer = customerList.get(0);
        CustomerDTO customerDTO = new CustomerDTO("Updated Customer");

        this.mockMvc
                .perform(
                        put("/api/customers/{id}", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customerDTO.text())));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        Customer customer = customerList.get(0);

        this.mockMvc
                .perform(delete("/api/customers/{id}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customer.getText())));
    }
}
