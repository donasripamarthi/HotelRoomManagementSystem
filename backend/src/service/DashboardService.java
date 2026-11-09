
package service;

import dao.DashboardDAO;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardService {

    private DashboardDAO dashboardDAO = new DashboardDAO();

    public Map<String, Object> getDashboardData()
            throws SQLException {

        Map<String, Object> dashboard =
                new LinkedHashMap<String, Object>();

        dashboard.put("roomCounts",
                dashboardDAO.getRoomCounts());

        dashboard.put("recentCheckIns",
                dashboardDAO.getRecentCheckIns());

        dashboard.put("recentCheckOuts",
                dashboardDAO.getRecentCheckOuts());

        return dashboard;
    }
}
