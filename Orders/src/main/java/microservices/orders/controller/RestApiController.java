package microservices.orders.controller;

import java.util.*;

import microservices.orders.bloggers.Blogger;
import microservices.orders.bloggers.Video;
import microservices.orders.model.Advertiser;
import microservices.orders.model.Order;
import microservices.orders.service.AdvertiserService;
import microservices.orders.service.OrderService;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import microservices.orders.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	AdvertiserService advertiserService;

	@Autowired
	OrderService orderService;

	@RequestMapping(value = "/advertiser/", method = RequestMethod.GET)
	public ResponseEntity<List<Advertiser>> listAllAdvertisers() {
		List<Advertiser> advertisers = advertiserService.findAllAdvertisers();
		if (advertisers.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Advertiser>>(advertisers, HttpStatus.OK);
	}

	@RequestMapping(value = "/order/", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listAllOrders() {
		List<Order> orders = orderService.findAllOrders();
		if (orders.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
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

	@RequestMapping(value = "/order/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOrder(@PathVariable("id") int id) {
		logger.info("Fetching Order with id {}", id);
		Order order = orderService.findById(id);
		if (order == null) {
			logger.error("Order with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Order with id " + id
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Order>(order, HttpStatus.OK);
	}

	@RequestMapping(value = "/order_advertiser/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listOrdersForAdvertiser(@PathVariable("id") int id) {
		List<Order> orders = orderService.findOrdersForAdvertiser(id);
		if (orders.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
	}

	@RequestMapping(value = "/order_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listOrdersForBlogger(@PathVariable("id") int id) {
		List<Order> orders = orderService.findOrdersForBlogger(id);
		if (orders.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
	}

	@RequestMapping(value = "/advertiser/", method = RequestMethod.POST)
	public ResponseEntity<?> createAdvertiser(@RequestBody Advertiser advertiser) {
		logger.info("Creating Advertiser : {}", advertiser);

		if (advertiserService.isAdvertiserExist(advertiser)) {
			logger.error("Unable to create. A Advertiser with id {} already exist", advertiser.getId());
			return new ResponseEntity(new CustomErrorType("Unable to create. A Advertiser with id " +
					advertiser.getId() + " already exist."),HttpStatus.CONFLICT);
		}
		advertiserService.saveAdvertiser(advertiser);

		return new ResponseEntity<Advertiser>(advertiser, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/order/", method = RequestMethod.POST)
	public ResponseEntity<?> createOrder(@RequestBody Order order) {
		logger.info("Creating Order : {}", order);

		if (orderService.isOrderExist(order)) {
			logger.error("Unable to create. A Order with id {} already exist", order.getId());
			return new ResponseEntity(new CustomErrorType("Unable to create. A Order id " +
					order.getId() + " already exist."),HttpStatus.CONFLICT);
		}

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity("http://localhost:9090/bloggers/blogger/", String.class);
		JSONArray array;

		int bloggerId = -1;

		try {
			array = new JSONArray(resp.getBody());
			double maxSum = -100;
			for(int i = 0; i < array.length(); i++)
			{
				Blogger blogger = new Blogger();
				blogger.setMinPrice(array.getJSONObject(i).getDouble("minPrice"));
				if(blogger.getMinPrice() > order.getSum())
					continue;
				blogger.setId(array.getJSONObject(i).getInt("id"));
				blogger.setCountOfSubscribers(array.getJSONObject(i).getInt("countOfSubscribers"));
				blogger.setStatus(array.getJSONObject(i).getString("status"));
				ResponseEntity<String> response = template.getForEntity("http://localhost:9090/bloggers/video_blogger/{id}", String.class, blogger.getId());
				JSONArray arr = new JSONArray(response.getBody());
				double coeff = 1;
				if(blogger.getStatus().equals("Bronze"))
					coeff = 1.1;
				if(blogger.getStatus().equals("Silver"))
					coeff = 1.2;
				if(blogger.getStatus().equals("Gold"))
					coeff = 1.3;
				if(blogger.getStatus().equals("Diamond"))
					coeff = 1.4;
				double sum = blogger.getCountOfSubscribers() * coeff;
				for (int j = 0; j < arr.length(); j++)
				{
					Video video = new Video();
					video.setTag(arr.getJSONObject(j).getString("tag"));
					if(!video.getTag().equals(order.getTag()))
						continue;
					video.setCountOfDislikes(arr.getJSONObject(j).getInt("countOfDislikes"));
					video.setCountOfLikes(arr.getJSONObject(j).getInt("countOfLikes"));
					video.setCountOfViews(arr.getJSONObject(j).getInt("countOfViews"));
					sum += Math.abs(video.getCountOfLikes() - video.getCountOfDislikes()) + video.getCountOfViews() / 1000;
				}
				if(sum > maxSum)
				{
					maxSum = sum;
					bloggerId = blogger.getId();
				}
			}
		}
		catch (Throwable t)
		{
			return new ResponseEntity(new CustomErrorType("Order creation failed"), HttpStatus.TOO_MANY_REQUESTS);
		}
		if(bloggerId == -1)
			return new ResponseEntity(new CustomErrorType("Order creation failed"), HttpStatus.TOO_MANY_REQUESTS);
		order.setBlogger(bloggerId);
		orderService.saveOrder(order);

		return new ResponseEntity<Order>(order, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/advertiser/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateAdvertiser(@PathVariable("id") int id, @RequestBody Advertiser advertiser) {
		logger.info("Updating Advertiser with id {}", id);

		Advertiser currentAdvertiser = advertiserService.findById(id);

		if (currentAdvertiser == null) {
			logger.error("Unable to update. Advertiser with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to upate. Advertiser with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		currentAdvertiser.setLogin(advertiser.getLogin());
		currentAdvertiser.setAccount(advertiser.getAccount());


		advertiserService.updateAdvertiser(currentAdvertiser);
		return new ResponseEntity<Advertiser>(currentAdvertiser, HttpStatus.OK);
	}

	@RequestMapping(value = "/order/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateOrder(@PathVariable("id") int id, @RequestBody Order order) {
		logger.info("Updating Order with id {}", id);

		Order currentOrder = orderService.findById(id);

		if (currentOrder == null) {
			logger.error("Unable to update. Order with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to upate. Order with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		if(currentOrder.getState().equals("InProgress")) {
			RestTemplate template = new RestTemplate();
			ResponseEntity<String> resp = template.getForEntity("http://localhost:9090/bloggers/blogger/", String.class);
			JSONArray array;

			int bloggerId = -1;

			try {
				array = new JSONArray(resp.getBody());
				double maxSum = -100;
				for (int i = 0; i < array.length(); i++) {
					Blogger blogger = new Blogger();
					blogger.setMinPrice(array.getJSONObject(i).getDouble("minPrice"));
					if (blogger.getMinPrice() > order.getSum())
						continue;
					blogger.setId(array.getJSONObject(i).getInt("id"));
					blogger.setCountOfSubscribers(array.getJSONObject(i).getInt("countOfSubscribers"));
					blogger.setStatus(array.getJSONObject(i).getString("status"));
					ResponseEntity<String> response = template.getForEntity("http://localhost:9090/bloggers/video_blogger/{id}", String.class, blogger.getId());
					JSONArray arr = new JSONArray(response.getBody());
					double coeff = 1;
					if (blogger.getStatus().equals("Bronze"))
						coeff = 1.1;
					if (blogger.getStatus().equals("Silver"))
						coeff = 1.2;
					if (blogger.getStatus().equals("Gold"))
						coeff = 1.3;
					if (blogger.getStatus().equals("Diamond"))
						coeff = 1.4;
					double sum = blogger.getCountOfSubscribers() * coeff;
					for (int j = 0; j < arr.length(); j++) {
						Video video = new Video();
						video.setTag(arr.getJSONObject(j).getString("tag"));
						if (!video.getTag().equals(order.getTag()))
							continue;
						video.setCountOfDislikes(arr.getJSONObject(j).getInt("countOfDislikes"));
						video.setCountOfLikes(arr.getJSONObject(j).getInt("countOfLikes"));
						video.setCountOfViews(arr.getJSONObject(j).getInt("countOfViews"));
						sum += Math.abs(video.getCountOfLikes() - video.getCountOfDislikes()) + video.getCountOfViews() / 1000;
					}
					if (sum > maxSum) {
						maxSum = sum;
						bloggerId = blogger.getId();
					}
				}
			} catch (Throwable t) {
				return new ResponseEntity(new CustomErrorType("Order creation failed"), HttpStatus.TOO_MANY_REQUESTS);
			}
			if (bloggerId == -1)
				return new ResponseEntity(new CustomErrorType("Order creation failed"), HttpStatus.TOO_MANY_REQUESTS);

			currentOrder.setName(order.getName());
			currentOrder.setDescription(order.getDescription());
			currentOrder.setStartDate(order.getStartDate());
			currentOrder.setEndDate(order.getEndDate());
			currentOrder.setLastUpdateDate(new java.util.Date());
			currentOrder.setOwner(order.getOwner());
			currentOrder.setState(order.getState());
			currentOrder.setSum(order.getSum());
			currentOrder.setTag(order.getTag());
			currentOrder.setBlogger(bloggerId);
		}
		else
		{
			return new ResponseEntity(new CustomErrorType("Unable to update. Order with id " + id + " isn't in progress."),
					HttpStatus.BAD_REQUEST);
		}
		orderService.updateOrder(currentOrder);
		return new ResponseEntity<Order>(currentOrder, HttpStatus.OK);
	}

	@RequestMapping(value = "/advertiser/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteAdvertiser(@PathVariable("id") int id) {
		logger.info("Deleting Advertiser with id {}", id);

		Advertiser advertiser = advertiserService.findById(id);
		if (advertiser == null) {
			logger.error("Unable to delete. Advertiser with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. Advertiser with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		advertiserService.deleteAdvertiserById(id);
		//orderService.deleteAllOrders(id);
		return new ResponseEntity<Advertiser>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/order/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteOrder(@PathVariable("id") int id) {
		logger.info("Deleting Order with id {}", id);

		Order order = orderService.findById(id);
		if (order == null) {
			logger.error("Unable to delete. Order with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. Order with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		if(!order.getState().equals("Done"))
			orderService.deleteOrderById(id);
		else
			return new ResponseEntity(new CustomErrorType("Unable to delete. Order with id " + id + " is done."),
					HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Order>(HttpStatus.NO_CONTENT);
	}

}