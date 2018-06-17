package microservices.users.controller;

import java.util.List;

import javafx.util.Pair;
import microservices.users.model.*;
import microservices.users.service.ContactInfoService;
import microservices.users.service.*;
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

	@RequestMapping(value = "/blogger/", method = RequestMethod.GET)
	public ResponseEntity<List<Blogger>> listAllBloggers() {
		List<Blogger> bloggers = bloggerService.findAllBloggers();
		if (bloggers.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<List<Blogger>>(bloggers, HttpStatus.OK);
	}

    @RequestMapping(value = "/statuses/", method = RequestMethod.GET)
    public ResponseEntity<List<Pair<String, Double>>> listAllStatuses() {
        List<Pair<String, Double>> statuses = bloggerService.getStatuses();
        return new ResponseEntity<List<Pair<String, Double>>>(statuses, HttpStatus.OK);
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

		return userService.identify(login, password);
	}

	@RequestMapping(value = "/user/{card_number}", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user, @PathVariable("card_number") Integer card_number) {
		logger.info("Creating User : {}", user);

		if (userService.isUserExist(user)) {
			logger.error("Unable to create. A User with name {} already exist", user.getContactInfo().getFirstName() + " " + user.getContactInfo().getSecondName());
			return new ResponseEntity(new CustomErrorType("Unable to create. A User with name " +
					user.getContactInfo().getFirstName() + " " + user.getContactInfo().getSecondName() + " already exist."),HttpStatus.CONFLICT);
		}
		userService.saveUser(user);
		contactInfoService.saveInfo(user.getContactInfo());
		if(user.getRole().equals("Advertiser"))
			advertiserService.newAdvertiser(user.getId(), user.getLogin(), card_number);
		else
			bloggerService.newBlogger(user.getId(), user.getLogin(), card_number);
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
			currentAdvertiser.setLogin(currentUser.getLogin());
			currentAdvertiser.setCard_number(advertiser.getCard_number());
			advertiserService.updateAdvertiser(currentAdvertiser);
		}
		if(currentUser.getRole().equals("Blogger"))
		{
			Blogger currentBlogger = bloggerService.findById(id);
			currentBlogger.setLogin(currentUser.getLogin());
			currentBlogger.setStatus(blogger.getStatus());
			currentBlogger.setCountOfSubscribers(blogger.getCountOfSubscribers());
			currentBlogger.setMinPrice(blogger.getMinPrice());
			currentBlogger.setCard_number(blogger.getCard_number());
			bloggerService.updateBlogger(currentBlogger);
		}

		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/user_account/{id}/{sum}/{external}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAccount(@PathVariable("id") int id, @PathVariable("sum") double sum, @PathVariable("external") boolean external) {
		logger.info("Updating Account for User with id {}", id);

		User currentUser = userService.findById(id);
		int card_number = 0;
		double account = 0;

		if (currentUser == null) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to update. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		if(currentUser.getRole().equals("Advertiser"))
            card_number = advertiserService.updateAccount(id, sum);
		else
            card_number = bloggerService.updateAccount(id, sum);

		if(card_number < 0)
		{
		    logger.error("Unable to update. Bad sum of money.");
		    return new ResponseEntity(new CustomErrorType("Unable to update. Bad sum of money."),
                    HttpStatus.BAD_REQUEST);
		}

		//user wants to withdraw or enter money into the system
		if(external)
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
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

    @RequestMapping(value = "/status/{id}/{status}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateBloggerStatus(@PathVariable("id") int id, @PathVariable("status") String status) {
        if(!bloggerService.updateBloggerStatus(id, status))
        {
            logger.error("Unable to update. You haven't such sum of money.");
            return new ResponseEntity(new CustomErrorType("Unable to update. You haven't such sum of money."),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
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

		if(user.getRole().equals("Blogger"))
			bloggerService.deleteBloggerById(id);
		if(user.getRole().equals("Advertiser"))
			advertiserService.deleteAdvertiserById(id);
		userService.deleteUserById(id);
		contactInfoService.deleteInfoById(id);
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}
}