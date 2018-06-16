package microservices.payment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@RequestMapping(value = "/withdraw_money/{card_number}/{sum}", method = RequestMethod.PUT)
	public ResponseEntity<?> withdrawMoneyFromCard(@PathVariable("card_number") int card_number, @PathVariable("sum") double sum) {
		logger.info("Withdrawing {} money from card with number {}", sum, card_number);

		return new ResponseEntity(HttpStatus.OK);
	}

}