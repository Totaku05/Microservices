package microservices.videos.service;

import java.util.Iterator;
import java.util.List;

import microservices.videos.model.Video;
import microservices.videos.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("videoService")
@Transactional
public class VideoServiceImpl implements VideoService{

	@Autowired
	private VideoRepository videoRepository;

	public Video findById(Integer id)
	{
		return videoRepository.findOne(id);
	}

	public void saveVideo(Video video) { videoRepository.save(video); }

	public void updateVideo(Video video){
		saveVideo(video);
	}

	public void deleteVideoById(Integer id){
		videoRepository.delete(id);
	}

	public void deleteAllVideos(Integer bloggerId)
	{
		List<Video> list = videoRepository.findAll();

		for (Video video : list)
		{
			if(video.getOwner() == bloggerId)
				deleteVideoById(video.getId());
		}
	}

	public List<Video> findAllVideos(){
		return videoRepository.findAll();
	}

	public Video findVideoByOrder(Integer order){
		List<Video> list = videoRepository.findAll();

		for(Video video : list)
		{
			if(video.getCompleted_order() == order)
				return video;
		}
		return null;
	}

	public List<Video> findVideosForBlogger(Integer blogger){
		List<Video> list = videoRepository.findAll();

		Iterator<Video> it = list.iterator();
		while (it.hasNext())
		{
			Video video = it.next();
			if(video.getOwner() != blogger)
				it.remove();
		}

		return list;
	}

	public boolean isVideoExist(Video video) {
		return findById(video.getId()) != null;
	}

}
