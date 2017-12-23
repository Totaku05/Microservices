package microservices.orders.bloggers;

import java.io.Serializable;
import java.util.Objects;

public class Video{
    private String tag;

    private Integer countOfLikes;

    private Integer countOfDislikes;

    private Integer countOfViews;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getCountOfLikes() {
        return countOfLikes;
    }

    public void setCountOfLikes(Integer countOfLikes) {
        this.countOfLikes = countOfLikes;
    }

    public Integer getCountOfDislikes() {
        return countOfDislikes;
    }

    public void setCountOfDislikes(Integer countOfDislikes) {
        this.countOfDislikes = countOfDislikes;
    }

    public Integer getCountOfViews() {
        return countOfViews;
    }

    public void setCountOfViews(Integer countOfViews) {
        this.countOfViews = countOfViews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return countOfLikes == video.countOfLikes &&
                countOfDislikes == video.countOfDislikes &&
                countOfViews == video.countOfViews &&
                Objects.equals(tag, video.tag);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tag, countOfLikes, countOfDislikes, countOfViews);
    }

    @Override
    public String toString() {
        return "Video{" +
                ", tag=" + tag +
                ", countOfLikes=" + countOfLikes +
                ", countOfDislikes=" + countOfDislikes +
                ", countOfViews=" + countOfViews +
                '}';
    }
}
