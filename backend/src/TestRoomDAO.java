import dao.RoomDAO;
import model.Room;
import java.util.List;

public class TestRoomDAO {

    public static void main(String[] args) {

        try {
            RoomDAO dao = new RoomDAO();

            List<Room> rooms = dao.getAllRooms();

            System.out.println("HOTEL ROOM DETAILS");
            System.out.println("-------------------------");

            for (Room room : rooms) {
                room.displayRoom();
                System.out.println("-------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}