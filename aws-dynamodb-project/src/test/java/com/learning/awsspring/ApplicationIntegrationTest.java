package com.learning.awsspring;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.awsspring.common.AbstractIntegrationTest;
import com.learning.awsspring.entities.Customer;

import io.awspring.cloud.dynamodb.DynamoDbOperations;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.UUID;

@Slf4j
class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Autowired private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Autowired private DynamoDbOperations dynamoDbOperations;

    @Test
    void contextLoads() {
        assertThat(LOCAL_STACK_CONTAINER.isRunning()).isTrue();
        // Create table on start up
        dynamoDbEnhancedClient
                .table("customer", TableSchema.fromBean(Customer.class))
                .createTable();

        UUID id = UUID.randomUUID();
        String email = "junit@email.com";
        String name = "junit";
        Customer customer = Customer.builder().id(id).name(name).email(email).build();
        // Saving Customer
        dynamoDbOperations.save(customer);

        // Get Customer for id
        Customer loadedCustomer = getCustomer(id, email);
        assertThat(loadedCustomer.getId()).isEqualTo(id);
        assertThat(loadedCustomer.getEmail()).isEqualTo(email);
        assertThat(loadedCustomer.getName()).isEqualTo(name);

        loadedCustomer.setName("junit-integration-test");
        dynamoDbOperations.update(loadedCustomer);
        Customer updatedCustomer = getCustomer(id, email);
        assertThat(updatedCustomer.getName()).isEqualTo("junit-integration-test");

        // Query
        PageIterable<Customer> departmentPageIterable =
                dynamoDbOperations.query(
                        QueryEnhancedRequest.builder()
                                .queryConditional(
                                        QueryConditional.keyEqualTo(
                                                Key.builder()
                                                        .partitionValue(id.toString())
                                                        .build()))
                                .build(),
                        Customer.class);
        // Print number of items queried.
        log.info("Number of items queried :{}", departmentPageIterable.items().stream().count());

        // Delete
        dynamoDbOperations.delete(customer);

        Customer customerAfterDeletion = getCustomer(id, email);
        assertThat(customerAfterDeletion).isNull();

        // Delete table after it has been used
        dynamoDbEnhancedClient
                .table("customer", TableSchema.fromBean(Customer.class))
                .deleteTable();
    }

    private Customer getCustomer(UUID id, String email) {
        return dynamoDbOperations.load(
                Key.builder()
                        .partitionValue(AttributeValue.builder().s(id.toString()).build())
                        .sortValue(email)
                        .build(),
                Customer.class);
    }
}
