package za.co.ticketwave;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        Ticket standard = new StandardTicket("B12", 250.0);
        Ticket vip = new VipTicket("A1", 250.0);
        System.out.println(standard);
        System.out.println(vip);

        Event jazz = new Event("Jazz Night", "Cape Town", LocalDateTime.of(2026, 11, 14, 19, 30), 250.0, 100);
        PricingService pricing = new PricingService();
        System.out.printf("4 standard tickets: R%.2f%n", pricing.ticketPrice(jazz, TicketType.STANDARD, 4));
        System.out.println("First booking reference: " + BookingReference.forSequence(1));
    }
}
