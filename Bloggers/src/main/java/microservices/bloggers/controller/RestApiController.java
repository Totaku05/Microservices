package microservices.bloggers.controller;

import java.util.List;

import microservices.bloggers.model.Blogger;
import microservices.bloggers.model.Video;
import microservices.bloggers.service.BloggerService;
import microservices.bloggers.service.VideoService;
import microservices.bloggers.value_object.Money;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import microservices.bloggers.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	BloggerService bloggerService;

	@Autowired
	VideoService videoService;

	@RequestMapping(value = "/blogger/", method = RequestMethod.GET)
	public ResponseEntity<List<Blogger>> listAllBloggers() {
		List<Blogger> bloggers = bloggerService.findAllBloggers();
		if (bloggers.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Blogger>>(bloggers, HttpStatus.OK);
	}

	@RequestMapping(value = "/video/", method = RequestMethod.GET)
	public ResponseEntity<List<Video>> listAllVideos() {
		List<Video> videos = videoService.findAllVideos();
		if (videos.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Video>>(videos, HttpStatus.OK);
	}

	@RequestMapping(value = "/blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Blogger with id {}", id);
		Blogger blogger = bloggerService.findById(id);
		if (blogger == null) {
			logger.error("Blogger with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Blogger with id " + id
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Blogger>(blogger, HttpStatus.OK);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getVideo(@PathVariable("id") int id) {
		logger.info("Fetching Video with id {}", id);
		Video video = videoService.findById(id);
		if (video == null) {
			logger.error("Video with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Video with id " + id
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Video>(video, HttpStatus.OK);
	}

	@RequestMapping(value = "/video_blogger/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getVideosForBlogger(@PathVariable("id") int id) {
		logger.info("Fetching Video by blogger id {}", id);
		List<Video> list = videoService.findVideosForBlogger(id);
		if (list.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Video>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/video_order/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getVideoByOrder(@PathVariable("id") int id) {
		logger.info("Fetching Video by order id {}", id);
		Video video = videoService.findVideoByOrder(id);
		if (video == null) {
			logger.error("Video by order id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Video by order id " + id
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Video>(video, HttpStatus.OK);
	}

	@RequestMapping(value = "/blogger/", method = RequestMethod.POST)
	public ResponseEntity<?> createBlogger(@RequestBody Blogger blogger) {
		logger.info("Creating Blogger : {}", blogger);

		if (bloggerService.isBloggerExist(blogger)) {
			logger.error("Unable to create. A Blogger with id {} already exist", blogger.getId());
			return new ResponseEntity(new CustomErrorType("Unable to create. A Blogger with id " +
					blogger.getId() + " already exist."),HttpStatus.CONFLICT);
		}
		bloggerService.saveBlogger(blogger);
		return new ResponseEntity<Blogger>(blogger, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/video/", method = RequestMethod.POST)
	public ResponseEntity<?> createVideo(@RequestBody Video video) {
		logger.info("Creating Video : {}", video);

		if (videoService.isVideoExist(video)) {
			logger.error("Unable to create. A Video with id {} already exist", video.getId());
			return new ResponseEntity(new CustomErrorType("Unable to create. A Video with id " +
					video.getId() + " already exist."),HttpStatus.CONFLICT);
		}
		videoService.saveVideo(video);
		return new ResponseEntity<Video>(video, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/blogger/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateBlogger(@PathVariable("id") int id, @RequestBody JSONObject object) {
		logger.info("Updating Blogger with id {}", id);

		Blogger currentBlogger = bloggerService.findById(id);

		if (currentBlogger == null) {
			logger.error("Unable to update. Blogger with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to update. Blogger with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		Blogger blogger = new Blogger();
        try {
            blogger.setLogin(object.getString("login"));
            blogger.setAccount(new Money(object.getJSONObject("account").getDouble("sum")));
            blogger.setMinPrice(new Money(object.getJSONObject("minPrice").getDouble("sum")));
            blogger.setCountOfSubscribers(object.getInt("countOfSubscribers"));
            blogger.setStatus(object.getString("status"));
        }
        catch (Throwable t)
        {
            return new ResponseEntity(new CustomErrorType("Unable to update. JSONObject opening problem."),
                    HttpStatus.NOT_FOUND);
        }
		currentBlogger.setLogin(blogger.getLogin());
		currentBlogger.setAccount(blogger.getAccount());
		currentBlogger.setMinPrice(blogger.getMinPrice());
		currentBlogger.setCountOfSubscribers(blogger.getCountOfSubscribers());
		currentBlogger.setStatus(blogger.getStatus());


		bloggerService.updateBlogger(currentBlogger);
		return new ResponseEntity<Blogger>(currentBlogger, HttpStatus.OK);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateVideo(@PathVariable("id") int id, @RequestBody Video video) {
		logger.info("Updating Video with id {}", id);

		Video currentVideo = videoService.findById(id);

		if (currentVideo == null) {
			logger.error("Unable to update. Video with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to update. Video with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		currentVideo.setName(video.getName());
		currentVideo.setDescription(video.getDescription());
		currentVideo.setCountOfLikes(video.getCountOfLikes());
		currentVideo.setCountOfDislikes(video.getCountOfDislikes());
		currentVideo.setCountOfViews(video.getCountOfViews());
		currentVideo.setDateOfCreation(video.getDateOfCreation());
		currentVideo.setDuration(video.getDuration());
		currentVideo.setTag(video.getTag());
		currentVideo.setOwner(video.getOwner());
		currentVideo.setCompleted_order(video.getCompleted_order());


		videoService.updateVideo(currentVideo);
		return new ResponseEntity<Video>(currentVideo, HttpStatus.OK);
	}

	@RequestMapping(value = "/blogger/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBlogger(@PathVariable("id") int id) {
		logger.info("Deleting Blogger with id {}", id);

		Blogger blogger = bloggerService.findById(id);
		if (blogger == null) {
			logger.error("Unable to delete. Blogger with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. Blogger with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		bloggerService.deleteBloggerById(id);
		videoService.deleteAllVideos(id);
		return new ResponseEntity<Blogger>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVideo(@PathVariable("id") int id) {
		logger.info("Deleting Video with id {}", id);

		Video video = videoService.findById(id);
		if (video == null) {
			logger.error("Unable to delete. Video with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. Video with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		videoService.deleteVideoById(id);
		return new ResponseEntity<Video>(HttpStatus.NO_CONTENT);
	}

}