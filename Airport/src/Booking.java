import java.util.*;

class Booking {
    private int bookingId;
    private Customer customer;
    private Flight flight;
    private List<Passenger> passengers;
    private String seatSelections;
    private String status;
    private String paymentStatus;

    public Booking(int bookingId, Customer customer, Flight flight,
                   List<Passenger> passengers, String seatSelections,
                   String status, String paymentStatus) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.flight = flight;
        this.passengers = passengers;
        this.seatSelections = seatSelections;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

    public double calculateTotalPrice() {
        return flight.calculatePrice(seatSelections) * passengers.size();
    }

    public void confirmBooking() {
        this.status = "Confirmed";
        this.paymentStatus = "Paid";
    }

    public void cancelBooking() {
        this.status = "Cancelled";
        this.paymentStatus = "Refunded";
        flight.releaseSeat(passengers.size());
    }

    public String generateItinerary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== FLIGHT ITINERARY ===\n\n");
        sb.append("Booking ID: ").append(bookingId).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Payment: ").append(paymentStatus).append("\n\n");

        sb.append("=== FLIGHT DETAILS ===\n");
        sb.append(flight.getAirline()).append(" Flight ").append(flight.getFlightNumber()).append("\n");
        sb.append("Route: ").append(flight.getOrigin()).append(" to ").append(flight.getDestination()).append("\n");
        sb.append("Date: ").append(flight.getDepartureDate()).append("\n");
        sb.append("Departure: ").append(flight.getDepartureTime()).append("\n");
        sb.append("Arrival: ").append(flight.getArrivalTime()).append("\n");
        sb.append("Class: ").append(seatSelections).append("\n\n");

        sb.append("=== PASSENGERS ===\n");
        for (Passenger p : passengers) {
            sb.append("- ").append(p.getName()).append(" (Passport: ").append(p.getPassportNumber()).append(")\n");
        }

        sb.append("\n=== TOTAL ===\n");
        sb.append("$").append(String.format("%.2f", calculateTotalPrice())).append("\n");

        return sb.toString();
    }




    // Getters and setters
    public int getBookingId() { return bookingId; }
    public Customer getCustomer() { return customer; }
    public Flight getFlight() { return flight; }
    public List<Passenger> getPassengers() { return passengers; }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setSeatSelections(String seatSelections) { this.seatSelections = seatSelections; }

    public CharSequence getSeatSelections() {
        return seatSelections;
    }

    public String getSeatSelection() {
        return seatSelections;
    }

    public void setSeatSelection(String seatSelection) {
        this.seatSelections = seatSelection;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}