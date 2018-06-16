package microservices.users.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "advertiser")
public class Advertiser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account", nullable = false)
    private double account;

    @Column(name = "card_number", nullable = false)
    private Integer card_number;

    @Column(name = "login", nullable = false)
    private String login;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getAccount() {
        return account;
    }

    public void setAccount(double account) {
        this.account = account;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getCard_number() { return card_number; }

    public void setCard_number(Integer card_number) { this.card_number = card_number; }

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
