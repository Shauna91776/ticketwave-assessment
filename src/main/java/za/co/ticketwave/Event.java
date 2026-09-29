package za.co.ticketwave;

import java.time.LocalDateTime;

public class Event {

    private final String title;
    private final String city;
    private final LocalDateTime dateTime;
    private final double basePrice;
    private final int capacity;

    public Event(String title, String city, LocalDateTime dateTime, double basePrice, int capacity) {
        this.title = title;
        this.city = city;
        this.dateTime = dateTime;
        this.basePrice = basePrice;
        this.capacity = capacity;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return title + " (" + city + ")";
    }
}
