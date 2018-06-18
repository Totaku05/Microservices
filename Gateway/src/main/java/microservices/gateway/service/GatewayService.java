package microservices.gateway.service;

import microservices.gateway.orders.Order;
import microservices.gateway.users.Advertiser;
import microservices.gateway.users.Blogger;
import microservices.gateway.users.User;
import microservices.gateway.videos.Video;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public interface GatewayService {
    ResponseEntity<?> updateOrderStatus(Order order);

    ResponseEntity<?> updateVideo(Video video);

    User updateUser(Integer id, User user);

    void updateBlogger(User user, Blogger blogger);

    void updateAdvertiser(User user, Advertiser advertiser);

    ResponseEntity<?> createOrder(Order order);

    ResponseEntity<?> listOrders(ResponseEntity<String> resp);

    ResponseEntity<?> listVideos(ResponseEntity<String> resp);

    ResponseEntity<?> listStatuses(ResponseEntity<String> resp);

    ResponseEntity<?> listUsers(ResponseEntity<String> resp);
}
