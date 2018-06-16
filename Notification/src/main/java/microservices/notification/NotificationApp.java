package microservices.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages={"microservices.notification"})
public class NotificationApp {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApp.class, args);
	}
}
