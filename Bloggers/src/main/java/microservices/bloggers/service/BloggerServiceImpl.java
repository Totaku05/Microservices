package microservices.bloggers.service;

import microservices.bloggers.model.Blogger;
import microservices.bloggers.repositories.BloggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
