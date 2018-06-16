package microservices.users.service;


import microservices.users.model.Blogger;

import java.util.List;

public interface BloggerService {
	
	Blogger findById(Integer id);

	void saveBlogger(Blogger blogger);

	void updateBlogger(Blogger blogger);

	void deleteBloggerById(Integer id);

	List<Blogger> findAllBloggers();

	boolean isBloggerExist(Blogger blogger);
}