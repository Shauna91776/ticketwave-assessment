package za.co.ticketwave;

import org.apache.commons.lang3.StringUtils;

public final class BookingReference {

    private BookingReference() {
    }

    /** 42 becomes "BK-000042". Sequences longer than six digits are not truncated. */
    public static String forSequence(int sequence) {
        return "BK-" + StringUtils.leftPad(String.valueOf(sequence), 6, '0');
    }
}
