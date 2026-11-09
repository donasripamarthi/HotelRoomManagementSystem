package dao;

import model.Room;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // Get all rooms
    public List<Room> getAllRooms() throws SQLException {

        List<Room> rooms = new ArrayList<Room>();

        String sql = "SELECT * FROM rooms";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Room room = new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getDouble("daily_price"),
                    rs.getBoolean("maintenance_status")
                );

                rooms.add(room);
            }
        }

        return rooms;
    }

    // Get room by ID
    public Room getRoomById(int roomId) throws SQLException {

        String sql = "SELECT * FROM rooms WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("daily_price"),
                        rs.getBoolean("maintenance_status")
                    );
                }
            }
        }

        return null;
    }

    // Update room maintenance status
    public boolean updateMaintenanceStatus(
            int roomId, boolean status) throws SQLException {

        String sql =
            "UPDATE rooms SET maintenance_status = ? WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, status);
            ps.setInt(2, roomId);

            return ps.executeUpdate() > 0;
        }
    }
}