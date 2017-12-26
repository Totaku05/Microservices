package microservices.orders.bloggers;

import java.util.Objects;

public class Blogger {
    private Integer id;

    private Double minPrice;

    private Integer countOfSubscribers;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getCountOfSubscribers() {
        return countOfSubscribers;
    }

    public void setCountOfSubscribers(Integer countOfSubscribers) {
        this.countOfSubscribers = countOfSubscribers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blogger blogger = (Blogger) o;
        return Double.compare(blogger.minPrice, minPrice) == 0 &&
                countOfSubscribers == blogger.countOfSubscribers &&
                Objects.equals(id, blogger.id) &&
                Objects.equals(status, blogger.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, minPrice, countOfSubscribers, status);
    }

    @Override
    public String toString() {
        return "Blogger{" +
                "id=" + id +
                ", minPrice=" + minPrice +
                ", countOfSubscribers=" + countOfSubscribers +
                ", status=" + status +
                '}';
    }
}
