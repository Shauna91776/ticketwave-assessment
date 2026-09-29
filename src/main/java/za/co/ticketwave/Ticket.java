package za.co.ticketwave;

public abstract class Ticket {

    private final String seat;
    private final double basePrice;

    protected Ticket(String seat, double basePrice) {
        this.seat = seat;
        this.basePrice = basePrice;
    }

    public String getSeat() {
        return seat;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public abstract double price();

    public abstract String perks();

    @Override
    public String toString() {
        return String.format("%s[seat=%s, price=%.2f, perks=%s]",
                getClass().getSimpleName(), seat, price(), perks());
    }
}
