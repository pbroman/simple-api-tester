package dev.pbroman.simple.api.tester;

import dev.pbroman.simple.api.tester.config.ApiTesterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApiTesterConfig.class)
public class ApitesterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApitesterApplication.class, args);
	}

}
