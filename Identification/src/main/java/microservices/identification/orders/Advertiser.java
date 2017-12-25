package microservices.identification.orders;

import microservices.identification.value_object.Money;

public class Advertiser {
    private Integer id;

    private double account;

    private String login;

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
