package microservices.users.controller;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import microservices.users.model.*;
import microservices.users.service.ContactInfoService;
import microservices.users.service.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import microservices.users.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	UserService userService;

	@Autowired
	BloggerService bloggerService;

	@Autowired
	AdvertiserService advertiserService;

	@Autowired
	ContactInfoService contactInfoService;
	
	@RequestMapping(value = "/user/", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = userService.findAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}

		for(User user : users)
			user.setContactInfo(contactInfoService.findById(user.getId()));
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("id") int id) {
		logger.info("Fetching User with id {}", id);
		User user = userService.findById(id);
		if (user == null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("User with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		user.setContactInfo(contactInfoService.findById(user.getId()));
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/advertiser/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getAdvertiser(@PathVariable("id") int id) {
		logger.info("Fetching Advertiser with id {}", id);
		Advertiser advertiser = advertiserService.findById(id);
		if (advertiser == null) {
			logger.error("Advertiser with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Advertiser with id " + id
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Advertiser>(advertiser, HttpStatus.OK);
	}

	@RequestMapping(value = "/blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Blogger with id {}", id);
		Blogger blogger = bloggerService.findById(id);
		if (blogger == null) {
			logger.error("Blogger with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Blogger with id " + id
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Blogger>(blogger, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{login}/{password}", method = RequestMethod.GET)
	public ResponseEntity<?> Identify(@PathVariable("login") String login, @PathVariable("password") String password) {
		logger.info("Fetching User with login {}", login);
		List<User> users = userService.findAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		for(User user : users)
			if(user.getLogin().equals(login))
			{
				if(user.getPassword().equals(password))
					return new ResponseEntity<User>(user, HttpStatus.OK);
				else return new ResponseEntity(new CustomErrorType("Bad password"), HttpStatus.BAD_REQUEST);
			}
		return new ResponseEntity(new CustomErrorType("User with login " + login
				+ " not found"), HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/user/{card_number}", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user, @PathVariable("card_number") Integer card_number) {
		logger.info("Creating User : {}", user);

		//user.setId(counter++);
		if (userService.isUserExist(user)) {
			logger.error("Unable to create. A User with name {} already exist", user.getContactInfo().getFirstName() + " " + user.getContactInfo().getSecondName());
			return new ResponseEntity(new CustomErrorType("Unable to create. A User with name " +
					user.getContactInfo().getFirstName() + " " + user.getContactInfo().getSecondName() + " already exist."),HttpStatus.CONFLICT);
		}
		userService.saveUser(user);
		contactInfoService.saveInfo(user.getContactInfo());
		RestTemplate template = new RestTemplate();
		if(user.getRole().equals("Advertiser"))
		{
			Advertiser advertiser = new Advertiser();
			advertiser.setId(user.getId());
			advertiser.setAccount(0.0);
			advertiser.setLogin(user.getLogin());
			advertiser.setCard_number(card_number);
			advertiserService.saveAdvertiser(advertiser);
		}
		else
		{
			Blogger blogger = new Blogger();
			blogger.setAccount(0.0);
			blogger.setMinPrice(0.0);
			blogger.setId(user.getId());
			blogger.setStatus("Common");
			blogger.setLogin(user.getLogin());
			blogger.setCountOfSubscribers(0);
			blogger.setCard_number(card_number);
			bloggerService.saveBlogger(blogger);
		}
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody User user, @RequestBody Blogger blogger, @RequestBody Advertiser advertiser) {
		logger.info("Updating User with id {}", id);

		User currentUser = userService.findById(id);

		if (currentUser == null) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		ContactInfo currentInfo = contactInfoService.findById(currentUser.getInfo());

		currentUser.setLogin(user.getLogin());
		currentUser.setPassword(user.getPassword());
		currentUser.setRole(user.getRole());

		currentInfo.setFirstName(user.getContactInfo().getFirstName());
		currentInfo.setSecondName(user.getContactInfo().getSecondName());
		currentInfo.setEmail(user.getContactInfo().getEmail());
		currentInfo.setPhoneNumber(user.getContactInfo().getPhoneNumber());

		userService.updateUser(currentUser);
		contactInfoService.updateInfo(currentInfo);

		RestTemplate template = new RestTemplate();
		if(currentUser.getRole().equals("Advertiser"))
		{
			Advertiser currentAdvertiser = advertiserService.findById(id);
			currentAdvertiser.setId(currentUser.getId());
			currentAdvertiser.setLogin(currentUser.getLogin());
			currentAdvertiser.setCard_number(advertiser.getCard_number());
			advertiserService.updateAdvertiser(currentAdvertiser);
		}
		if(currentUser.getRole().equals("Blogger"))
		{
			Blogger currentBlogger = bloggerService.findById(id);
			currentBlogger.setId(currentUser.getId());
			currentBlogger.setLogin(currentUser.getLogin());
			currentBlogger.setStatus(blogger.getStatus());
			currentBlogger.setCountOfSubscribers(blogger.getCountOfSubscribers());
			currentBlogger.setMinPrice(blogger.getMinPrice());
			currentBlogger.setCard_number(blogger.getCard_number());
			bloggerService.updateBlogger(currentBlogger);
		}

		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/user_account/{id}/{sum}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAccount(@PathVariable("id") int id, @PathVariable("sum") double sum) {
		logger.info("Updating Account for User with id {}", id);

		User currentUser = userService.findById(id);
		int card_number = 0;

		if (currentUser == null) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		if(currentUser.getRole().equals("Advertiser"))
		{
			Advertiser currentAdvertiser = advertiserService.findById(id);
			card_number = currentAdvertiser.getCard_number();
			if(currentAdvertiser.getAccount() + sum < 0)
			{
				logger.error("Unable to update. Bad sum of money.", id);
				return new ResponseEntity(new CustomErrorType("Unable to upate. Bad sum of money."),
						HttpStatus.BAD_REQUEST);
			}

			currentAdvertiser.setAccount(currentAdvertiser.getAccount() + sum);
			advertiserService.updateAdvertiser(currentAdvertiser);
		}
		if(currentUser.getRole().equals("Blogger"))
		{
			Blogger currentBlogger = bloggerService.findById(id);
			card_number = currentBlogger.getCard_number();
			if(currentBlogger.getAccount() + sum < 0)
			{
				logger.error("Unable to update. Bad sum of money.", id);
				return new ResponseEntity(new CustomErrorType("Unable to upate. Bad sum of money."),
						HttpStatus.BAD_REQUEST);
			}

			currentBlogger.setAccount(currentBlogger.getAccount() + sum);
			bloggerService.updateBlogger(currentBlogger);
		}

		if(sum > 0) {
			try {
				RestTemplate template = new RestTemplate();
				template.put("http://localhost:9696/payment/withdraw_money/{card_number}/{sum}", card_number, sum);
			} catch (HttpClientErrorException e) {
				try {
					JSONObject object = new JSONObject(e.getResponseBodyAsString());
					return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
				} catch (Throwable t) {
					return new ResponseEntity(new CustomErrorType("Account updating failed"), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
		logger.info("Deleting User with id {}", id);

		User user = userService.findById(id);
		if (user == null) {
			logger.error("Unable to delete. User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		RestTemplate template = new RestTemplate();
		if(user.getRole().equals("Blogger"))
			bloggerService.deleteBloggerById(id);
		if(user.getRole().equals("Advertiser"))
			advertiserService.deleteAdvertiserById(id);
		userService.deleteUserById(id);
		contactInfoService.deleteInfoById(id);
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}
}