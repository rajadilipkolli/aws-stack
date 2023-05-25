package com.learning.awsspring.web.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.learning.awsspring.common.AbstractIntegrationTest;
import com.learning.awsspring.entities.Customer;
import com.learning.awsspring.repositories.CustomerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerIT extends AbstractIntegrationTest {

    @Autowired private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Autowired private CustomerRepository customerRepository;

    private List<Customer> customerList = null;

    @BeforeAll
    void setUpDbTable() {
        assertThat(LOCAL_STACK_CONTAINER.isRunning()).isTrue();
        // Create table on start up
        dynamoDbEnhancedClient
                .table("customer", TableSchema.fromBean(Customer.class))
                .createTable();
    }

    @BeforeEach
    void setUp() {
        List<Customer> customerArrayList = new ArrayList<>();
        customerArrayList.add(
                new Customer(UUID.randomUUID(), "First Customer", "junit1@email.com"));
        customerArrayList.add(
                new Customer(UUID.randomUUID(), "Second Customer", "junit2@email.com"));
        customerArrayList.add(
                new Customer(UUID.randomUUID(), "Third Customer", "junit3@email.com"));

        customerList = customerRepository.saveAll(customerArrayList);
    }

    @Test
    void shouldFetchAllCustomers() throws Exception {
        this.mockMvc
                .perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(customerList.size())));
    }

    @Test
    @Disabled
    void shouldFindCustomerById() throws Exception {
        Customer customer = customerList.get(0);
        UUID customerId = customer.getId();

        this.mockMvc
                .perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(customer.getName())))
                .andExpect(jsonPath("$.email", is(customer.getEmail())));
    }

    @Test
    void shouldCreateNewCustomer() throws Exception {
        Customer customer = new Customer(null, "New Customer", "New Email");
        this.mockMvc
                .perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(customer.getName())))
                .andExpect(jsonPath("$.email", is(customer.getEmail())))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(header().exists("location"));
    }
}
