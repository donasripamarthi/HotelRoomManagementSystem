
package dao;

import database.DBConnection;
import model.Stay;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class StayDAO {

    // Check in a guest
    public boolean checkIn(int guestId, int roomId)
            throws SQLException {

        String roomSql =
            "SELECT daily_price, maintenance_status " +
            "FROM rooms WHERE room_id = ? FOR UPDATE";

        String activeSql =
            "SELECT stay_id FROM stays " +
            "WHERE room_id = ? AND check_out IS NULL";

        String insertSql =
            "INSERT INTO stays " +
            "(guest_id, room_id, check_in, daily_price) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                double dailyPrice;

                // Lock and verify the room
                try (PreparedStatement ps =
                         con.prepareStatement(roomSql)) {

                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Room not found.");
                        }

                        if (rs.getBoolean("maintenance_status")) {
                            throw new SQLException(
                                "Room is under maintenance."
                            );
                        }

                        dailyPrice = rs.getDouble("daily_price");
                    }
                }

                // Check existing active stay
                try (PreparedStatement ps =
                         con.prepareStatement(activeSql)) {

                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException(
                                "Room is already occupied."
                            );
                        }
                    }
                }

                // Insert new stay
                try (PreparedStatement ps =
                         con.prepareStatement(insertSql)) {

                    ps.setInt(1, guestId);
                    ps.setInt(2, roomId);
                    ps.setTimestamp(
                        3, Timestamp.valueOf(LocalDateTime.now())
                    );
                    ps.setDouble(4, dailyPrice);

                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Check out a guest
    public boolean checkOut(int stayId) throws SQLException {

        String selectSql =
            "SELECT check_in, daily_price FROM stays " +
            "WHERE stay_id = ? AND check_out IS NULL FOR UPDATE";

        String updateSql =
            "UPDATE stays SET check_out = ?, total_bill = ? " +
            "WHERE stay_id = ? AND check_out IS NULL";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                LocalDateTime checkIn;
                double dailyPrice;
                LocalDateTime checkOut = LocalDateTime.now();

                // Find active stay
                try (PreparedStatement ps =
                         con.prepareStatement(selectSql)) {

                    ps.setInt(1, stayId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                "Active stay not found."
                            );
                        }

                        checkIn = rs.getTimestamp("check_in")
                                    .toLocalDateTime();

                        dailyPrice = rs.getDouble("daily_price");
                    }
                }

                // Calculate minimum one-day bill
                long seconds = Duration.between(
                    checkIn, checkOut
                ).getSeconds();

                long days = Math.max(
                    1, (seconds + 86399) / 86400
                );

                double totalBill = days * dailyPrice;

                // Update checkout details
                try (PreparedStatement ps =
                         con.prepareStatement(updateSql)) {

                    ps.setTimestamp(
                        1, Timestamp.valueOf(checkOut)
                    );

                    ps.setDouble(2, totalBill);
                    ps.setInt(3, stayId);

                    int rows = ps.executeUpdate();

                    if (rows == 0) {
                        throw new SQLException(
                            "Checkout could not be completed."
                        );
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Get all stay records
    public List<Stay> getAllStays() throws SQLException {

        List<Stay> stays = new ArrayList<Stay>();

        String sql = "SELECT * FROM stays ORDER BY stay_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Timestamp in = rs.getTimestamp("check_in");
                Timestamp out = rs.getTimestamp("check_out");

                Stay stay = new Stay(
                    rs.getInt("stay_id"),
                    rs.getInt("guest_id"),
                    rs.getInt("room_id"),
                    in.toLocalDateTime(),
                    out == null ? null : out.toLocalDateTime(),
                    rs.getDouble("daily_price"),
                    rs.getDouble("total_bill")
                );

                stays.add(stay);
            }
        }

        return stays;
    }
}
