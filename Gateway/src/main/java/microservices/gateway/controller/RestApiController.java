package microservices.gateway.controller;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import microservices.gateway.videos.Video;
import microservices.gateway.users.*;
import microservices.gateway.orders.Order;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import microservices.gateway.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	private User convertJsonToUser(JSONObject object)
	{
		User user = new User();
		ContactInfo info = new ContactInfo();

		try {
			user.setId(object.getInt("id"));
			user.setLogin(object.getString("login"));
			user.setRole(object.getString("role"));

			JSONObject jInfo = new JSONObject(object.getString("contactInfo"));
			info.setEmail(jInfo.getString("email"));
			info.setFirstName(jInfo.getString("firstName"));
			info.setSecondName(jInfo.getString("secondName"));
			info.setPhoneNumber(jInfo.getString("phoneNumber"));
			user.setContactInfo(info);
		}
		catch (Throwable t)
		{
			return null;
		}
		return user;
	}

	private Video convertJsonToVideo(JSONObject object)
	{
		Video video = new Video();

		try {
            video.setId(object.getInt("id"));
			video.setName(object.getString("name"));
			video.setDescription(object.getString("description"));
			video.setTag(object.getString("tag"));

            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            video.setDuration(new Time(formatter.parse(object.getString("duration")).getTime()));
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(object.getString("dateOfCreation"));
            video.setDateOfCreation(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));

			video.setCountOfLikes(object.getInt("countOfLikes"));
			video.setCountOfDislikes(object.getInt("countOfDislikes"));
			video.setCountOfViews(object.getInt("countOfViews"));
            if(!object.getString("completed_order").equals("null"))
                video.setCompleted_order(object.getInt("completed_order"));
			video.setOwner(object.getInt("owner"));
		}
		catch (Throwable t)
		{
			return null;
		}
		return video;
	}

	private Order convertJsonToOrder(JSONObject object)
	{
		Order order = new Order();

		try {
            order.setId(object.getInt("id"));
			order.setName(object.getString("name"));
			order.setDescription(object.getString("description"));
			order.setTag(object.getString("tag"));
			order.setSum(object.getDouble("sum"));

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(object.getString("startDate"));
            order.setStartDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
            date = formatter.parse(object.getString("lastUpdateDate"));
            order.setLastUpdateDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
            if(!object.getString("endDate").equals("null")) {
                date = formatter.parse(object.getString("endDate"));
                order.setEndDate(new java.sql.Date(date.getYear(), date.getMonth(), date.getDate()));
            }

			order.setState(object.getString("state"));
			order.setBlogger(object.getInt("blogger"));
			order.setOwner(object.getInt("owner"));
		}
		catch (Throwable t)
		{
			return null;
		}
		return order;
	}

	@RequestMapping(value = "/user/", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		logger.info("Fetching all Users");

		RestTemplate template = new RestTemplate();
		List<User> users = new LinkedList<User>();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:8080/users/user/", String.class);
		try {
			JSONArray array = new JSONArray(resp.getBody());
			for(int i = 0; i < array.length(); i++)
			{
				JSONObject object = array.getJSONObject(i);
				User user = convertJsonToUser(object);
				if(user != null)
					users.add(user);
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Fetching all Users failed"), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/status/", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllStatuses() {
		logger.info("Fetching all Statuses");

		RestTemplate template = new RestTemplate();
		List<Pair<String, Double>> statuses = new LinkedList<Pair<String, Double>>();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:8080/users/status/", String.class);
		try {
			JSONArray array = new JSONArray(resp.getBody());
			for(int i = 0; i < array.length(); i++)
			{
				JSONObject object = array.getJSONObject(i);
				Pair<String, Double> pair = new ImmutablePair<String, Double>(object.getString("status"), object.getDouble("sum"));
				statuses.add(pair);
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Fetching all Users failed"), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/video_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Video>> listVideosForBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Videos for Blogger with id {}", id);

		RestTemplate template = new RestTemplate();
		List<Video> videos = new LinkedList<Video>();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:9090/videos/video_blogger/{id}", String.class, id);

		try {
			JSONArray array = new JSONArray(resp.getBody());
			for(int i = 0; i < array.length(); i++)
			{
				JSONObject object = array.getJSONObject(i);
				Video video = convertJsonToVideo(object);
				if(video != null)
					videos.add(video);
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Fetching Videos for Blogger"), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Video>>(videos, HttpStatus.OK);
	}

	@RequestMapping(value = "/order_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listOrdersForBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Orders for Blogger with id {}", id);

		RestTemplate template = new RestTemplate();
		List<Order> orders = new LinkedList<Order>();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:8585/orders/order_blogger/{id}", String.class, id);

		try {
			JSONArray array = new JSONArray(resp.getBody());
			for(int i = 0; i < array.length(); i++)
			{
				JSONObject object = array.getJSONObject(i);
				Order order = convertJsonToOrder(object);
				if(order != null)
					orders.add(order);
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Fetching Videos for Blogger"), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
	}

	@RequestMapping(value = "/order_advertiser/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listOrdersForAdvertiser(@PathVariable("id") int id) {
		logger.info("Fetching Orders for Advertiser with id {}", id);

		RestTemplate template = new RestTemplate();
		List<Order> orders = template.getForObject("http://localhost:8585/orders/order_advertiser/{id}", List.class, id);

		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
	}

	@RequestMapping(value = "/video_order/{id}", method = RequestMethod.GET)
	public ResponseEntity<Video> getVideoByOrder(@PathVariable("id") int id) {
		logger.info("Fetching Video by order with id {}", id);

		RestTemplate template = new RestTemplate();
		Video video = null;

		try {
			video = template.getForObject("http://localhost:9090/videos/video_order/{id}", Video.class, id);
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("Video fetching by order failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<Video>(video, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("id") int id) {
		logger.info("Fetching User with id {}", id);

		RestTemplate template = new RestTemplate();
		User user = template.getForObject("http://localhost:8080/users/user/{id}", User.class, id);

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/advertiser/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getAdvertiser(@PathVariable("id") int id) {
		logger.info("Fetching Advertiser with id {}", id);

		RestTemplate template = new RestTemplate();
		Advertiser advertiser = template.getForObject("http://localhost:8080/users/advertiser/{id}", Advertiser.class, id);

		return new ResponseEntity<Advertiser>(advertiser, HttpStatus.OK);
	}

	@RequestMapping(value = "/blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Blogger with id {}", id);

		RestTemplate template = new RestTemplate();
		Blogger blogger = template.getForObject("http://localhost:8080/users/blogger/{id}", Blogger.class, id);

		return new ResponseEntity<Blogger>(blogger, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{login}/{password}", method = RequestMethod.GET)
	public ResponseEntity<?> Identify(@PathVariable("login") String login, @PathVariable("password") String password) {
		logger.info("Fetching User with login {}", login);

		User user = null;
		try {
			RestTemplate template = new RestTemplate();
			user = template.getForObject("http://localhost:8080/users/user/{login}/{password}", User.class, login, password);
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("Identify operation failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user) {
		logger.info("Creating User : {}", user);

		RestTemplate template = new RestTemplate();
		User u = template.postForObject("http://localhost:8080/users/user/", user, User.class);
		return new ResponseEntity<User>(u, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/video/", method = RequestMethod.POST)
	public ResponseEntity<?> createVideo(@RequestBody Video video) {
		logger.info("Creating Video : {}", video);

		RestTemplate template = new RestTemplate();
		Video v = template.postForObject("http://localhost:9090/videos/video/", video, Video.class);

		return new ResponseEntity<Video>(v, HttpStatus.CREATED);
	}

	//return blogger that would realise this order
	@RequestMapping(value = "/order/", method = RequestMethod.POST)
	public ResponseEntity<?> createOrder(@RequestBody Order order) {
		logger.info("Creating Order : {}", order);

		RestTemplate template = new RestTemplate();
		Blogger blogger = null;
		try {
			Order o = template.postForObject("http://localhost:8585/orders/order/", order, Order.class);
			template.put("http://localhost:8080/users/user_account/{id}/{sum}", order.getOwner(), -order.getSum());
			blogger = template.getForObject("http://localhost:8080/users/blogger/{id}", Blogger.class, order.getBlogger());
			User user = template.getForObject("http://localhost:8080/users/user/{id}", User.class, order.getBlogger());
			String message = "You have new order.";
            template.put("http://localhost:9797/notification/send_message/{email}/{message}", user.getContactInfo().getEmail(), message);
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("Order creation failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<Blogger>(blogger, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody User user, @RequestBody Blogger blogger, @RequestBody Advertiser advertiser) {
		logger.info("Updating User with id {}", id);

		User currentUser = null;
		try {
			RestTemplate template = new RestTemplate();
			currentUser = template.getForObject("http://localhost:8080/user/user/{id}", User.class, id);

			if (currentUser == null) {
				logger.error("Unable to update. User with id {} not found.", id);
				return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
						HttpStatus.NOT_FOUND);
			}

			currentUser.setLogin(user.getLogin());
			currentUser.setPassword(user.getPassword());
			currentUser.setRole(user.getRole());
			currentUser.setContactInfo(user.getContactInfo());

			template.put("http://localhost:8080/users/user/{id}", currentUser, currentUser.getId());

			if (currentUser.getRole().equals("Advertiser")) {
				Advertiser currentAdvertiser = template.getForObject("http://localhost:8080/users/advertiser/{id}", Advertiser.class, id);
				currentAdvertiser.setId(currentUser.getId());
				currentAdvertiser.setLogin(currentUser.getLogin());
				currentAdvertiser.setCard_number(advertiser.getCard_number());
				template.put("http://localhost:8080/users/advertiser/{id}", currentAdvertiser, currentAdvertiser.getId());
			}
			if (currentUser.getRole().equals("Blogger")) {
				Blogger currentBlogger = template.getForObject("http://localhost:8080/users/blogger/{id}", Blogger.class, id);
				currentBlogger.setId(currentUser.getId());
				currentBlogger.setLogin(currentUser.getLogin());
				currentBlogger.setStatus(blogger.getStatus());
				currentBlogger.setCountOfSubscribers(blogger.getCountOfSubscribers());
				currentBlogger.setMinPrice(blogger.getMinPrice());
				currentBlogger.setCard_number(blogger.getCard_number());
				template.put("http://localhost:8080/users/blogger/{id}", currentBlogger, currentBlogger.getId());
			}
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("User updating failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/user_account/{id}/{sum}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAccount(@PathVariable("id") int id, @PathVariable("sum") double sum) {
		logger.info("Updating Account for User with id {}", id);

		RestTemplate template = new RestTemplate();
		try {
			template.put("http://localhost:8080/users/user_account/{id}/{sum}", id, sum);
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("User account updating failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateVideo(@PathVariable("id") int id, @RequestBody Video video) {
		RestTemplate template = new RestTemplate();
		Video currentVideo = template.getForObject("http://localhost:9090/videos/video/{id}", Video.class, id);
		currentVideo.setTag(video.getTag());
		currentVideo.setName(video.getName());
		currentVideo.setDuration(video.getDuration());
		currentVideo.setDescription(video.getDescription());
		currentVideo.setCountOfViews(video.getCountOfViews());
		currentVideo.setCountOfLikes(video.getCountOfLikes());
		currentVideo.setCountOfDislikes(video.getCountOfDislikes());
		currentVideo.setCompleted_order(video.getCompleted_order());
		try {
			template.put("http://localhost:9090/videos/video/{id}", currentVideo, currentVideo.getId());
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("Video updating failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		if(currentVideo.getCompleted_order() != null)
		{
			//send message to admin
		}
		return new ResponseEntity<Video>(currentVideo, HttpStatus.OK);
	}

	@RequestMapping(value = "/order_status/", method = RequestMethod.PUT)
	public ResponseEntity<?> updateOrderStatus(@RequestBody Order order) {
		RestTemplate template = new RestTemplate();
		Order currentOrder = template.getForObject("http://localhost:8585/orders/order/{id}", Order.class, order.getId());

		currentOrder.setState(order.getState());
		try {
			template.put("http://localhost:8585/orders/status/{id}", currentOrder, currentOrder.getId());
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("Order status updating failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		if(currentOrder.getState().equals("Done"))
		{
            User blogger = template.getForObject("http://localhost:8080/users/user/{id}", User.class, order.getBlogger());
            User advertiser = template.getForObject("http://localhost:8080/users/user/{id}", User.class, order.getOwner());
            String message = "Status of your order is changed.";
            template.put("http://localhost:9797/notification/send_message/{email}/{message}", blogger.getContactInfo().getEmail(), message);
            template.put("http://localhost:9797/notification/send_message/{email}/{message}", advertiser.getContactInfo().getEmail(), message);
		}
		return new ResponseEntity<Order>(currentOrder, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
		logger.info("Deleting User with id {}", id);

		RestTemplate template = new RestTemplate();
		try {
			template.delete("http://localhost:8080/users/user/{id}", id);
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("User deleting failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVideo(@PathVariable("id") int id) {
		logger.info("Deleting Video with id {}", id);

		RestTemplate template = new RestTemplate();
		try {
			template.delete("http://localhost:9090/videos/video/{id}", id);
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("Video deleting failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/order/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteOrder(@PathVariable("id") int id) {
		logger.info("Deleting Order with id {}", id);

		RestTemplate template = new RestTemplate();
		try {
			template.delete("http://localhost:8585/orders/order/{id}", id);
		}
		catch (HttpClientErrorException e)
		{
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			}
			catch (Throwable t)
			{
				return new ResponseEntity(new CustomErrorType("Order deleting failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

}