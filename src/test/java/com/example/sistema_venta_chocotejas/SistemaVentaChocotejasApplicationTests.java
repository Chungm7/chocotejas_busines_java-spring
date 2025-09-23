package com.example.sistema_venta_chocotejas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class SistemaVentaChocotejasApplicationTests {

    @Test
    void contextLoads() {
    }

}
