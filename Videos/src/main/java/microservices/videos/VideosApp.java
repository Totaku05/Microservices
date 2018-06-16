package microservices.videos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import microservices.videos.configuration.JpaConfiguration;


@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages={"microservices.videos"})
public class VideosApp {

	public static void main(String[] args) {
		SpringApplication.run(VideosApp.class, args);
	}
}
