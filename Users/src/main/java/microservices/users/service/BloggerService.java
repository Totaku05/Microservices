package microservices.users.service;


import javafx.util.Pair;
import microservices.users.model.Blogger;

import java.util.List;

public interface BloggerService {
	
	Blogger findById(Integer id);

	void saveBlogger(Blogger blogger);

	void updateBlogger(Blogger blogger);

	void deleteBloggerById(Integer id);

	List<Blogger> findAllBloggers();

	boolean isBloggerExist(Blogger blogger);

	void newBlogger(Integer id, String login, Integer card_number);

	Integer updateAccount(Integer id, Double sum);

	List<Pair<String, Double>> getStatuses();

	boolean updateBloggerStatus(Integer id, String status);
}