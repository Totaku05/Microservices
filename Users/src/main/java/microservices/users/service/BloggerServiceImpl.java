package microservices.users.service;

import javafx.util.Pair;
import microservices.users.model.Blogger;
import microservices.users.repositories.BloggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;


@Service("bloggerService")
@Transactional
public class BloggerServiceImpl implements BloggerService{

	@Autowired
	private BloggerRepository bloggerRepository;

	public Blogger findById(Integer id)
	{
		return bloggerRepository.findOne(id);
	}

	public void saveBlogger(Blogger blogger) {
		bloggerRepository.save(blogger);
	}

	public void updateBlogger(Blogger blogger){
		saveBlogger(blogger);
	}

	public void deleteBloggerById(Integer id){
		bloggerRepository.delete(id);
	}

	public List<Blogger> findAllBloggers(){
		return bloggerRepository.findAll();
	}

	public boolean isBloggerExist(Blogger blogger) {
		return findById(blogger.getId()) != null;
	}

	public void newBlogger(Integer id, String login, Integer card_number)
	{
		Blogger blogger = new Blogger();
		blogger.setAccount(0.0);
		blogger.setMinPrice(0.0);
		blogger.setId(id);
		blogger.setStatus("Common");
		blogger.setLogin(login);
		blogger.setCountOfSubscribers(0);
		blogger.setCard_number(card_number);
		saveBlogger(blogger);
	}

	public Integer updateAccount(Integer id, Double sum)
	{
		Blogger blogger = findById(id);

		if(blogger.getAccount() + sum < 0)
			return -1;

		blogger.setAccount(blogger.getAccount() + sum);
		updateBlogger(blogger);

		return blogger.getCard_number();
	}

	public List<Pair<String, Double>> getStatuses()
	{
		List<Pair<String, Double>> statuses = new LinkedList<Pair<String, Double>>();
		Pair<String, Double> pair = new Pair<String, Double>("Bronze", 100.0);
		statuses.add(pair);
		pair = new Pair<String, Double>("Silver", 200.0);
		statuses.add(pair);
		pair = new Pair<String, Double>("Gold", 300.0);
		statuses.add(pair);
		pair = new Pair<String, Double>("Diamond", 400.0);
		statuses.add(pair);
		return statuses;
	}

	public boolean updateBloggerStatus(Integer id, String status)
	{
		Blogger blogger = findById(id);
		switch (status) {
			case "Bronze":
				if (updateAccount(id, 100.0) == -1)
					return false;
				break;
			case "Silver":
				if (updateAccount(id, 200.0) == -1)
					return false;
				break;
			case "Gold":
				if (updateAccount(id, 300.0) == -1)
					return false;
				break;
			case "Diamond":
				if (updateAccount(id, 400.0) == -1)
					return false;
				break;
		}
		blogger.setStatus(status);
		updateBlogger(blogger);
		return true;
	}

}
