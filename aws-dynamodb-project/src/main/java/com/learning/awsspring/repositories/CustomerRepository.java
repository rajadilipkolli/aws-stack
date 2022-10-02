package com.learning.awsspring.repositories;

import com.learning.awsspring.entities.Customer;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
@RequiredArgsConstructor
public class CustomerRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public Customer getCustomerById(UUID uuid) {

        // Create a KEY object
        Key key = Key.builder().partitionValue(uuid.toString()).build();

        // Get the item by using the key
        return dynamoDbTemplate.load(key, Customer.class);
    }

    // Store this customer item in the database
    public Customer saveEntity(Customer customer) {
        return dynamoDbTemplate.save(customer);
    }

    public Customer update(Customer persistedCustomer) {
        return this.dynamoDbTemplate.update(persistedCustomer);
    }
}
