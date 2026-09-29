package za.co.ticketwave;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VipTicketTest {

    @Test
    void price_isTwoAndAHalfTimesTheBasePrice() {
        assertEquals(625.0, new VipTicket("A1", 250.0).price(), 0.001);
    }

    @Test
    void perks_mentionTheLounge() {
        assertTrue(new VipTicket("A1", 250.0).perks().contains("Lounge"));
    }

    @Test
    void getSeat_returnsSeatProvided() {
        assertEquals("A2", new VipTicket("A2", 100.0).getSeat());
    }

    @Test
    void priceIsAvailableThroughTheTicketType() {
        Ticket ticket = new VipTicket("A3", 100.0);
        assertEquals(250.0, ticket.price(), 0.001);
    }
}
