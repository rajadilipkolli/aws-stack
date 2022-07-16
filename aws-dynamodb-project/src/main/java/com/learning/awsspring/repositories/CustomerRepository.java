package com.learning.awsspring.repositories;

import com.learning.awsspring.entities.Customer;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class CustomerRepository {

    @Autowired private DynamoDbEnhancedClient dynamoDbenhancedClient;

    public CustomerRepository() {
        super();
    }

    // Store this order item in the database
    public void save(final Customer customer) {
        DynamoDbTable<Customer> customerTable = getTable();
        customerTable.putItem(customer);
    }

    /**
     * @return
     */
    private DynamoDbTable<Customer> getTable() {
        DynamoDbTable<Customer> customerTable =
                dynamoDbenhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
        return customerTable;
    }

    public Customer getCustomerById(UUID uuid) {

        DynamoDbTable<Customer> customerTable = getTable();

        // Create a KEY object
        Key key = Key.builder().partitionValue(uuid.toString()).build();

        // Get the item by using the key
        return customerTable.getItem(r -> r.key(key));
    }
}
