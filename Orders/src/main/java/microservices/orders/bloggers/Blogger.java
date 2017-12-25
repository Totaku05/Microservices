package microservices.orders.bloggers;

import com.fasterxml.jackson.annotation.JsonProperty;
import microservices.orders.value_object.Money;

import java.io.Serializable;

public class Blogger {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("minPrice")
    private Money minPrice;

    @JsonProperty("countOfSubscribers")
    private Integer countOfSubscribers;

    @JsonProperty("status")
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Money money) {
        minPrice = money;
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

        if (!id.equals(blogger.id)) return false;
        if (!minPrice.equals(blogger.minPrice)) return false;
        if (!countOfSubscribers.equals(blogger.countOfSubscribers)) return false;
        return status.equals(blogger.status);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + minPrice.hashCode();
        result = 31 * result + countOfSubscribers.hashCode();
        result = 31 * result + status.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Blogger{" +
                "id=" + id +
                ", minPrice=" + minPrice +
                ", countOfSubscribers=" + countOfSubscribers +
                ", status='" + status + '\'' +
                '}';
    }
}
