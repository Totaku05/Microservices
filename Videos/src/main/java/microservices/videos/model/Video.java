package microservices.videos.model;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "dateOfCreation", nullable = false)
    private Date dateOfCreation;

    @Column(name = "duration", nullable = false)
    private Time duration;

    @Column(name = "countOfLikes", nullable = false)
    private Integer countOfLikes;

    @Column(name = "countOfDislikes", nullable = false)
    private Integer countOfDislikes;

    @Column(name = "countOfViews", nullable = false)
    private Integer countOfViews;

    @Column(name = "completed_order")
    private Integer completed_order;

    @Column(name = "owner", nullable = false)
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
