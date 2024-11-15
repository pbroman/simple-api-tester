package dev.pbroman.simple.api.tester;

import dev.pbroman.simple.api.tester.config.ApiTesterAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {ApiTesterAutoConfiguration.class, JacksonAutoConfiguration.class})
class ApitesterApplicationTests {

	@Test
	void contextLoads() {
	}

}
