package za.co.ticketwave;

public class StandardTicket extends Ticket {

    public StandardTicket(String seat, double basePrice) {
        super(seat, basePrice);
    }

    @Override
    public double price() {
        return getBasePrice();
    }
}
