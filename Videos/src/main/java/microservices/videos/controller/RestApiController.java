package microservices.videos.controller;

import java.util.List;

import microservices.videos.model.Video;
import microservices.videos.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import microservices.videos.util.CustomErrorType;

@RestController
@RequestMapping("/")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	VideoService videoService;

	@RequestMapping(value = "/video/", method = RequestMethod.GET)
	public ResponseEntity<List<Video>> listAllVideos() {
		List<Video> videos = videoService.findAllVideos();
		if (videos.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Video>>(videos, HttpStatus.OK);
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

	@RequestMapping(value = "/video/", method = RequestMethod.POST)
	public ResponseEntity<?> createVideo(@RequestBody String json_string) {
	    Video video = videoService.convertJsonToVideo(json_string);

		if (video == null || videoService.isVideoExist(video)) {
			logger.error("Unable to create. Video with such id already exists");
			return new ResponseEntity(new CustomErrorType("Unable to create. Video with such id already exists."),HttpStatus.CONFLICT);
		}
        logger.info("Creating Video : {}", video);

		videoService.saveVideo(video);
		return new ResponseEntity<Video>(video, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/video/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateVideo(@PathVariable("id") int id, @RequestBody String json_string) {
		Video video = videoService.convertJsonToVideo(json_string);
		if (video == null) {
			logger.error("Unable to update. Bad video info.");
			return new ResponseEntity(new CustomErrorType("Unable to update. Bad video info."),
					HttpStatus.BAD_REQUEST);
		}

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

	@RequestMapping(value = "/video/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVideo(@PathVariable("id") int id) {
		logger.info("Deleting Video with id {}", id);

		Video video = videoService.findById(id);
		if (video == null) {
			logger.error("Unable to delete. Video with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. Video with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		if(video.getCompleted_order() == null)
			videoService.deleteVideoById(id);
		else
			return new ResponseEntity(new CustomErrorType("Unable to delete. Video with id " + id + " has completed order."),
					HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Video>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/video/blogger/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteAllVideosOfBlogger(@PathVariable("id") int id) {
		logger.info("Deleting all Videos of Blogger with id {}", id);

		List<Video> videos = videoService.findVideosForBlogger(id);
		if (videos.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		for(Video video : videos) {
			if (video.getCompleted_order() == null)
				videoService.deleteVideoById(id);
		}
		return new ResponseEntity<Video>(HttpStatus.NO_CONTENT);
	}
}