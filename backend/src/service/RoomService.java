
package service;

import dao.RoomDAO;
import model.Room;

import java.sql.SQLException;
import java.util.List;

public class RoomService {

    private RoomDAO roomDAO = new RoomDAO();

    // Get all rooms
    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }

    // Get room by ID
    public Room getRoomById(int roomId) throws SQLException {

        if (roomId <= 0) {
            throw new IllegalArgumentException("Invalid room ID.");
        }

        return roomDAO.getRoomById(roomId);
    }

    // Update maintenance status
    public boolean updateMaintenanceStatus(
            int roomId, boolean status) throws SQLException {

        if (roomId <= 0) {
            throw new IllegalArgumentException("Invalid room ID.");
        }

        Room room = roomDAO.getRoomById(roomId);

        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }

        return roomDAO.updateMaintenanceStatus(roomId, status);
    }
}
