package microservices.bloggers.model;

import microservices.bloggers.value_object.Money;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "blogger")
public class Blogger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account", nullable = false)
    private Double account;

    @Column(name = "minPrice", nullable = false)
    private Double minPrice;

    @Column(name = "countOfSubscribers", nullable = false)
    private Integer countOfSubscribers;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "login", nullable = false)
    private String login;

    @Transient
    private Money acc, price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getAccount() {
        acc = new Money(account);
        return acc;
    }

    public void setAccount(Money money) {
        acc = money;
        account = money.getSum();
    }

    public Money getMinPrice() {
        price = new Money(minPrice);
        return price;
    }

    public void setMinPrice(Money money) {
        price = money;
        minPrice = money.getSum();
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
