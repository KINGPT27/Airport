import java.time.*;
import java.util.Set;

class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String method;
    private String status;
    private LocalDate transactionDate;

    public Payment(int paymentId, int bookingId, double amount, String method, String status, LocalDate transactionDate) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionDate = transactionDate;
    }

    public boolean processPayment() {
        if (validatePaymentDetails()) {
            this.status = "Completed";
            this.transactionDate = LocalDate.now();
            return true;
        }
        return false;
    }

    public boolean validatePaymentDetails() {
        Set<String> validMethods = Set.of("Credit Card", "Bank Transfer", "Cash");
        return amount > 0 && validMethods.contains(method);
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public int getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public LocalDate getTransactionDate() { return transactionDate; }
}