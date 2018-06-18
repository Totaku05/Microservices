package microservices.gateway.service;

import javafx.util.Pair;
import microservices.gateway.orders.Order;
import microservices.gateway.users.Advertiser;
import microservices.gateway.users.Blogger;
import microservices.gateway.users.ContactInfo;
import microservices.gateway.users.User;
import microservices.gateway.util.CustomErrorType;
import microservices.gateway.videos.Video;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("orderService")
@Transactional
public class GatewayServiceImpl implements GatewayService {

    public ResponseEntity<?> updateOrderStatus(Order order)
    {
        RestTemplate template = new RestTemplate();
        try {
            HttpEntity<Order> entity = new HttpEntity<Order>(order);
            template.exchange("http://localhost:8585/orders/order/status/{id}", HttpMethod.PUT, entity, Order.class, order.getId());
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
        if(order.getState().equals("Done"))
        {
            User blogger = template.getForObject("http://localhost:9898/users/user/{id}", User.class, order.getBlogger());
            User advertiser = template.getForObject("http://localhost:9898/users/user/{id}", User.class, order.getOwner());
            String message = "Status of your order is changed.";
            Map<String, String> param = new HashMap<String, String>();
            param.put("email", blogger.getContactInfo().getEmail());
            param.put("message", message);
            template.put("http://localhost:9797/notification/send_message/{email}/{message}", null, param);
            param = new HashMap<String, String>();
            param.put("email", advertiser.getContactInfo().getEmail());
            param.put("message", message);
            template.put("http://localhost:9797/notification/send_message/{email}/{message}", null, param);
        }
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    public ResponseEntity<?> updateVideo(Video video)
    {
        RestTemplate template = new RestTemplate();
        try {
            HttpEntity<Video> entity = new HttpEntity<Video>(video);
            template.exchange("http://localhost:9090/videos/video/{id}", HttpMethod.PUT, entity, Video.class, video.getId());
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
        if(video.getCompleted_order() != null)
        {
            String message = "You have new video with your completed order.";
            Order order = template.getForObject("http://localhost:8585/orders/order/{id}", Order.class, video.getCompleted_order());
            User advertiser = template.getForObject("http://localhost:9898/users/user/{id}", User.class, order.getOwner());
            Map<String, String> param = new HashMap<String, String>();
            param.put("email", advertiser.getContactInfo().getEmail());
            param.put("message", message);
            template.put("http://localhost:9797/notification/send_message/{email}/{message}", null, param);
        }
        return new ResponseEntity<Video>(video, HttpStatus.OK);
    }

    public User updateUser(Integer id, User user)
    {
        RestTemplate template = new RestTemplate();
        User currentUser = template.getForObject("http://localhost:9898/users/user/{id}", User.class, id);

        if (currentUser == null)
            return null;

        currentUser.setLogin(user.getLogin());
        currentUser.setPassword(user.getPassword());
        currentUser.setRole(user.getRole());
        currentUser.setContactInfo(user.getContactInfo());

        HttpEntity<User> entity = new HttpEntity<User>(currentUser);
        template.exchange("http://localhost:9898/users/user/{id}", HttpMethod.PUT, entity, User.class, id);
        return currentUser;
    }

    public void updateBlogger(User user, Blogger blogger)
    {
        RestTemplate template = new RestTemplate();
        Blogger currentBlogger = template.getForObject("http://localhost:9898/users/blogger/{id}", Blogger.class, user.getId());
        currentBlogger.setId(user.getId());
        currentBlogger.setLogin(user.getLogin());
        currentBlogger.setStatus(blogger.getStatus());
        currentBlogger.setCountOfSubscribers(blogger.getCountOfSubscribers());
        currentBlogger.setMinPrice(blogger.getMinPrice());
        currentBlogger.setCard_number(blogger.getCard_number());
        HttpEntity<Blogger> entity = new HttpEntity<Blogger>(currentBlogger);
        template.exchange("http://localhost:9898/users/blogger/{id}", HttpMethod.PUT, entity, Blogger.class, user.getId());
    }

    public void updateAdvertiser(User user, Advertiser advertiser)
    {
        RestTemplate template = new RestTemplate();
        Advertiser currentAdvertiser = template.getForObject("http://localhost:9898/users/advertiser/{id}", Advertiser.class, user.getId());
        currentAdvertiser.setId(user.getId());
        currentAdvertiser.setLogin(user.getLogin());
        currentAdvertiser.setCard_number(advertiser.getCard_number());
        HttpEntity<Advertiser> entity = new HttpEntity<Advertiser>(currentAdvertiser);
        template.exchange("http://localhost:9898/users/advertiser/{id}", HttpMethod.PUT, entity, Advertiser.class, user.getId());
    }

    public ResponseEntity<?> createOrder(Order order)
    {
        RestTemplate template = new RestTemplate();
        Blogger blogger = null;
        try {
            Order o = template.postForObject("http://localhost:8585/orders/order/", order, Order.class);
            blogger = template.getForObject("http://localhost:9898/users/blogger/{id}", Blogger.class, order.getBlogger());
            User user = template.getForObject("http://localhost:9898/users/user/{id}", User.class, order.getBlogger());
            String message = "You have new order.";
            Map<String, String> param = new HashMap<String, String>();
            param.put("email", user.getContactInfo().getEmail());
            param.put("message", message);
            template.put("http://localhost:9797/notification/send_message/{email}/{message}", null, param);
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

    public ResponseEntity<?> listOrders(ResponseEntity<String> resp)
    {
        List<Order> orders = new LinkedList<Order>();
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
            return new ResponseEntity(new CustomErrorType("Error while fetching Orders."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
    }

    public ResponseEntity<?> listVideos(ResponseEntity<String> resp)
    {
        List<Video> videos = new LinkedList<Video>();
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

    public ResponseEntity<?> listStatuses(ResponseEntity<String> resp)
    {
        List<Pair<String, Double>> statuses = new LinkedList<Pair<String, Double>>();
        try {
            JSONArray array = new JSONArray(resp.getBody());
            for(int i = 0; i < array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);
                Pair<String, Double> pair = new Pair<String, Double>(object.getString("key"), object.getDouble("value"));
                statuses.add(pair);
            }
        }
        catch (Throwable t)
        {
            return new ResponseEntity(new CustomErrorType("Fetching all Users failed"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Pair<String, Double>>>(statuses, HttpStatus.OK);
    }

    public ResponseEntity<?> listUsers(ResponseEntity<String> resp)
    {
        List<User> users = new LinkedList<User>();
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
}
