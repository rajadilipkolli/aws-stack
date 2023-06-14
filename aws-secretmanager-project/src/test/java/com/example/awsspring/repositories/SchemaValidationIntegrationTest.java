package com.example.awsspring.repositories;

import com.example.awsspring.common.DBTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ImportTestcontainers(DBTestContainer.class)
public class SchemaValidationIntegrationTest {

    @Test
    public void testSchemaValidity() {}
}
