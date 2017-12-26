package microservices.identification.bloggers;

import java.util.Objects;

public class Blogger {
    private Integer id;

    private Double account;

    private Double minPrice;

    private Integer countOfSubscribers;

    private String status;

    private String login;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAccount() {
        return account;
    }

    public void setAccount(Double account) {
        this.account = account;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blogger blogger = (Blogger) o;
        return Double.compare(blogger.account, account) == 0 &&
                Double.compare(blogger.minPrice, minPrice) == 0 &&
                countOfSubscribers == blogger.countOfSubscribers &&
                Objects.equals(id, blogger.id) &&
                Objects.equals(status, blogger.status) &&
                Objects.equals(login, blogger.login);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, account, minPrice, countOfSubscribers, status, login);
    }

    @Override
    public String toString() {
        return "Blogger{" +
                "id=" + id +
                ", account=" + account +
                ", minPrice=" + minPrice +
                ", countOfSubscribers=" + countOfSubscribers +
                ", status=" + status +
                ", login='" + login + '\'' +
                '}';
    }
}
