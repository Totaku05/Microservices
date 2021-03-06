package microservices.gateway.controller;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.util.*;

import microservices.gateway.service.GatewayService;
import microservices.gateway.videos.Video;
import microservices.gateway.users.*;
import microservices.gateway.orders.Order;
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

import microservices.gateway.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	GatewayService gatewayService;

	@RequestMapping(value = "/user/", method = RequestMethod.GET)
	public ResponseEntity<?> listAllUsers() {
		logger.info("Fetching all Users");

		RestTemplate template = new RestTemplate();
		/*ResponseEntity<String> resp = template.getForEntity("http://localhost:9898/users/user/", String.class);
		return gatewayService.listUsers(resp);*/

		Order order = new Order();
		order.setId(6);
		order.setName("name");
		order.setDescription("description");
		order.setTag("Food");
		order.setSum(100);

		order.setStartDate(new Date());
		order.setLastUpdateDate(new Date());

		order.setState("InProgress");
		order.setOwner(4);

		/*order.setState("InReview");
		HttpEntity<Order> entity = new HttpEntity<Order>(order);
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", Integer.toString(order.getOwner()));
		template.exchange("http://localhost:8585/orders/order/status/{id}", HttpMethod.PUT, entity, Order.class, order.getId());*/
		Order o = template.postForObject("http://localhost:8585/orders/order/", order, Order.class);

		/*Video video = new Video();
		video.setOwner(6);
		video.setCountOfLikes(500);
		video.setName("video");
		video.setCountOfDislikes(1000);
		video.setCountOfViews(2000);
		video.setDescription("description");
		video.setDuration(new Time(0));
		video.setTag("Food");
		video.setDateOfCreation(new Date());
		video.setId(150);
		Video v = template.postForObject("http://localhost:9090/videos/video/", video, Video.class);*/



		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/status/", method = RequestMethod.GET)
	public ResponseEntity<?> listAllStatuses() {
		logger.info("Fetching all Statuses");

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:9898/users/statuses/", String.class);

		return gatewayService.listStatuses(resp);
	}

	@RequestMapping(value = "/video_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> listVideosForBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Videos for Blogger with id {}", id);

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:9090/videos/video_blogger/{id}", String.class, id);

		return gatewayService.listVideos(resp);
	}

	@RequestMapping(value = "/order_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> listOrdersForBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Orders for Blogger with id {}", id);

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:8585/orders/order_blogger/{id}", String.class, id);

		return gatewayService.listOrders(resp);
	}

	@RequestMapping(value = "/order_advertiser/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> listOrdersForAdvertiser(@PathVariable("id") int id) {
		logger.info("Fetching Orders for Advertiser with id {}", id);

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:8585/orders/order_advertiser/{id}", String.class, id);

		return gatewayService.listOrders(resp);
	}

	@RequestMapping(value = "/video_order/{id}", method = RequestMethod.GET)
	public ResponseEntity<Video> getVideoByOrder(@PathVariable("id") int id) {
		logger.info("Fetching Video by order with id {}", id);

		RestTemplate template = new RestTemplate();
		Video video = template.getForObject("http://localhost:9090/videos/video_order/{id}", Video.class, id);

		return new ResponseEntity<Video>(video, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("id") int id) {
		logger.info("Fetching User with id {}", id);

		RestTemplate template = new RestTemplate();
		User user = template.getForObject("http://localhost:9898/users/user/{id}", User.class, id);

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/advertiser/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getAdvertiser(@PathVariable("id") int id) {
		logger.info("Fetching Advertiser with id {}", id);

		RestTemplate template = new RestTemplate();
		Advertiser advertiser = template.getForObject("http://localhost:9898/users/advertiser/{id}", Advertiser.class, id);

		return new ResponseEntity<Advertiser>(advertiser, HttpStatus.OK);
	}

	@RequestMapping(value = "/blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Blogger with id {}", id);

		RestTemplate template = new RestTemplate();
		Blogger blogger = template.getForObject("http://localhost:9898/users/blogger/{id}", Blogger.class, id);

		return new ResponseEntity<Blogger>(blogger, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{login}/{password}", method = RequestMethod.GET)
	public ResponseEntity<?> Identify(@PathVariable("login") String login, @PathVariable("password") String password) {
		logger.info("Fetching User with login {}", login);

		User user = null;
		try {
			RestTemplate template = new RestTemplate();
			user = template.getForObject("http://localhost:9898/users/user/{login}/{password}", User.class, login, password);
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
		User u = template.postForObject("http://localhost:9898/users/user/", user, User.class);
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

		return gatewayService.createOrder(order);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody User user, @RequestBody Blogger blogger, @RequestBody Advertiser advertiser) {
		logger.info("Updating User with id {}", id);

		User currentUser = gatewayService.updateUser(id, user);
		if (currentUser == null) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to update. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		if (currentUser.getRole().equals("Advertiser"))
			gatewayService.updateAdvertiser(currentUser, advertiser);
		if (currentUser.getRole().equals("Blogger"))
			gatewayService.updateBlogger(currentUser, blogger);

		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/status/{id}/{status}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateBloggerStatus(@PathVariable("id") int id, @PathVariable("status") String status) {
		logger.info("Updating User with id {}", id);

		try {
			RestTemplate template = new RestTemplate();
			Map<String, String> param = new HashMap<String, String>();
			param.put("id", Integer.toString(id));
			param.put("status", status);

			template.put("http://localhost:9898/users/status/{id}/{status}", null, param);
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
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/minPrice/{id}/{minPrice}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateBloggerMinPrice(@PathVariable("id") int id, @PathVariable("minPrice") Double minPrice) {
		logger.info("Updating User with id {}", id);

		try {
			RestTemplate template = new RestTemplate();
			Map<String, String> param = new HashMap<String, String>();
			param.put("id", Integer.toString(id));
			param.put("minPrice", Double.toString(minPrice));

			template.put("http://localhost:9898/users/minPrice/{id}/{minPrice}", null, param);
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
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/user_account/{id}/{sum}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAccount(@PathVariable("id") int id, @PathVariable("sum") double sum) {
		logger.info("Updating Account for User with id {}", id);

		RestTemplate template = new RestTemplate();
		try {
			Map<String, String> param = new HashMap<String, String>();
			param.put("id", Integer.toString(id));
			param.put("sum", Double.toString(sum));
			param.put("external", Boolean.toString(true));

			template.put("http://localhost:9898/users/user_account/{id}/{sum}/{external}", null, param);
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

		return gatewayService.updateVideo(currentVideo);
	}

	@RequestMapping(value = "/order_status/", method = RequestMethod.PUT)
	public ResponseEntity<?> updateOrderStatus(@RequestBody Order order) {
		RestTemplate template = new RestTemplate();
		Order currentOrder = template.getForObject("http://localhost:8585/orders/order/{id}", Order.class, order.getId());

		currentOrder.setState(order.getState());
		return gatewayService.updateOrderStatus(currentOrder);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
		logger.info("Deleting User with id {}", id);

		RestTemplate template = new RestTemplate();
		try {
			template.delete("http://localhost:9898/users/user/{id}", id);
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