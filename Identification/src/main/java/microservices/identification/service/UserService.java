package microservices.identification.service;


import microservices.identification.model.User;

import java.util.List;

public interface UserService {
	
	User findById(Integer id);

	void saveUser(User user);

	void updateUser(User user);

	void deleteUserById(Integer id);

	List<User> findAllUsers();

	boolean isUserExist(User user);
}