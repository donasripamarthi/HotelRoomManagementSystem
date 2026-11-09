
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import database.DBConnection;

public class Main {

    public static void main(String[] args) {

        try {
            Connection con = DBConnection.getConnection();

            System.out.println("MySQL Connected Successfully!");

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM rooms"
            );

            System.out.println("\nHOTEL ROOM DETAILS");
            System.out.println("-----------------------------");

            while (rs.next()) {

                System.out.println(
                    "Room Number: " + rs.getString("room_number")
                );

                System.out.println(
                    "Room Type: " + rs.getString("room_type")
                );

                System.out.println(
                    "Daily Price: " + rs.getDouble("daily_price")
                );

                System.out.println("-----------------------------");
            }

            rs.close();
            stmt.close();
            con.close();

        } catch (Exception e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
    }
}

