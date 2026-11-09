package model;

public class Guest {

    private int guestId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String governmentId;

    // Constructor
    public Guest(int guestId, String name, String phone,
                 String email, String address, String governmentId) {

        this.guestId = guestId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.governmentId = governmentId;
    }

    // Getters
    public int getGuestId() {
        return guestId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getGovernmentId() {
        return governmentId;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGovernmentId(String governmentId) {
        this.governmentId = governmentId;
    }

    // Display guest details
    public void displayGuest() {
        System.out.println("Guest ID: " + guestId);
        System.out.println("Name: " + name);
        System.out.println("Phone: " + phone);
        System.out.println("Email: " + email);
        System.out.println("Address: " + address);
        System.out.println("Government ID: " + governmentId);
    }
}