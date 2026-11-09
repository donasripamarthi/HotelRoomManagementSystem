
package dao;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    // GET ROOM COUNTS
    public Map<String, Integer> getRoomCounts() throws SQLException {

        Map<String, Integer> counts =
                new LinkedHashMap<String, Integer>();

        String sql =
            "SELECT " +
            "(SELECT COUNT(*) FROM rooms) AS total_rooms, " +

            "(SELECT COUNT(*) FROM rooms r " +
            "WHERE r.maintenance_status = FALSE " +
            "AND NOT EXISTS (" +
            "SELECT 1 FROM stays s " +
            "WHERE s.room_id = r.room_id " +
            "AND s.check_out IS NULL)) AS available_rooms, " +

            "(SELECT COUNT(DISTINCT room_id) FROM stays " +
            "WHERE check_out IS NULL) AS occupied_rooms, " +

            "(SELECT COUNT(*) FROM rooms " +
            "WHERE maintenance_status = TRUE) AS maintenance_rooms";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                counts.put("totalRooms", rs.getInt("total_rooms"));

                counts.put("availableRooms",
                        rs.getInt("available_rooms"));

                counts.put("occupiedRooms",
                        rs.getInt("occupied_rooms"));

                counts.put("maintenanceRooms",
                        rs.getInt("maintenance_rooms"));
            }
        }

        return counts;
    }

    // GET RECENT CHECK-INS
    public List<Map<String, String>> getRecentCheckIns()
            throws SQLException {

        List<Map<String, String>> records =
                new ArrayList<Map<String, String>>();

        String sql =
            "SELECT s.stay_id, g.guest_name, r.room_number, s.check_in " +
            "FROM stays s " +
            "JOIN guests g ON s.guest_id = g.guest_id " +
            "JOIN rooms r ON s.room_id = r.room_id " +
            "ORDER BY s.check_in DESC LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, String> record =
                        new LinkedHashMap<String, String>();

                record.put("stayId",
                        String.valueOf(rs.getInt("stay_id")));

                record.put("guestName",
                        rs.getString("guest_name"));

                record.put("roomNumber",
                        rs.getString("room_number"));

                record.put("date",
                        rs.getTimestamp("check_in").toString());

                records.add(record);
            }
        }

        return records;
    }

    // GET RECENT CHECK-OUTS
    public List<Map<String, String>> getRecentCheckOuts()
            throws SQLException {

        List<Map<String, String>> records =
                new ArrayList<Map<String, String>>();

        String sql =
            "SELECT s.stay_id, g.guest_name, r.room_number, s.check_out " +
            "FROM stays s " +
            "JOIN guests g ON s.guest_id = g.guest_id " +
            "JOIN rooms r ON s.room_id = r.room_id " +
            "WHERE s.check_out IS NOT NULL " +
            "ORDER BY s.check_out DESC LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, String> record =
                        new LinkedHashMap<String, String>();

                record.put("stayId",
                        String.valueOf(rs.getInt("stay_id")));

                record.put("guestName",
                        rs.getString("guest_name"));

                record.put("roomNumber",
                        rs.getString("room_number"));

                record.put("date",
                        rs.getTimestamp("check_out").toString());

                records.add(record);
            }
        }

        return records;
    }
}

