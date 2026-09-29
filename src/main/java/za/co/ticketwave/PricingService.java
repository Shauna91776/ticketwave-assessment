package za.co.ticketwave;

public class PricingService {

    public double ticketPrice(Event event, TicketType type, int quantity) {
        double total;
        if (type == TicketType.STANDARD) {
            total = event.getBasePrice() * quantity;
            if (quantity >= 4) {
                total = total - total * 0.10;
            }
            total = total + total * 0.15;
        } else if (type == TicketType.VIP) {
            total = event.getBasePrice() * 2.5 * quantity;
            if (quantity >= 4) {
                total = total - total * 0.10;
            }
            total = total + 50 * quantity;
            total = total + total * 0.15;
        } else if (type == TicketType.STUDENT) {
            total = event.getBasePrice() * 0.6 * quantity;
            if (quantity >= 4) {
                total = total - total * 0.10;
            }
            total = total + total * 0.15;
        } else {
            throw new IllegalArgumentException("Unknown ticket type");
        }
        return total;
    }
}
