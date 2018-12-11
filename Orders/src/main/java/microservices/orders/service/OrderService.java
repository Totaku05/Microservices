package microservices.orders.service;


import microservices.orders.model.Order;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
	
	Order findById(Integer id);

	void saveOrder(Order order);

	void updateOrder(Order order);

	void deleteOrderById(Integer id);

	void deleteAllOrders(Integer advertiserId);

	List<Order> findAllOrders();

	List<Order> findOrdersForAdvertiser(Integer id);

	List<Order> findOrdersForBlogger(Integer id);

	boolean isOrderExist(Order order);

	ResponseEntity<?> createOrder(Order order);

	Order convertJsonToOrder(String json_string);
}