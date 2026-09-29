package za.co.ticketwave;

import java.time.Duration;
import java.time.LocalDateTime;

public class RefundService {

    public double refundAmount(Booking booking, LocalDateTime cancelledAt) {
        double refund = 0;
        long hoursBefore = Duration.between(cancelledAt, booking.getEvent().getDateTime()).toHours();
        if (booking.isCancelled()) {
            refund = 0;
        } else if (hoursBefore < 0) {
            refund = 0;
        } else if (hoursBefore >= 168) {
            refund = booking.getAmountPaid();
        } else if (hoursBefore < 168 && hoursBefore >= 48) {
            refund = booking.getAmountPaid() * 0.75;
        } else if (hoursBefore < 48 && hoursBefore >= 24) {
            refund = booking.getAmountPaid() * 0.5;
        } else if (hoursBefore < 24) {
            refund = 0;
        }
        return refund;
    }
}
