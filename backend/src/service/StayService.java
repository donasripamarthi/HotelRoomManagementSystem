
package service;

import dao.StayDAO;
import dao.GuestDAO;
import dao.RoomDAO;

import model.Stay;
import model.Guest;
import model.Room;

import java.sql.SQLException;
import java.util.List;

public class StayService {

    private StayDAO stayDAO = new StayDAO();
    private GuestDAO guestDAO = new GuestDAO();
    private RoomDAO roomDAO = new RoomDAO();

    // Check in guest
    public boolean checkIn(int guestId, int roomId)
            throws SQLException {

        if (guestId <= 0 || roomId <= 0) {
            throw new IllegalArgumentException("Invalid guest or room ID.");
        }

        Guest guest = guestDAO.getGuestById(guestId);

        if (guest == null) {
            throw new IllegalArgumentException("Guest not found.");
        }

        Room room = roomDAO.getRoomById(roomId);

        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }

        if (room.isMaintenanceStatus()) {
            throw new IllegalArgumentException(
                "Room is under maintenance."
            );
        }

        return stayDAO.checkIn(guestId, roomId);
    }

    // Check out guest
    public boolean checkOut(int stayId) throws SQLException {

        if (stayId <= 0) {
            throw new IllegalArgumentException("Invalid stay ID.");
        }

        return stayDAO.checkOut(stayId);
    }

    // Get all stay records
    public List<Stay> getAllStays() throws SQLException {
        return stayDAO.getAllStays();
    }
}
