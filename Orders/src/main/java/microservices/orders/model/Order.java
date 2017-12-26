package microservices.orders.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "sum", nullable = false)
    private double sum;

    @Column(name = "startDate", nullable = false)
    private Date startDate;

    @Column(name = "endDate")
    private Date endDate;

    @Column(name = "lastUpdateDate", nullable = false)
    private Date lastUpdateDate;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "blogger", nullable = false)
    private int blogger;

    @Column(name = "owner", nullable = false)
    private int owner;

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

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getBlogger() {
        return blogger;
    }

    public void setBlogger(int blogger) {
        this.blogger = blogger;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(order.sum, sum) == 0 &&
                blogger == order.blogger &&
                owner == order.owner &&
                Objects.equals(id, order.id) &&
                Objects.equals(name, order.name) &&
                Objects.equals(description, order.description) &&
                Objects.equals(tag, order.tag) &&
                Objects.equals(startDate, order.startDate) &&
                Objects.equals(endDate, order.endDate) &&
                Objects.equals(lastUpdateDate, order.lastUpdateDate) &&
                Objects.equals(state, order.state);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, description, tag, sum, startDate, endDate, lastUpdateDate, state, blogger, owner);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tag='" + tag + '\'' +
                ", sum=" + sum +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", state='" + state + '\'' +
                ", blogger=" + blogger +
                ", owner=" + owner +
                '}';
    }
}
