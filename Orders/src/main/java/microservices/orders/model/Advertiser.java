package microservices.orders.model;

import microservices.orders.value_object.Money;

import javax.persistence.*;

@Entity
@Table(name = "advertiser")
public class Advertiser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account", nullable = false)
    private double account;

    @Column(name = "login", nullable = false)
    private String login;

    @Transient
    private Money money;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getAccount() {
        money = new Money(account);
        return money;
    }

    public void setAccount(Money money) {
        this.money = money;
        account = money.getSum();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "Advertiser{" +
                "id=" + id +
                ", account=" + account +
                ", login='" + login + '\'' +
                '}';
    }
}
