package za.co.ticketwave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private PricingService service;
    private Event event;

    @BeforeEach
    void setUp() {
        service = new PricingService();
        event = new Event("Jazz Night", "Cape Town", LocalDateTime.of(2026, 11, 14, 19, 30), 200.0, 100);
    }

    // Standard: base price, then bulk discount, then 15% VAT

    @Test
    void standard_singleTicketIsBasePlusVat() {
        assertEquals(230.0, service.ticketPrice(event, TicketType.STANDARD, 1), 0.001);
    }

    @Test
    void standard_threeTicketsGetNoBulkDiscount() {
        assertEquals(690.0, service.ticketPrice(event, TicketType.STANDARD, 3), 0.001);
    }

    @Test
    void standard_fourTicketsGetTenPercentOff() {
        // 800 - 10% = 720, plus 15% VAT
        assertEquals(828.0, service.ticketPrice(event, TicketType.STANDARD, 4), 0.001);
    }

    // VIP: 2.5 x base, bulk discount, then R50 lounge fee per ticket, then VAT

    @Test
    void vip_singleTicketIncludesLoungeFee() {
        // 500 + 50 = 550, plus 15% VAT
        assertEquals(632.5, service.ticketPrice(event, TicketType.VIP, 1), 0.001);
    }

    @Test
    void vip_bulkDiscountAppliesBeforeTheLoungeFee() {
        // 2000 - 10% = 1800, + 4 x 50 = 2000, plus 15% VAT
        assertEquals(2300.0, service.ticketPrice(event, TicketType.VIP, 4), 0.001);
    }

    // Student: 60% of base, bulk discount, then VAT

    @Test
    void student_singleTicketIsSixtyPercentPlusVat() {
        assertEquals(138.0, service.ticketPrice(event, TicketType.STUDENT, 1), 0.001);
    }

    @Test
    void student_fourTicketsGetTenPercentOff() {
        // 480 - 10% = 432, plus 15% VAT
        assertEquals(496.8, service.ticketPrice(event, TicketType.STUDENT, 4), 0.001);
    }

    @Test
    void priceScalesWithTheEventsBasePrice() {
        Event pricey = new Event("Gala", "Durban", LocalDateTime.of(2026, 12, 1, 18, 0), 400.0, 50);
        assertEquals(460.0, service.ticketPrice(pricey, TicketType.STANDARD, 1), 0.001);
    }
}
