package microservices.identification.bloggers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.sql.Time;
import java.util.Objects;

public class Video {
    private Integer id;

    private String name;

    private String description;

    private String tag;

    private Date dateOfCreation;

    private Time duration;

    private Integer countOfLikes;

    private Integer countOfDislikes;

    private Integer countOfViews;

    private Integer completed_order;

    private Integer owner;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
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

    public Integer getCompleted_order() {
        return completed_order;
    }

    public void setCompleted_order(Integer completed_order) {
        this.completed_order = completed_order;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) { this.owner = owner; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return countOfLikes == video.countOfLikes &&
                countOfDislikes == video.countOfDislikes &&
                countOfViews == video.countOfViews &&
                completed_order == video.completed_order &&
                owner == video.owner &&
                Objects.equals(id, video.id) &&
                Objects.equals(name, video.name) &&
                Objects.equals(description, video.description) &&
                Objects.equals(tag, video.tag) &&
                Objects.equals(dateOfCreation, video.dateOfCreation) &&
                Objects.equals(duration, video.duration);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, completed_order, owner);
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tag=" + tag +
                ", dateOfCreation=" + dateOfCreation +
                ", duration=" + duration +
                ", countOfLikes=" + countOfLikes +
                ", countOfDislikes=" + countOfDislikes +
                ", countOfViews=" + countOfViews +
                ", comleted_order=" + completed_order +
                ", owner=" + owner +
                '}';
    }
}
