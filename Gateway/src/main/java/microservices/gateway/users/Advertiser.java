package microservices.gateway.users;

import java.util.Objects;

public class Advertiser {
    private Integer id;

    private double account;

    private Integer card_number;

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) { this.login = login; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advertiser that = (Advertiser) o;
        return Double.compare(that.account, account) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(card_number, that.card_number) &&
                Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, card_number, login);
    }

    @Override
    public String toString() {
        return "Advertiser{" +
                "id=" + id +
                ", account=" + account +
                ", card_number=" + card_number +
                ", login='" + login + '\'' +
                '}';
    }
}
