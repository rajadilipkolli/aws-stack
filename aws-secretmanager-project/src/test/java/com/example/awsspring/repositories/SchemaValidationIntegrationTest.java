package com.example.awsspring.repositories;

import com.example.awsspring.common.DBTestContainer;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ImportTestcontainers(DBTestContainer.class)
class SchemaValidationIntegrationTest {

    @Autowired private DataSource dataSource;

    @Test
    void testSchemaValidity() {
        Assertions.assertThat(dataSource).isInstanceOf(HikariDataSource.class);
    }
}
