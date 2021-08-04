package com.example.hellospring

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import static org.assertj.core.api.Assertions.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeControllerTest {
    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    @Test
    public void hola() {
        assertThat(this.restTemplate.getForObject("http://localhost:"+port+"/hello?name=Jesus2", String.class))
                .isEqualTo("Hello Jesus2!");
    }
}
