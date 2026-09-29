package za.co.ticketwave;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Q3.2 - BookingValidator has no meaningful test coverage yet.
 * The rules it must follow are written in the Javadoc of BookingValidator.
 * One worked example is provided. Add your own tests below it.
 */
class BookingValidatorTest {

    @Test
    void requireValidQuantity_acceptsTypicalQuantity() {
        assertDoesNotThrow(() -> BookingValidator.requireValidQuantity(4));
    }

    // TODO (Q3.2): add at least SIX more tests. Do not edit the test above.
}
