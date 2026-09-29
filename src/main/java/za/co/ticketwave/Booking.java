package za.co.ticketwave;

public class Booking {

    private final String reference;
    private final Event event;
    private final int quantity;
    private final double amountPaid;
    private boolean cancelled;

    public Booking(String reference, Event event, int quantity, double amountPaid) {
        this.reference = reference;
        this.event = event;
        this.quantity = quantity;
        this.amountPaid = amountPaid;
        this.cancelled = false;
    }

    public String getReference() {
        return reference;
    }

    public Event getEvent() {
        return event;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
