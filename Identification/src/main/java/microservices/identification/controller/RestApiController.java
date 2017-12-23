package microservices.identification.controller;

import java.util.List;

import microservices.identification.model.ContactInfo;
import microservices.identification.model.User;
import microservices.identification.service.ContactInfoService;
import microservices.identification.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import microservices.identification.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	UserService userService;

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

	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		logger.info("Creating User : {}", user);

		if (userService.isUserExist(user)) {
			logger.error("Unable to create. A User with name {} already exist", user.getContactInfo().getFirstName() + " " + user.getContactInfo().getSecondName());
			return new ResponseEntity(new CustomErrorType("Unable to create. A User with name " +
					user.getContactInfo().getFirstName() + " " + user.getContactInfo().getSecondName() + " already exist."),HttpStatus.CONFLICT);
		}
		userService.saveUser(user);
		contactInfoService.saveInfo(user.getContactInfo());
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody User user) {
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
			template.delete("http://localhost:9090/bloggers/blogger/{id}", id);
		if(user.getRole().equals("Advertiser"))
			template.delete("http://localhost:8585/orders/advertiser/{id}", id);
		userService.deleteUserById(id);
		contactInfoService.deleteInfoById(id);
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVideo(@PathVariable("id") int id) {
		logger.info("Deleting Video with id {}", id);

		RestTemplate template = new RestTemplate();
		template.delete("http://localhost:9090/bloggers/video/{id}", id);
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/order/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteOrder(@PathVariable("id") int id) {
		logger.info("Deleting Order with id {}", id);

		RestTemplate template = new RestTemplate();
		template.delete("http://localhost:8585/orders/order/{id}", id);
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

}