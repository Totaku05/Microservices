package microservices.gateway.users;

import java.util.Objects;

public class Blogger {
    private Integer id;

    private Double account;

    private Integer card_number;

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

    public Integer getCard_number() {
        return card_number;
    }

    public void setCard_number(Integer card_number) {
        this.card_number = card_number;
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
        return Objects.equals(id, blogger.id) &&
                Objects.equals(account, blogger.account) &&
                Objects.equals(card_number, blogger.card_number) &&
                Objects.equals(minPrice, blogger.minPrice) &&
                Objects.equals(countOfSubscribers, blogger.countOfSubscribers) &&
                Objects.equals(status, blogger.status) &&
                Objects.equals(login, blogger.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, card_number, minPrice, countOfSubscribers, status, login);
    }

    @Override
    public String toString() {
        return "Blogger{" +
                "id=" + id +
                ", account=" + account +
                ", card_number=" + card_number +
                ", minPrice=" + minPrice +
                ", countOfSubscribers=" + countOfSubscribers +
                ", status='" + status + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
