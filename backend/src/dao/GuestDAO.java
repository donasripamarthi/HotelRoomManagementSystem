
package dao;

import model.Guest;
import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    // Register a new guest
    public int addGuest(Guest guest) throws SQLException {

        String sql = "INSERT INTO guests " +
            "(guest_name, phone, email, address, government_id) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, guest.getName());
            ps.setString(2, guest.getPhone());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getAddress());
            ps.setString(5, guest.getGovernmentId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    // Get guest by ID
    public Guest getGuestById(int guestId) throws SQLException {

        String sql = "SELECT * FROM guests WHERE guest_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, guestId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Guest(
                        rs.getInt("guest_id"),
                        rs.getString("guest_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("government_id")
                    );
                }
            }
        }

        return null;
    }

    // Get all guests
    public List<Guest> getAllGuests() throws SQLException {

        List<Guest> guests = new ArrayList<Guest>();

        String sql = "SELECT * FROM guests ORDER BY guest_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("guest_id"),
                    rs.getString("guest_name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("government_id")
                );

                guests.add(guest);
            }
        }

        return guests;
    }
}
