package com.learning.awsspring.repositories;

import com.learning.awsspring.entities.Customer;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

@Repository
@RequiredArgsConstructor
public class CustomerRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public List<Customer> findAll() {
        return dynamoDbTemplate.scanAll(Customer.class).stream()
                .map(Page::items)
                .flatMap(List::stream)
                .toList();
    }

    public Customer getCustomerById(UUID uuid) {

        // Create a KEY object
        Key key = Key.builder().partitionValue(uuid.toString()).build();

        // Get the item by using the key
        return dynamoDbTemplate.load(key, Customer.class);
    }

    // Store this customer item in the database
    public Customer saveEntity(Customer customer) {
        if (null == customer.getId()) {
            customer.setId(UUID.randomUUID());
        }
        return dynamoDbTemplate.save(customer);
    }

    public Customer update(Customer persistedCustomer) {
        return this.dynamoDbTemplate.update(persistedCustomer);
    }

    public List<Customer> saveAll(List<Customer> entities) {

        List<Customer> result = new ArrayList<>();

        for (Customer entity : entities) {
            result.add(saveEntity(entity));
        }

        return result;
    }
}
