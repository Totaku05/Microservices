package microservices.identification.value_object;

public class Money {
    private double sum;

    public Money(double sum)
    {
        if(sum >= 0)
            this.sum = sum;
        else
            throw new RuntimeException("Money must be greater than or equal to zero");
    }

    public double getSum() {
        return sum;
    }

    public Money add(Money money)
    {
        return new Money(sum + money.getSum());
    }

    public Money sub(Money money)
    {
        if(sum - money.getSum() >= 0)
            return new Money(sum - money.getSum());
        else
            throw new RuntimeException("Money must be greater than or equal to zero");
    }
}
