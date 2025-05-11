class Passenger {
    private int passengerId;
    private String name;
    private String passportNumber;
    private String dateOfBirth;
    private String specialRequests;

    public Passenger(int passengerId, String name, String passportNumber, String dateOfBirth, String specialRequests) {
        this.passengerId = passengerId;
        this.name = name;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.specialRequests = specialRequests;
    }

    public void updateInfo(String name, String passportNumber, String dateOfBirth, String specialRequests) {
        this.name = name;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.specialRequests = specialRequests;
    }

    public String getPassengerDetails() {
        return "ID: " + passengerId + "\n" +
                "Name: " + name + "\n" +
                "Passport: " + passportNumber + "\n" +
                "DOB: " + dateOfBirth + "\n" +
                "Requests: " + specialRequests;
    }

    // Getters
    public int getPassengerId() { return passengerId; }
    public String getName() { return name; }
    public String getPassportNumber() { return passportNumber; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getSpecialRequests() { return specialRequests; }
}