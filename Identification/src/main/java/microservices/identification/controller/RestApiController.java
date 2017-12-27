package microservices.identification.controller;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import microservices.identification.bloggers.Blogger;
import microservices.identification.bloggers.Video;
import microservices.identification.model.ContactInfo;
import microservices.identification.model.User;
import microservices.identification.orders.Advertiser;
import microservices.identification.orders.Order;
import microservices.identification.service.ContactInfoService;
import microservices.identification.service.UserService;
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
import org.springframework.web.client.RestTemplate;

import microservices.identification.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	UserService userService;

	@Autowired
	ContactInfoService contactInfoService;

	//int counter = 11;
	
	@RequestMapping(value = "/user/", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = userService.findAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}

		/*RestTemplate template = new RestTemplate();
		Order order = new Order();
		order.setId(6);
		order.setStartDate(new Date());
		order.setOwner(1);
		order.setTag("Food");
		order.setSum(10.0);
		order.setState("InProgress");
		order.setLastUpdateDate(new Date());
		order.setName("hoho");
		order.setDescription("noname");
		template.put("http://localhost:8585/orders/order/{id}", order, order.getId());*/
		//Order o = template.postForObject("http://localhost:8585/orders/order/", order, Order.class);*/


		//RestTemplate template = new RestTemplate();
		//List<Video> videos = template.getForObject("http://localhost:9090/bloggers/video/", List.class);

		//Blogger blogger = template.getForObject("http://localhost:9090/bloggers/blogger/{id}", Blogger.class, 6);

		//blogger.setLogin("Son Que");

		//template.put("http://localhost:9090/bloggers/blogger/{id}", blogger, 6);

		/*Video video = new Video();
		video.setId(8);
		video.setTag("Food");
		video.setOwner(6);
		video.setName("Second video");
		video.setDescription("!!!Second video!!!");
		video.setDuration(new Time(0, 5, 0));
		video.setDateOfCreation(new java.util.Date());
		video.setCountOfViews(50000);
		video.setCountOfLikes(1000);
		video.setCountOfDislikes(100);

		RestTemplate template = new RestTemplate();
		Video v = template.postForObject("http://localhost:9090/bloggers/video/", video, Video.class);*/

		for(User user : users)
			user.setContactInfo(contactInfoService.findById(user.getId()));
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/video_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Video>> listVideosForBlogger(@PathVariable("id") int id) {
		RestTemplate template = new RestTemplate();
		String resp = template.getForObject("http://localhost:9090/bloggers/video_blogger/{id}", String.class, id);
		List<Video> videos = new ArrayList<Video>();
		try {
			JSONArray objects = new JSONArray(resp);
			for (int i = 0; i < objects.length(); i++)
			{
				Video video = new Video();
				JSONObject object = objects.getJSONObject(i);
				video.setId(object.getInt("id"));
				if(!object.getString("completed_order").equals("null"))
					video.setCompleted_order(object.getInt("completed_order"));
				video.setTag(object.getString("tag"));
				video.setOwner(object.getInt("owner"));
				video.setName(object.getString("name"));
				video.setDescription(object.getString("description"));
				DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				video.setDuration(new Time(formatter.parse(object.getString("duration")).getTime()));
				formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = formatter.parse(object.getString("dateOfCreation"));
				video.setDateOfCreation(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
				video.setCountOfViews(object.getInt("countOfViews"));
				video.setCountOfLikes(object.getInt("countOfLikes"));
				video.setCountOfDislikes(object.getInt("countOfDislikes"));
				videos.add(video);
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Json parsing exception"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<Video>>(videos, HttpStatus.OK);
	}

	@RequestMapping(value = "/order_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listOrdersForBlogger(@PathVariable("id") int id) {
		RestTemplate template = new RestTemplate();
		String resp = template.getForObject("http://localhost:8585/orders/order_blogger/{id}", String.class, id);
		List<Order> orders = new ArrayList<Order>();
		try {
			JSONArray objects = new JSONArray(resp);
			for (int i = 0; i < objects.length(); i++) {
				JSONObject object = objects.getJSONObject(i);
				Order order = new Order();
				order.setId(object.getInt("id"));
				order.setBlogger(object.getInt("blogger"));
				order.setTag(object.getString("tag"));
				order.setSum(object.getDouble("sum"));
				order.setState(object.getString("state"));
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = formatter.parse(object.getString("startDate"));
				order.setStartDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
				date = formatter.parse(object.getString("lastUpdateDate"));
				order.setLastUpdateDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
				if(!object.getString("endDate").equals("null")) {
					date = formatter.parse(object.getString("endDate"));
					order.setEndDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
				}
				order.setOwner(object.getInt("owner"));
				order.setName(object.getString("name"));
				order.setDescription(object.getString("description"));
				orders.add(order);
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Json parsing exception"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
	}

	@RequestMapping(value = "/order_advertiser/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listOrdersForAdvertiser(@PathVariable("id") int id) {
		RestTemplate template = new RestTemplate();
		String resp = template.getForObject("http://localhost:8585/orders/order_advertiser/{id}", String.class, id);
		List<Order> orders = new ArrayList<Order>();
		try {
			JSONArray objects = new JSONArray(resp);
			for (int i = 0; i < objects.length(); i++) {
				JSONObject object = objects.getJSONObject(i);
				Order order = new Order();
				order.setId(object.getInt("id"));
				order.setBlogger(object.getInt("blogger"));
				order.setTag(object.getString("tag"));
				order.setSum(object.getDouble("sum"));
				order.setState(object.getString("state"));
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = formatter.parse(object.getString("startDate"));
				order.setStartDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
				date = formatter.parse(object.getString("lastUpdateDate"));
				order.setLastUpdateDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
				if(!object.getString("endDate").equals("null")) {
					date = formatter.parse(object.getString("endDate"));
					order.setEndDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
				}
				order.setOwner(object.getInt("owner"));
				order.setName(object.getString("name"));
				order.setDescription(object.getString("description"));
				orders.add(order);
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Json parsing exception"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
	}

	@RequestMapping(value = "/video_order/{id}", method = RequestMethod.GET)
	public ResponseEntity<Video> getVideoByOrder(@PathVariable("id") int id) {
		RestTemplate template = new RestTemplate();
		Video video = template.getForObject("http://localhost:9090/bloggers/video_order/{id}", Video.class, id);
		return new ResponseEntity<Video>(video, HttpStatus.OK);
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
		RestTemplate template = new RestTemplate();
		Advertiser advertiser = template.getForObject("http://localhost:8585/orders/advertiser/{id}", Advertiser.class, id);

		return new ResponseEntity<Advertiser>(advertiser, HttpStatus.OK);
	}

	@RequestMapping(value = "/blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Blogger with id {}", id);
		RestTemplate template = new RestTemplate();
		Blogger blogger = template.getForObject("http://localhost:9090/bloggers/blogger/{id}", Blogger.class, id);

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

	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user) {
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
			template.postForObject("http://localhost:8585/orders/advertiser/", advertiser, Advertiser.class);
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
		}
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/video/", method = RequestMethod.POST)
	public ResponseEntity<?> createVideo(@RequestBody Video video) {
		logger.info("Creating Video : {}", video);

		RestTemplate template = new RestTemplate();
		Video v = template.postForObject("http://localhost:9090/bloggers/video/", video, Video.class);

		return new ResponseEntity<Video>(video, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/order/", method = RequestMethod.POST)
	public ResponseEntity<?> createOrder(@RequestBody Order order) {
		logger.info("Creating Order : {}", order);

		RestTemplate template = new RestTemplate();
		Order o = template.postForObject("http://localhost:8585/orders/order/", order, Order.class);
		Advertiser advertiser = template.getForObject("http://localhost:8585/orders/advertiser/{id}", Advertiser.class, order.getOwner());
		advertiser.setAccount(advertiser.getAccount() - order.getSum());
		template.put("http://localhost:8585/orders/advertiser/{id}", advertiser, advertiser.getId());

		return new ResponseEntity<Order>(o, HttpStatus.CREATED);
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
			Advertiser currentAdvertiser = template.getForObject("http://localhost:8585/orders/advertiser/{id}", Advertiser.class, id);
			currentAdvertiser.setId(currentUser.getId());
			currentAdvertiser.setLogin(currentUser.getLogin());
			template.put("http://localhost:8585/orders/advertiser/{id}", currentAdvertiser, currentAdvertiser.getId());
		}
		if(currentUser.getRole().equals("Blogger"))
		{
			Blogger currentBlogger = template.getForObject("http://localhost:9090/bloggers/blogger/{id}", Blogger.class, id);
			currentBlogger.setId(currentUser.getId());
			currentBlogger.setLogin(currentUser.getLogin());
			currentBlogger.setStatus(blogger.getStatus());
			currentBlogger.setCountOfSubscribers(blogger.getCountOfSubscribers());
			currentBlogger.setMinPrice(blogger.getMinPrice());
			template.put("http://localhost:9090/bloggers/blogger/{id}", currentBlogger, currentBlogger.getId());
		}

		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/user_account/{id}/{sum}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAccount(@PathVariable("id") int id, @PathVariable("sum") double sum) {
		logger.info("Updating Account for User with id {}", id);

		User currentUser = userService.findById(id);

		if (currentUser == null) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		RestTemplate template = new RestTemplate();
		if(currentUser.getRole().equals("Advertiser"))
		{
			Advertiser currentAdvertiser = template.getForObject("http://localhost:8585/orders/advertiser/{id}", Advertiser.class, id);
			if(currentAdvertiser.getAccount() + sum < 0)
			{
				logger.error("Unable to update. Bad sum of money.", id);
				return new ResponseEntity(new CustomErrorType("Unable to upate. Bad sum of money."),
						HttpStatus.BAD_REQUEST);
			}
			currentAdvertiser.setAccount(currentAdvertiser.getAccount() + sum);
			template.put("http://localhost:8585/orders/advertiser/{id}", currentAdvertiser, currentAdvertiser.getId());
		}
		if(currentUser.getRole().equals("Blogger"))
		{
			Blogger currentBlogger = template.getForObject("http://localhost:9090/bloggers/blogger/{id}", Blogger.class, id);
			if(currentBlogger.getAccount() + sum < 0)
			{
				logger.error("Unable to update. Bad sum of money.", id);
				return new ResponseEntity(new CustomErrorType("Unable to upate. Bad sum of money."),
						HttpStatus.BAD_REQUEST);
			}
			currentBlogger.setAccount(currentBlogger.getAccount() + sum);
			template.put("http://localhost:9090/bloggers/blogger/{id}", currentBlogger, currentBlogger.getId());
		}

		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/order/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateOrder(@PathVariable("id") int id, @RequestBody Order order) {
		RestTemplate template = new RestTemplate();
		Order currentOrder = template.getForObject("http://localhost:8585/orders/order/{id}", Order.class, id);
		if(!currentOrder.getState().equals("Done")) {
			Advertiser advertiser = template.getForObject("http://localhost:8585/orders/advertiser/{id}", Advertiser.class, order.getOwner());
			advertiser.setAccount(advertiser.getAccount() + currentOrder.getSum() - order.getSum());
			currentOrder.setTag(order.getTag());
			currentOrder.setSum(order.getSum());
			currentOrder.setState(order.getState());
			currentOrder.setLastUpdateDate(new Date());
			currentOrder.setEndDate(order.getEndDate());
			currentOrder.setName(order.getName());
			currentOrder.setDescription(order.getDescription());
			template.put("http://localhost:8585/orders/order/{id}", currentOrder, currentOrder.getId());
			template.put("http://localhost:8585/orders/advertiser/{id}", advertiser, advertiser.getId());
			return new ResponseEntity<Order>(currentOrder, HttpStatus.OK);
		}
		return new ResponseEntity(new CustomErrorType("Unable to update. Order with id " + id + " is done."),
				HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateVideo(@PathVariable("id") int id, @RequestBody Video video) {
		RestTemplate template = new RestTemplate();
		Video currentVideo = template.getForObject("http://localhost:9090/bloggers/video/{id}", Video.class, id);
		currentVideo.setTag(video.getTag());
		currentVideo.setName(video.getName());
		currentVideo.setDuration(video.getDuration());
		currentVideo.setDescription(video.getDescription());
		currentVideo.setCountOfViews(video.getCountOfViews());
		currentVideo.setCountOfLikes(video.getCountOfLikes());
		currentVideo.setCountOfDislikes(video.getCountOfDislikes());
		currentVideo.setCompleted_order(video.getCompleted_order());
		template.put("http://localhost:9090/bloggers/video/{id}", currentVideo, currentVideo.getId());
		if(currentVideo.getCompleted_order() != null)
		{
			Blogger blogger = template.getForObject("http://localhost:9090/bloggers/blogger/{id}", Blogger.class, currentVideo.getOwner());
			Order order = template.getForObject("http://localhost:8585/orders/order/{id}", Order.class, currentVideo.getCompleted_order());
			blogger.setAccount(blogger.getAccount() + order.ge````````````````````````tSum());
			order.setState("Done");
			template.put("http://localhost:8585/orders/order/{id}", order, order.getId());
			template.put("http://localhost:9090/bloggers/blogger/{id}", blogger, blogger.getId());
		}
		return new ResponseEntity<Video>(currentVideo, HttpStatus.OK);
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