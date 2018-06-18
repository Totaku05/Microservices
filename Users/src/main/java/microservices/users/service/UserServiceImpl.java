package microservices.users.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microservices.users.model.User;
import microservices.users.repositories.ContactInfoRepository;
import microservices.users.repositories.UserRepository;
import microservices.users.util.CustomErrorType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


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

	public ResponseEntity<?> identify(String login, String password)
	{
		List<User> users = findAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		for(User user : users)
			if(user.getLogin().equals(login))
			{
				if(user.getPassword().equals(password))
					return new ResponseEntity<User>(user, HttpStatus.OK);
				else return new ResponseEntity(new CustomErrorType("Bad password"), HttpStatus.BAD_REQUEST);
			}
		return new ResponseEntity(new CustomErrorType("User with login " + login
				+ " not found"), HttpStatus.NOT_FOUND);
	}

	public ResponseEntity<?> externalOperation(Integer card_number, Double sum)
	{
		try {
			RestTemplate template = new RestTemplate();
			Map<String, String> param = new HashMap<String, String>();
			param.put("card_number", Integer.toString(card_number));
			param.put("sum", Double.toString(sum));
			template.put("http://localhost:9696/payment/withdraw_money/{card_number}/{sum}", null, param);
		} catch (HttpClientErrorException e) {
			try {
				JSONObject object = new JSONObject(e.getResponseBodyAsString());
				return new ResponseEntity(new CustomErrorType(object.getString("errorMessage")), e.getStatusCode());
			} catch (Throwable t) {
				return new ResponseEntity(new CustomErrorType("Account updating failed"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity(HttpStatus.OK);
	}

}
