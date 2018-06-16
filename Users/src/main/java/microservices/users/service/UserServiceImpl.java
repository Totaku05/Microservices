package microservices.users.service;

import java.util.List;

import microservices.users.model.User;
import microservices.users.repositories.ContactInfoRepository;
import microservices.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;

	public User findById(Integer id)
	{
		return userRepository.findOne(id);
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}

	public void updateUser(User user){
		saveUser(user);
	}

	public void deleteUserById(Integer id){
		userRepository.delete(id);
	}

	public List<User> findAllUsers(){
		return userRepository.findAll();
	}

	public boolean isUserExist(User user) {
		return findById(user.getId()) != null;
	}

}
