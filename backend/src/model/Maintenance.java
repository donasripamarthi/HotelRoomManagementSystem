package model;

import java.time.LocalDateTime;

public class Maintenance {

    private int maintenanceId;
    private int roomId;

    private String reason;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;

    // Constructor
    public Maintenance(int maintenanceId, int roomId,
                       String reason,
                       LocalDateTime startDate,
                       LocalDateTime endDate,
                       String status) {

        this.maintenanceId = maintenanceId;
        this.roomId = roomId;
        this.reason = reason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters
    public int getMaintenanceId() {
        return maintenanceId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Check maintenance status
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    // Display maintenance details
    public void displayMaintenance() {
        System.out.println("Maintenance ID: " + maintenanceId);
        System.out.println("Room ID: " + roomId);
        System.out.println("Reason: " + reason);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Status: " + status);
    }
}