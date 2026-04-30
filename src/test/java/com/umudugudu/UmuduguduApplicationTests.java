package com.umudugudu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/umudugudu_test",
    "spring.jpa.hibernate.ddl-auto=none",
    "app.jwt.secret=test_secret_32_chars_minimum_xxxx",
    "spring.data.redis.host=localhost"
})
class UmuduguduApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring context starts without errors
    }
}
