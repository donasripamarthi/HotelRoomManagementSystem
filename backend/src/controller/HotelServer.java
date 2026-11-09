
package controller;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import service.RoomService;
import service.GuestService;
import service.StayService;
import service.MaintenanceService;
import service.DashboardService;

import model.Room;
import model.Guest;
import model.Stay;
import model.Maintenance;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HotelServer {

    private static RoomService roomService = new RoomService();
    private static GuestService guestService = new GuestService();
    private static StayService stayService = new StayService();
    private static MaintenanceService maintenanceService =
            new MaintenanceService();
    private static DashboardService dashboardService =
            new DashboardService();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/api", HotelServer::handleRequest);

        server.setExecutor(null);
        server.start();

        System.out.println("Hotel Management Server Started!");
        System.out.println("Server URL: http://localhost:8080");
    }

    // HANDLE ALL API REQUESTS
    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {

            // GET ALL ROOMS
            if (method.equals("GET")
                    && path.equals("/api/rooms")) {

                List<Room> rooms = roomService.getAllRooms();

                sendJson(exchange, roomsToJson(rooms));
            }

            // GET ALL GUESTS
            else if (method.equals("GET")
                    && path.equals("/api/guests")) {

                List<Guest> guests = guestService.getAllGuests();

                sendJson(exchange, guestsToJson(guests));
            }

            // GET ALL STAYS
            else if (method.equals("GET")
                    && path.equals("/api/stays")) {

                List<Stay> stays = stayService.getAllStays();

                sendJson(exchange, staysToJson(stays));
            }

            // GET MAINTENANCE RECORDS
            else if (method.equals("GET")
                    && path.equals("/api/maintenance")) {

                List<Maintenance> records =
                        maintenanceService.getAllMaintenance();

                sendJson(exchange, maintenanceToJson(records));
            }

            // GET DASHBOARD DATA
            else if (method.equals("GET")
                    && path.equals("/api/dashboard")) {

                Map<String, Object> dashboard =
                        dashboardService.getDashboardData();

                sendJson(exchange, dashboardToJson(dashboard));
            }

            // REGISTER GUEST
            else if (method.equals("POST")
                    && path.equals("/api/guests")) {

                Map<String, String> data = readFormData(exchange);

                Guest guest = new Guest(
                        0,
                        data.get("name"),
                        data.get("phone"),
                        data.get("email"),
                        data.get("address"),
                        data.get("governmentId")
                );

                int guestId = guestService.registerGuest(guest);

                sendJson(exchange,
                        "{\"success\":true,\"guestId\":" + guestId + "}");
            }

            // CHECK IN
            else if (method.equals("POST")
                    && path.equals("/api/checkin")) {

                Map<String, String> data = readFormData(exchange);

                int guestId = Integer.parseInt(data.get("guestId"));
                int roomId = Integer.parseInt(data.get("roomId"));

                stayService.checkIn(guestId, roomId);

                sendJson(exchange,
                        "{\"success\":true,\"message\":\"Check-in successful\"}");
            }

            // CHECK OUT
            else if (method.equals("POST")
                    && path.equals("/api/checkout")) {

                Map<String, String> data = readFormData(exchange);

                int stayId = Integer.parseInt(data.get("stayId"));

                stayService.checkOut(stayId);

                sendJson(exchange,
                        "{\"success\":true,\"message\":\"Check-out successful\"}");
            }

            // START MAINTENANCE
            else if (method.equals("POST")
                    && path.equals("/api/maintenance/start")) {

                Map<String, String> data = readFormData(exchange);

                int roomId = Integer.parseInt(data.get("roomId"));
                String reason = data.get("reason");

                maintenanceService.startMaintenance(roomId, reason);

                sendJson(exchange,
                        "{\"success\":true,\"message\":\"Maintenance started\"}");
            }

            // COMPLETE MAINTENANCE
            else if (method.equals("POST")
                    && path.equals("/api/maintenance/complete")) {

                Map<String, String> data = readFormData(exchange);

                int maintenanceId =
                        Integer.parseInt(data.get("maintenanceId"));

                maintenanceService.completeMaintenance(maintenanceId);

                sendJson(exchange,
                        "{\"success\":true,\"message\":\"Maintenance completed\"}");
            }

            // INVALID ENDPOINT
            else {
                sendError(exchange, 404, "Endpoint not found");
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendError(exchange, 400,
                    e.getMessage() == null
                            ? "Request failed"
                            : e.getMessage());
        }
    }

    // READ FORM DATA - JAVA 8
    private static Map<String, String> readFormData(
            HttpExchange exchange) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] temp = new byte[1024];
        int length;

        while ((length = exchange.getRequestBody().read(temp)) != -1) {
            buffer.write(temp, 0, length);
        }

        String body = new String(
                buffer.toByteArray(),
                StandardCharsets.UTF_8);

        Map<String, String> data =
                new HashMap<String, String>();

        for (String pair : body.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(parts[0], "UTF-8");

            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], "UTF-8")
                    : "";

            data.put(key, value);
        }

        return data;
    }

    // ROOM JSON
    private static String roomsToJson(List<Room> rooms) {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < rooms.size(); i++) {

            Room r = rooms.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"roomId\":").append(r.getRoomId()).append(",")
                .append("\"roomNumber\":\"").append(escape(r.getRoomNumber())).append("\",")
                .append("\"roomType\":\"").append(escape(r.getRoomType())).append("\",")
                .append("\"dailyPrice\":").append(r.getDailyPrice()).append(",")
                .append("\"maintenanceStatus\":").append(r.isMaintenanceStatus())
                .append("}");
        }

        return json.append("]").toString();
    }

    // GUEST JSON
    private static String guestsToJson(List<Guest> guests) {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < guests.size(); i++) {

            Guest g = guests.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"guestId\":").append(g.getGuestId()).append(",")
                .append("\"name\":\"").append(escape(g.getName())).append("\",")
                .append("\"phone\":\"").append(escape(g.getPhone())).append("\",")
                .append("\"email\":\"").append(escape(g.getEmail())).append("\"")
                .append("}");
        }

        return json.append("]").toString();
    }

    // STAY JSON
    private static String staysToJson(List<Stay> stays) {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < stays.size(); i++) {

            Stay s = stays.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"stayId\":").append(s.getStayId()).append(",")
                .append("\"guestId\":").append(s.getGuestId()).append(",")
                .append("\"roomId\":").append(s.getRoomId()).append(",")
                .append("\"checkIn\":\"").append(s.getCheckIn()).append("\",")
                .append("\"checkOut\":")
                .append(s.getCheckOut() == null
                        ? "null" : "\"" + s.getCheckOut() + "\"")
                .append(",")
                .append("\"dailyPrice\":").append(s.getDailyPrice()).append(",")
                .append("\"totalBill\":").append(s.getTotalBill())
                .append("}");
        }

        return json.append("]").toString();
    }

    // MAINTENANCE JSON
    private static String maintenanceToJson(
            List<Maintenance> records) {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < records.size(); i++) {

            Maintenance m = records.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"maintenanceId\":").append(m.getMaintenanceId()).append(",")
                .append("\"roomId\":").append(m.getRoomId()).append(",")
                .append("\"reason\":\"").append(escape(m.getReason())).append("\",")
                .append("\"startDate\":\"").append(m.getStartDate()).append("\",")
                .append("\"endDate\":")
                .append(m.getEndDate() == null
                        ? "null" : "\"" + m.getEndDate() + "\"")
                .append(",")
                .append("\"status\":\"").append(escape(m.getStatus())).append("\"")
                .append("}");
        }

        return json.append("]").toString();
    }

    // DASHBOARD JSON
    private static String dashboardToJson(
            Map<String, Object> dashboard) {

        StringBuilder json = new StringBuilder("{");

        Map<String, Integer> counts =
                (Map<String, Integer>) dashboard.get("roomCounts");

        json.append("\"roomCounts\":{")
            .append("\"totalRooms\":").append(counts.get("totalRooms")).append(",")
            .append("\"availableRooms\":").append(counts.get("availableRooms")).append(",")
            .append("\"occupiedRooms\":").append(counts.get("occupiedRooms")).append(",")
            .append("\"maintenanceRooms\":").append(counts.get("maintenanceRooms"))
            .append("},");

        json.append("\"recentCheckIns\":");

        json.append(recordsToJson(
                (List<Map<String, String>>) dashboard.get("recentCheckIns")));

        json.append(",\"recentCheckOuts\":");

        json.append(recordsToJson(
                (List<Map<String, String>>) dashboard.get("recentCheckOuts")));

        json.append("}");

        return json.toString();
    }

    // CONVERT ACTIVITY RECORDS TO JSON
    private static String recordsToJson(
            List<Map<String, String>> records) {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < records.size(); i++) {

            Map<String, String> record = records.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                .append("\"stayId\":\"").append(escape(record.get("stayId"))).append("\",")
                .append("\"guestName\":\"").append(escape(record.get("guestName"))).append("\",")
                .append("\"roomNumber\":\"").append(escape(record.get("roomNumber"))).append("\",")
                .append("\"date\":\"").append(escape(record.get("date"))).append("\"")
                .append("}");
        }

        return json.append("]").toString();
    }

    // ESCAPE JSON CHARACTERS
    private static String escape(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    // SEND JSON RESPONSE
    private static void sendJson(
            HttpExchange exchange, String response)
            throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // SEND ERROR RESPONSE
    private static void sendError(
            HttpExchange exchange, int status, String message)
            throws IOException {

        String response = "{\"success\":false,\"message\":\""
                + escape(message) + "\"}";

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // CORS HEADERS
    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "GET, POST, OPTIONS");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type");
    }
}
