package microservices.bloggers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import microservices.bloggers.configuration.JpaConfiguration;


@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages={"microservices.bloggers"})
public class BloggerApp {

	public static void main(String[] args) {
		SpringApplication.run(BloggerApp.class, args);
	}
}
