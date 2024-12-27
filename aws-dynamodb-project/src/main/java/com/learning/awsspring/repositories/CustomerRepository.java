package com.learning.awsspring.repositories;

import com.learning.awsspring.entities.Customer;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;

@Repository
public class CustomerRepository {

    private final DynamoDbTemplate dynamoDbTemplate;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public CustomerRepository(
            DynamoDbTemplate dynamoDbTemplate, DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbTemplate = dynamoDbTemplate;
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    public List<Customer> findAll() {
        return dynamoDbTemplate.scanAll(Customer.class).stream()
                .map(Page::items)
                .flatMap(List::stream)
                .toList();
    }

    public Customer getCustomerById(UUID uuid, String email) {

        // Create a KEY object
        Key key = Key.builder().partitionValue(uuid.toString()).sortValue(email).build();

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

    public void deleteCustomerById(UUID uuid, String emailId) {

        // Create a KEY object
        Key key = Key.builder().partitionValue(uuid.toString()).sortValue(emailId).build();

        // delete the item by using the key
        dynamoDbTemplate.delete(key, Customer.class);
    }

    public BatchWriteResult deleteAll() {
        DynamoDbTable<Customer> customerDynamoDbTable =
                dynamoDbEnhancedClient.table("customer", TableSchema.fromBean(Customer.class));
        List<WriteBatch> writeBatchList =
                findAll().stream()
                        .map(
                                customer ->
                                        WriteBatch.builder(Customer.class)
                                                .mappedTableResource(customerDynamoDbTable)
                                                .addDeleteItem(customer)
                                                .build())
                        .toList();
        return dynamoDbEnhancedClient.batchWriteItem(
                builder -> builder.writeBatches(writeBatchList));
    }

    public void deleteCustomer(Customer customer) {
        dynamoDbTemplate.delete(customer);
    }
}
