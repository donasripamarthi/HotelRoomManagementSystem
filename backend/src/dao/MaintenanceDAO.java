
package dao;

import database.DBConnection;
import model.Maintenance;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceDAO {

    // Add maintenance to a room
    public boolean addMaintenance(int roomId, String reason)
            throws SQLException {

        String roomSql =
            "SELECT room_id FROM rooms WHERE room_id = ? FOR UPDATE";

        String insertSql =
            "INSERT INTO maintenance " +
            "(room_id, reason, start_date, status) " +
            "VALUES (?, ?, ?, 'ACTIVE')";

        String updateSql =
            "UPDATE rooms SET maintenance_status = TRUE " +
            "WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                // Check whether room exists
                try (PreparedStatement ps =
                         con.prepareStatement(roomSql)) {

                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Room not found.");
                        }
                    }
                }

                // Insert maintenance record
                try (PreparedStatement ps =
                         con.prepareStatement(insertSql)) {

                    ps.setInt(1, roomId);
                    ps.setString(2, reason);
                    ps.setTimestamp(
                        3, Timestamp.valueOf(LocalDateTime.now())
                    );

                    ps.executeUpdate();
                }

                // Mark room as under maintenance
                try (PreparedStatement ps =
                         con.prepareStatement(updateSql)) {

                    ps.setInt(1, roomId);
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

    // Complete maintenance
    public boolean completeMaintenance(int maintenanceId)
            throws SQLException {

        String selectSql =
            "SELECT room_id FROM maintenance " +
            "WHERE maintenance_id = ? AND status = 'ACTIVE' " +
            "FOR UPDATE";

        String updateMaintenanceSql =
            "UPDATE maintenance SET status = 'COMPLETED', " +
            "end_date = ? WHERE maintenance_id = ?";

        String checkSql =
            "SELECT COUNT(*) FROM maintenance " +
            "WHERE room_id = ? AND status = 'ACTIVE'";

        String updateRoomSql =
            "UPDATE rooms SET maintenance_status = FALSE " +
            "WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int roomId;

                // Find active maintenance record
                try (PreparedStatement ps =
                         con.prepareStatement(selectSql)) {

                    ps.setInt(1, maintenanceId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                "Active maintenance record not found."
                            );
                        }

                        roomId = rs.getInt("room_id");
                    }
                }

                // Mark maintenance completed
                try (PreparedStatement ps =
                         con.prepareStatement(updateMaintenanceSql)) {

                    ps.setTimestamp(
                        1, Timestamp.valueOf(LocalDateTime.now())
                    );

                    ps.setInt(2, maintenanceId);
                    ps.executeUpdate();
                }

                // Check for other active maintenance records
                int activeCount;

                try (PreparedStatement ps =
                         con.prepareStatement(checkSql)) {

                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        activeCount = rs.getInt(1);
                    }
                }

                // Make room available for new assignments
                // only if no other maintenance is active
                if (activeCount == 0) {
                    try (PreparedStatement ps =
                             con.prepareStatement(updateRoomSql)) {

                        ps.setInt(1, roomId);
                        ps.executeUpdate();
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

    // Get all maintenance records
    public List<Maintenance> getAllMaintenance()
            throws SQLException {

        List<Maintenance> records =
            new ArrayList<Maintenance>();

        String sql =
            "SELECT * FROM maintenance ORDER BY maintenance_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Timestamp start = rs.getTimestamp("start_date");
                Timestamp end = rs.getTimestamp("end_date");

                Maintenance maintenance = new Maintenance(
                    rs.getInt("maintenance_id"),
                    rs.getInt("room_id"),
                    rs.getString("reason"),
                    start.toLocalDateTime(),
                    end == null ? null : end.toLocalDateTime(),
                    rs.getString("status")
                );

                records.add(maintenance);
            }
        }

        return records;
    }
}
