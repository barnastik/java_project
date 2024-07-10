package barnastik.project.database;

import barnastik.project.classes.ElectionTrafficLightService;

import java.sql.*;
import java.util.List;

public class DataBase {
    private static final String URL = "jdbc:h2:~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    public static Statement statement;
    public static Connection connection;

    public static void dropTable() throws SQLException {
        connectDb();
        statement.executeUpdate("DROP TABLE IF EXISTS voting_station");
        closeDb();
    }

    public static void fillDb(List<List<String>> records) throws SQLException {
        String sql;
        List<List<String>> stations = records.stream().distinct().toList();
        sql = "INSERT INTO voting_station (id, st_name, address, capacity, voters_count) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (List<String> station : stations) {
            if (station.size() >= 5) {
                int id = Integer.parseInt(station.get(0));
                String st_name = station.get(1);
                String address = station.get(2);
                int capacity = Integer.parseInt(station.get(3));
                int votersCount = Integer.parseInt(station.get(4));

                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, st_name);
                preparedStatement.setString(3, address);
                preparedStatement.setInt(4, capacity);
                preparedStatement.setInt(5, votersCount);

                preparedStatement.executeUpdate();
            } else {
                System.err.println("Ошибка в формате записи: " + station);
            }
        }
    }

    public static void connectDb() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        statement = connection.createStatement();
    }

    public static void createElectionTable() throws SQLException {
        DataBase.connectDb();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS voting_station (id INTEGER PRIMARY KEY , st_name VARCHAR NOT NULL, address VARCHAR NOT NULL, capacity INTEGER NOT NULL, voters_count INTEGER NOT NULL)");
        System.out.println("Таблица участков голосования успешно создана.");

    }

    public static void showStations() throws SQLException {
        ResultSet stationResult = statement.executeQuery("SELECT * FROM voting_station");
        System.out.print("TABLE VOTING_STATION\n");
        while (stationResult.next()) {
            System.out.print("ID: " + stationResult.getInt("id"));
            System.out.print(", stationName: " + stationResult.getString("st_name"));
            System.out.print(", Address: " + stationResult.getString("address"));
            System.out.print(", Capacity: " + stationResult.getInt("capacity"));
            System.out.print(", Voters Count: " + stationResult.getInt("voters_count") + "\n");
        }
    }

    public static void addStation(ElectionTrafficLightService service) throws SQLException{
        ResultSet stationResult = statement.executeQuery("SELECT * FROM voting_station");
        while (stationResult.next()) {
            //System.out.print("ID: " + stationResult.getInt("id"));
            String name = stationResult.getString("st_name");
            String address = stationResult.getString("address");
            int capacity = stationResult.getInt("capacity");
            int voters = stationResult.getInt("voters_count");
            service.addPollingStation(name,address, capacity, voters);
        }
    }

    public static void closeDb() throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
