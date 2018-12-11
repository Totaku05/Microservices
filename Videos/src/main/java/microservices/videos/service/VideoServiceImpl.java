package microservices.videos.service;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import microservices.videos.model.Video;
import microservices.videos.repositories.VideoRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.sql.Date;

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

    public Video convertJsonToVideo(String json_string)
    {
        Video video = new Video();

        try {
            JSONObject object = new JSONObject(json_string);
            video.setId(object.getInt("id"));
            video.setName(object.getString("name"));
            video.setDescription(object.getString("description"));
            video.setTag(object.getString("tag"));

			JSONObject time = object.getJSONObject("duration");
			String str_time = String.format("%02d:%02d:%02d", time.getInt("hour"), time.getInt("minute"), time.getInt("second"));
			video.setDuration(Time.valueOf(str_time));
			JSONObject date = object.getJSONObject("dateOfCreation");
			String str_date = String.format("%d-%02d-%02d", date.getInt("year"), date.getInt("month"), date.getInt("day"));
            video.setDateOfCreation(Date.valueOf(str_date));

            video.setCountOfLikes(object.getInt("countOfLikes"));
            video.setCountOfDislikes(object.getInt("countOfDislikes"));
            video.setCountOfViews(object.getInt("countOfViews"));
            if(!object.getString("completed_order").equals("null"))
                video.setCompleted_order(object.getInt("completed_order"));
            video.setOwner(object.getInt("owner"));
        }
        catch (Throwable t)
        {
            return null;
        }
        return video;
    }
}
