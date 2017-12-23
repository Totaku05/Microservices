package microservices.identification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import microservices.identification.configuration.JpaConfiguration;


@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages={"microservices.identification"})
public class IdentificationApp {

	public static void main(String[] args) {
		SpringApplication.run(IdentificationApp.class, args);
	}
}
