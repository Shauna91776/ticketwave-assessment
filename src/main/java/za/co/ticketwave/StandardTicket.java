package za.co.ticketwave;

public class StandardTicket extends Ticket {

    public StandardTicket(String seat, double basePrice) {
        super(seat, basePrice);
    }

    @Override
    public double price() {
        return getBasePrice();
    }

    @Override
    public String perks() {
        return "Lounge access, fast-track entry";
    }
}
