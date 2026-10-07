package za.co.ticketwave;

public class VipTicket extends Ticket {

    public static final double MULTIPLIER = 2.5;

    public VipTicket(String seat, double basePrice) {
        super(seat, basePrice);
    }

    @Override
    public double price() {
        return getBasePrice() * MULTIPLIER;
    }

    @Override
    public String perks() {
        return "Lounge access, fast-track entry";
    }
}
