package microservices.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import microservices.users.configuration.JpaConfiguration;


@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages={"microservices.users"})
public class UsersApp {

	public static void main(String[] args) {
		SpringApplication.run(UsersApp.class, args);
	}
}
