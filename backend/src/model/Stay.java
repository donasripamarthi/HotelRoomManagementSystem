package model;

import java.time.LocalDateTime;

public class Stay {

    private int stayId;
    private int guestId;
    private int roomId;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private double dailyPrice;
    private double totalBill;

    // Constructor
    public Stay(int stayId, int guestId, int roomId,
                LocalDateTime checkIn, LocalDateTime checkOut,
                double dailyPrice, double totalBill) {

        this.stayId = stayId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.dailyPrice = dailyPrice;
        this.totalBill = totalBill;
    }

    // Getters
    public int getStayId() {
        return stayId;
    }

    public int getGuestId() {
        return guestId;
    }

    public int getRoomId() {
        return roomId;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public double getTotalBill() {
        return totalBill;
    }

    // Setters
    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    // Check whether stay is active
    public boolean isActive() {
        return checkOut == null;
    }

    // Display stay details
    public void displayStay() {
        System.out.println("Stay ID: " + stayId);
        System.out.println("Guest ID: " + guestId);
        System.out.println("Room ID: " + roomId);
        System.out.println("Check In: " + checkIn);
        System.out.println("Check Out: " + checkOut);
        System.out.println("Daily Price: " + dailyPrice);
        System.out.println("Total Bill: " + totalBill);
        System.out.println("Active: " + isActive());
    }
}