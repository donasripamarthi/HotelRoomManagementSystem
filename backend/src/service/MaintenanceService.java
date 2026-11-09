
package service;

import dao.MaintenanceDAO;
import dao.RoomDAO;

import model.Maintenance;
import model.Room;

import java.sql.SQLException;
import java.util.List;

public class MaintenanceService {

    private MaintenanceDAO maintenanceDAO =
            new MaintenanceDAO();

    private RoomDAO roomDAO = new RoomDAO();

    // Start maintenance
    public boolean startMaintenance(int roomId, String reason)
            throws SQLException {

        if (roomId <= 0) {
            throw new IllegalArgumentException("Invalid room ID.");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Maintenance reason is required."
            );
        }

        Room room = roomDAO.getRoomById(roomId);

        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }

        if (room.isMaintenanceStatus()) {
            throw new IllegalArgumentException(
                "Room is already under maintenance."
            );
        }

        return maintenanceDAO.addMaintenance(
            roomId, reason.trim()
        );
    }

    // Complete maintenance
    public boolean completeMaintenance(int maintenanceId)
            throws SQLException {

        if (maintenanceId <= 0) {
            throw new IllegalArgumentException(
                "Invalid maintenance ID."
            );
        }

        return maintenanceDAO.completeMaintenance(maintenanceId);
    }

    // Get all maintenance records
    public List<Maintenance> getAllMaintenance()
            throws SQLException {

        return maintenanceDAO.getAllMaintenance();
    }
}
