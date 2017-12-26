package microservices.orders.service;

import java.util.Iterator;
import java.util.List;

import microservices.orders.model.Order;
import microservices.orders.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("orderService")
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public Order findById(Integer id)
	{
		return orderRepository.findOne(id);
	}

	public void saveOrder(Order order) { orderRepository.save(order); }

	public void updateOrder(Order order){
		saveOrder(order);
	}

	public void deleteOrderById(Integer id){
		orderRepository.delete(id);
	}

	public void deleteAllOrders(Integer advertiserId)
	{
		List<Order> list = orderRepository.findAll();

		for (Order order : list)
		{
			if(order.getOwner() == advertiserId)
				deleteOrderById(order.getId());
		}
	}

	public List<Order> findAllOrders(){
		return orderRepository.findAll();
	}

	public List<Order> findOrdersForAdvertiser(Integer id)
	{
		List<Order> list = orderRepository.findAll();

		Iterator<Order> it = list.iterator();
		while (it.hasNext())
		{
			Order order = it.next();
			if(order.getOwner() != id)
				it.remove();
		}
		return list;
	}

	public List<Order> findOrdersForBlogger(Integer id)
	{
		List<Order> list = orderRepository.findAll();

		Iterator<Order> it = list.iterator();
		while (it.hasNext())
		{
			Order order = it.next();
			if(order.getBlogger() != id)
				it.remove();
		}
		return list;
	}

	public boolean isOrderExist(Order order) {
		return findById(order.getId()) != null;
	}

}
