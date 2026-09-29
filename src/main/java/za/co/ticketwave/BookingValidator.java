package za.co.ticketwave;

/**
 * Validation rules for bookings accepted by TicketWave.
 *
 * <ul>
 *   <li>A booking is for <b>at least 1</b> and <b>at most 8</b> tickets (both limits are allowed).</li>
 *   <li>A seat label is one upper-case letter (row) followed by a number from
 *       1 to 99 with no leading zero (e.g. {@code B12}, {@code A1}; not {@code B012},
 *       {@code b12} or {@code B100}).</li>
 *   <li>A promo code is {@code TW-} followed by exactly six upper-case letters or digits
 *       (e.g. {@code TW-9K2M4A}).</li>
 *   <li>An email address has text, then a single {@code @}, then text, a dot and more text,
 *       with no spaces (e.g. {@code lerato@example.com}).</li>
 * </ul>
 */
public final class BookingValidator {

    public static final int MAX_TICKETS = 8;

    private BookingValidator() {
    }

    /**
     * @throws IllegalArgumentException if the quantity is less than 1 or more than 8
     */
    public static void requireValidQuantity(int quantity) {
        if (quantity < 1 || quantity >= MAX_TICKETS) {
            throw new IllegalArgumentException(
                    "Quantity must be between 1 and " + MAX_TICKETS + " but was " + quantity);
        }
    }

    public static boolean isValidSeat(String seat) {
        return seat != null && seat.matches("[A-Z]([1-9]|[1-9][0-9])");
    }

    public static boolean isValidPromoCode(String code) {
        return code != null && code.matches("TW-[A-Z0-9]{6}");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }
}
