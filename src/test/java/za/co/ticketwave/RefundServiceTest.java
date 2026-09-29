package za.co.ticketwave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefundServiceTest {

    private static final LocalDateTime EVENT_START = LocalDateTime.of(2026, 12, 20, 18, 0);

    private RefundService service;
    private Booking booking;

    @BeforeEach
    void setUp() {
        service = new RefundService();
        Event event = new Event("Year End Bash", "Johannesburg", EVENT_START, 250.0, 500);
        booking = new Booking("BK-000001", event, 2, 1000.0);
    }

    private double refundWhenCancelled(long hoursBeforeEvent) {
        return service.refundAmount(booking, EVENT_START.minusHours(hoursBeforeEvent));
    }

    @Test
    void fullRefundWellInAdvance() {
        assertEquals(1000.0, refundWhenCancelled(240), 0.001);
    }

    @Test
    void fullRefundExactlySevenDaysBefore() {
        assertEquals(1000.0, refundWhenCancelled(168), 0.001);
    }

    @Test
    void seventyFivePercentJustInsideSevenDays() {
        assertEquals(750.0, refundWhenCancelled(167), 0.001);
        assertEquals(750.0, service.refundAmount(booking, EVENT_START.minusHours(168).plusMinutes(1)), 0.001);
    }

    @Test
    void seventyFivePercentExactlyFortyEightHoursBefore() {
        assertEquals(750.0, refundWhenCancelled(48), 0.001);
    }

    @Test
    void fiftyPercentJustInsideFortyEightHours() {
        assertEquals(500.0, refundWhenCancelled(47), 0.001);
    }

    @Test
    void fiftyPercentExactlyTwentyFourHoursBefore() {
        assertEquals(500.0, refundWhenCancelled(24), 0.001);
    }

    @Test
    void noRefundInsideTwentyFourHours() {
        assertEquals(0.0, refundWhenCancelled(23), 0.001);
        assertEquals(0.0, refundWhenCancelled(1), 0.001);
    }

    @Test
    void noRefundAfterTheEventHasStarted() {
        assertEquals(0.0, service.refundAmount(booking, EVENT_START.plusHours(2)), 0.001);
    }

    @Test
    void noRefundForABookingThatIsAlreadyCancelled() {
        booking.cancel();
        assertEquals(0.0, refundWhenCancelled(240), 0.001);
    }

    @Test
    void refundIsProportionalToWhatWasPaid() {
        Event event = new Event("Comedy Club", "Pretoria", EVENT_START, 100.0, 80);
        Booking small = new Booking("BK-000002", event, 1, 200.0);
        assertEquals(150.0, service.refundAmount(small, EVENT_START.minusHours(100)), 0.001);
    }
}
