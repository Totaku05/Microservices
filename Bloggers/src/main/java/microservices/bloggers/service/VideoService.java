package microservices.bloggers.service;


import microservices.bloggers.model.Video;

import java.util.List;

public interface VideoService {
	
	Video findById(Integer id);

	void saveVideo(Video video);

	void updateVideo(Video video);

	void deleteVideoById(Integer id);

	void deleteAllVideos(Integer bloggerId);

	List<Video> findAllVideos();

	Video findVideoByOrder(Integer order);

	List<Video> findVideosForBlogger(Integer blogger);

	boolean isVideoExist(Video video);
}