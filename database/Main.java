package barnastik.project.database;

import java.sql.SQLException;
import java.util.List;

import static barnastik.project.database.DataBase.*;
import static barnastik.project.database.FileReaderCsv.readCsv;

public class Main {

    public static void main(String[] args) {
        String fileInfo = "./src/barnastik/project/database/stations.csv";
        try {
            dropTable();
            List<List<String>> records = readCsv(fileInfo);
            connectDb();
            createElectionTable();
            fillDb(records);
            System.out.println("Данные об избирательных участках в базе данных:");
            showStations();
            closeDb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
