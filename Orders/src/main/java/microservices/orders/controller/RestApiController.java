package microservices.orders.controller;

import java.util.*;

import microservices.orders.entities.Blogger;
import microservices.orders.entities.Video;
import microservices.orders.model.Order;
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
	OrderService orderService;

	@RequestMapping(value = "/order/", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> listAllOrders() {
		List<Order> orders = orderService.findAllOrders();
		if (orders.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
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

	@RequestMapping(value = "/order/", method = RequestMethod.POST)
	public ResponseEntity<?> createOrder(@RequestBody String json_string) {
		Order order = orderService.convertJsonToOrder(json_string);

		if (order == null || orderService.isOrderExist(order)) {
			logger.error("Unable to create. Bad Order info.");
			return new ResponseEntity(new CustomErrorType("Unable to create. Bad Order info."),HttpStatus.CONFLICT);
		}
		logger.info("Creating Order : {}", order);

		return orderService.createOrder(order);
	}

	@RequestMapping(value = "/order/status/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateOrderStatus(@PathVariable("id") int id, @RequestBody String json_string) {
		Order order = orderService.convertJsonToOrder(json_string);

		Order currentOrder = orderService.findById(id);
		if(currentOrder == null) {
			logger.error("Unable to update status. Order with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to update status. Order with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		logger.info("Updating status of the Order with id {}", id);

		if(currentOrder.getState().equals("Done")) {
			logger.error("Unable to update status. Order with id {} is done.", id);
			return new ResponseEntity(new CustomErrorType("Unable to update status. Order with id " + id + " is done."),
					HttpStatus.NOT_FOUND);
		}

		if(order.getState().equals("Done")) {
			RestTemplate template = new RestTemplate();
			Map<String, String> param = new HashMap<String, String>();
			param.put("id", Integer.toString(order.getBlogger()));
			param.put("sum", Double.toString(order.getSum()));
			param.put("external", Boolean.toString(false));
			template.put("http://localhost:9898/users/user_account/{id}/{sum}/{external}", null, param);
			currentOrder.setEndDate(order.getEndDate());
		}
		currentOrder.setState(order.getState());
		orderService.updateOrder(currentOrder);
		return new ResponseEntity<Order>(currentOrder, HttpStatus.OK);
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