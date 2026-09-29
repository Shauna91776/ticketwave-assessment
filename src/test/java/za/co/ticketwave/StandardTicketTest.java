package za.co.ticketwave;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardTicketTest {

    @Test
    void price_isTheBasePrice() {
        assertEquals(250.0, new StandardTicket("B12", 250.0).price(), 0.001);
    }

    @Test
    void perks_describeStandardEntry() {
        assertEquals("Standard entry", new StandardTicket("B12", 250.0).perks());
    }

    @Test
    void getSeat_returnsSeatProvided() {
        assertEquals("C7", new StandardTicket("C7", 100.0).getSeat());
    }

    @Test
    void canBeUsedAsATicket() {
        Ticket ticket = new StandardTicket("A1", 90.0);
        assertEquals(90.0, ticket.getBasePrice(), 0.001);
    }
}
