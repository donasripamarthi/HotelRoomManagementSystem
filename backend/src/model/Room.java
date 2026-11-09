package model;

public class Room {

    private int roomId;
    private String roomNumber;
    private String roomType;
    private double dailyPrice;
    private boolean maintenanceStatus;

    // Constructor
    public Room(int roomId, String roomNumber,
                String roomType, double dailyPrice,
                boolean maintenanceStatus) {

        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.dailyPrice = dailyPrice;
        this.maintenanceStatus = maintenanceStatus;
    }

    // Getters
    public int getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public boolean isMaintenanceStatus() {
        return maintenanceStatus;
    }

    // Setters
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public void setMaintenanceStatus(boolean maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }

    // Display room details
    public void displayRoom() {
        System.out.println("Room ID: " + roomId);
        System.out.println("Room Number: " + roomNumber);
        System.out.println("Room Type: " + roomType);
        System.out.println("Daily Price: " + dailyPrice);
        System.out.println("Under Maintenance: " + maintenanceStatus);
    }
}
