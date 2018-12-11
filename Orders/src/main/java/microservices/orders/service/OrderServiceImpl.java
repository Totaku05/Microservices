package microservices.orders.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import microservices.orders.entities.Blogger;
import microservices.orders.entities.Video;
import microservices.orders.model.Order;
import microservices.orders.repositories.OrderRepository;
import microservices.orders.util.CustomErrorType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

	private Blogger convertJsonToBlogger(JSONObject object, double sum)
	{
		Blogger blogger = new Blogger();

		try {
			blogger.setMinPrice(object.getDouble("minPrice"));
			if(blogger.getMinPrice() > sum)
				return null;
			blogger.setId(object.getInt("id"));
			blogger.setCountOfSubscribers(object.getInt("countOfSubscribers"));
			blogger.setStatus(object.getString("status"));
		}
		catch (Throwable t)
		{
			return null;
		}
		return blogger;
	}

	private Video convertJsonToVideo(JSONObject object, String tag)
	{
		Video video = new Video();

		try {
			video.setTag(object.getString("tag"));
			if(!video.getTag().equals(tag))
				return null;
			video.setCountOfDislikes(object.getInt("countOfDislikes"));
			video.setCountOfLikes(object.getInt("countOfLikes"));
			video.setCountOfViews(object.getInt("countOfViews"));
		}
		catch (Throwable t)
		{
			return null;
		}
		return video;
	}

	private double getCoefficientByStatus(String status)
	{
		switch (status) {
			case "Bronze":
				return 1.1;
			case "Silver":
				return 1.2;
			case "Gold":
				return 1.3;
			case "Diamond":
				return 1.4;
			default:
				return 1;
		}
	}

	public ResponseEntity<?> createOrder(Order order)
	{
		RestTemplate template = new RestTemplate();
		Map<String, String> param = new HashMap<String, String>();
		param.put("id", Integer.toString(order.getOwner()));
		param.put("sum", Double.toString(-order.getSum()));
		param.put("external", Boolean.toString(false));

		template.put("http://localhost:9898/users/user_account/{id}/{sum}/{external}", null, param);
		ResponseEntity<String> resp = template.getForEntity("http://localhost:9898/users/blogger/", String.class);
		JSONArray array;

		int bloggerId = -1;

		try {
			array = new JSONArray(resp.getBody());
			double maxSum = -100;
			for(int i = 0; i < array.length(); i++)
			{
				Blogger blogger = convertJsonToBlogger(array.getJSONObject(i), order.getSum());
				if(blogger == null)
					continue;
				ResponseEntity<String> response = template.getForEntity("http://localhost:9090/videos/video_blogger/{id}", String.class, blogger.getId());
				JSONArray arr = new JSONArray(response.getBody());
				double coeff = getCoefficientByStatus(blogger.getStatus());
				double sum = blogger.getCountOfSubscribers() * coeff;
				for (int j = 0; j < arr.length(); j++)
				{
					Video video = convertJsonToVideo(arr.getJSONObject(j), order.getTag());
					if(video == null)
						continue;
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
			return new ResponseEntity(new CustomErrorType("Order creation failed"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(bloggerId == -1)
			return new ResponseEntity(new CustomErrorType("Order creation failed"), HttpStatus.INTERNAL_SERVER_ERROR);
		order.setBlogger(bloggerId);
		saveOrder(order);

		return new ResponseEntity<Order>(order, HttpStatus.CREATED);
	}

	public Order convertJsonToOrder(String json_string)
	{
		Order order = new Order();

		try {
			JSONObject object = new JSONObject(json_string);
			order.setId(object.getInt("id"));
			order.setName(object.getString("name"));
			order.setDescription(object.getString("description"));
			order.setTag(object.getString("tag"));
			order.setSum(object.getDouble("sum"));

			JSONObject date = object.getJSONObject("startDate");
			String str_date = String.format("%d-%02d-%02d", date.getInt("year"), date.getInt("month"), date.getInt("day"));
			order.setStartDate(Date.valueOf(str_date));

			date = object.getJSONObject("lastUpdateDate");
			str_date = String.format("%d-%02d-%02d", date.getInt("year"), date.getInt("month"), date.getInt("day"));
			order.setLastUpdateDate(Date.valueOf(str_date));

			if(!object.getString("endDate").equals("null")) {
				date = object.getJSONObject("endDate");
				str_date = String.format("%d-%02d-%02d", date.getInt("year"), date.getInt("month"), date.getInt("day"));
				order.setEndDate(Date.valueOf(str_date));
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
}
