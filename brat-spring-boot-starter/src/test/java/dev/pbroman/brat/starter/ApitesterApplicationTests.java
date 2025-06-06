package dev.pbroman.brat.starter;

import dev.pbroman.brat.starter.config.ApiTesterAutoConfiguration;
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
