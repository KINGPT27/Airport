import java.time.*;

class Flight {
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private int availableSeats;
    private double[] prices;
    private LocalDate departureDate;

    public Flight(String flightNumber, String airline, String origin, String destination,
                  String departureTime, String arrivalTime, int availableSeats,
                  double[] prices, LocalDate departureDate) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.prices = prices;
        this.departureDate = departureDate;
    }

    public Flight(String number, String airline, String origin, String destination, String dep, String arr, int seats, String date, double price) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.prices = prices;
        this.departureDate = departureDate; }

    public boolean checkAvailability(int requiredSeats) {
        return availableSeats >= requiredSeats;
    }

    public synchronized boolean reserveSeat(int seats) {
        if (availableSeats >= seats) {
            availableSeats -= seats;
            return true;
        }
        return false;
    }

    public synchronized void releaseSeat(int seats) {
        availableSeats += seats;
    }

    public double calculatePrice(String seatClass) {
        switch(seatClass.toLowerCase()) {
            case "economy": return prices[0];
            case "business": return prices[1];
            case "first": return prices[2];
            default: return prices[0];
        }
    }
    public void updateSchedule(String newDepartureTime, String newArrivalTime) {
        this.departureTime = newDepartureTime;
        this.arrivalTime = newArrivalTime;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }





    // Getters
    public String getFlightNumber() { return flightNumber; }
    public String getAirline() { return airline; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public int getAvailableSeats() { return availableSeats; }
    public LocalDate getDepartureDate() { return departureDate; }

    public Object getBasePrice() {
        return prices;
    }
}