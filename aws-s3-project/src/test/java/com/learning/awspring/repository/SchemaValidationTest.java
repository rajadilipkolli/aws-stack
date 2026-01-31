package com.learning.awspring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.awspring.common.SQLContainerConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
@AutoConfigureTestDatabase
@Import(SQLContainerConfig.class)
class SchemaValidationTest {

    @Autowired private DataSource dataSource;

    @Test
    void validateJpaMappingsWithDbSchema() {
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);
    }
}
