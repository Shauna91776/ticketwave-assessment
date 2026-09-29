package za.co.ticketwave;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingReferenceTest {

    @Test
    void forSequence_padsToSixDigits() {
        assertEquals("BK-000042", BookingReference.forSequence(42));
    }

    @Test
    void forSequence_doesNotTruncateLongSequences() {
        assertEquals("BK-1234567", BookingReference.forSequence(1234567));
    }
}
