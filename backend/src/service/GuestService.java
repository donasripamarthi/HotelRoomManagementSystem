
package service;

import dao.GuestDAO;
import model.Guest;

import java.sql.SQLException;
import java.util.List;

public class GuestService {

    private GuestDAO guestDAO = new GuestDAO();

    // Register guest
    public int registerGuest(Guest guest) throws SQLException {

        if (guest == null) {
            throw new IllegalArgumentException("Guest details required.");
        }

        if (guest.getName() == null ||
            guest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name is required.");
        }

        if (guest.getPhone() == null ||
            !guest.getPhone().matches("[0-9]{10,15}")) {
            throw new IllegalArgumentException("Enter a valid phone number.");
        }

        if (guest.getEmail() != null &&
            !guest.getEmail().trim().isEmpty() &&
            !guest.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Enter a valid email.");
        }

        if (guest.getGovernmentId() == null ||
            guest.getGovernmentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Government ID is required.");
        }

        return guestDAO.addGuest(guest);
    }

    // Get guest by ID
    public Guest getGuestById(int guestId) throws SQLException {

        if (guestId <= 0) {
            throw new IllegalArgumentException("Invalid guest ID.");
        }

        return guestDAO.getGuestById(guestId);
    }

    // Get all guests
    public List<Guest> getAllGuests() throws SQLException {
        return guestDAO.getAllGuests();
    }
}
