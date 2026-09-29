package za.co.ticketwave;

/**
 * The SQL behind TicketWave's reporting screens. Replace each TODO with a query
 * that satisfies the description above it. Column names matter: see
 * EventQueriesTest. Only bookings whose status is 'CONFIRMED' count as sold
 * tickets; 'CANCELLED' bookings never do.
 */
public final class EventQueries {

    private EventQueries() {
    }

    /**
     * One parameter (?) : the customer's id.
     * Columns: title, quantity.
     * Every booking that customer has made (whatever its status), with the title
     * of the event it is for. Earliest event first (event_date), then title (A to Z).
     */
    public static final String BOOKINGS_FOR_CUSTOMER = "TODO";

    /**
     * Columns: title, tickets_sold.
     * One row for EVERY event, including events nobody has booked, with the total
     * quantity of its CONFIRMED bookings (0 when there are none). Most tickets sold
     * first; ties broken by title (A to Z).
     */
    public static final String TICKETS_SOLD_PER_EVENT = "TODO";

    /**
     * Columns: title.
     * The events that are sold out: those whose CONFIRMED tickets add up to at
     * least the event's capacity. Ordered by title (A to Z).
     */
    public static final String SOLD_OUT_EVENTS = "TODO";
}
