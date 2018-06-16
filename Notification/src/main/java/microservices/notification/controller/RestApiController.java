package microservices.notification.controller;

import microservices.notification.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@RequestMapping(value = "/send_message/{email}/{message}", method = RequestMethod.PUT)
	public ResponseEntity<?> sendMessage(@PathVariable("email") String email, @PathVariable("message") String message) {
		logger.info("Sending message to {}", email);

		String from = "advertisingOnYoutube@gmail.com";

		String host = "localhost";

		Properties properties = System.getProperties();

		properties.setProperty("mail.smtp.host", host);

		Session session = Session.getDefaultInstance(properties);

		try {
			MimeMessage mes = new MimeMessage(session);

			mes.setFrom(new InternetAddress(from));
			mes.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			mes.setSubject("New event on the AdvertisingOnYoutube!");
			mes.setText(message);

			Transport.send(mes);
		} catch (MessagingException mex) {
			return new ResponseEntity(new CustomErrorType("Sending message failed"), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(HttpStatus.OK);
	}

}