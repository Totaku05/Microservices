package microservices.users.service;


import microservices.users.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
	
	User findById(Integer id);

	void saveUser(User user);

	void updateUser(User user);

	void deleteUserById(Integer id);

	List<User> findAllUsers();

	boolean isUserExist(User user);

	ResponseEntity<?> identify(String login, String password);

	ResponseEntity<?> externalOperation(Integer card_number, Double sum);
}