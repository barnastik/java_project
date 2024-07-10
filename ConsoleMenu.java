package barnastik.project;

import barnastik.project.classes.ElectionTrafficLightService;
import barnastik.project.classes.PollingStation;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static barnastik.project.database.DataBase.*;
import static barnastik.project.database.FileReaderCsv.readCsv;

public class ConsoleMenu {

    public static void main(String[] args) throws SQLException {
        ElectionTrafficLightService service = new ElectionTrafficLightService();
        Scanner scanner = new Scanner(System.in);

        int choice;
        displayMainMenu();
        do {
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addPollingStation(service, scanner);
                case 2 -> updateVotersCount(service, scanner);
                case 3 -> closePollingStation(service, scanner);
                case 4 -> displayTrafficLightStatus(service);
                case 5 -> searchLessLoadedStationOnStreet(service, scanner);
                case 6 -> service.readStationsFromFile("./src/barnastik/project/files/station.txt");
                case 7 -> viewIndStationInfo(service, scanner);
                case 8 -> displayTrafficLightStatusByStreet(service, scanner);
                case 9 -> viewDataBase();
                case 10 -> addStationFromBD(service);
                case 11 -> viewAllStations(service);
                case 12 -> AverageVotersCount(service);
                case 0 -> System.out.println("Выход из программы.");
                default -> System.out.println("Некорректный ввод. Пожалуйста, выберите действие из меню.");
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n===== Меню =====");
        System.out.println("1. Добавить избирательный участок");
        System.out.println("2. Обновить данные о загруженности участка");
        System.out.println("3. Закрыть избирательный участок");
        System.out.println("4. Светофор загруженности по всем участкам");
        System.out.println("5. Поиск наименее загруженного участка на улице");
        System.out.println("6. Считать данные об участках из файла");
        System.out.println("7. Посмотреть информацию о конкретном участке");
        System.out.println("8. Светофор участков на конкретной улице");
        System.out.println("9. Вывести информацию об участках из базы данных");
        System.out.println("10. Записать информацию об участках из базы данных");
        System.out.println("11. Вывести информацию о всех участках");
        System.out.println("12. Среднее кол-во голосущих по участкам");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private static void addPollingStation(ElectionTrafficLightService service, Scanner scanner) {
        System.out.print("Введите название избирательного участка: ");
        String stationName = scanner.nextLine();
        System.out.print("Введите адрес: ");
        String address = scanner.nextLine();
        System.out.print("Введите вместимость: ");
        int capacity = scanner.nextInt();
        System.out.print("Введите количество избирателей на участке в данный момент: ");
        int votersCount = scanner.nextInt();
        service.addPollingStation(stationName, address, capacity, votersCount);
    }

    private static void updateVotersCount(ElectionTrafficLightService service, Scanner scanner) {
        System.out.print("Введите название избирательного участка для обновления данных: ");
        String stationName = scanner.nextLine();
        System.out.print("Введите количество избирателей на участке в данный момент: ");
        int newVotersCount = scanner.nextInt();
        service.updateVotersCount(stationName, newVotersCount);
        System.out.println("Данные обновлены.");
    }

    private static void displayTrafficLightStatus(ElectionTrafficLightService service) {
        service.displayTrafficLightStatusReverseOrder();
    }

    private static void searchLessLoadedStationOnStreet(ElectionTrafficLightService service, Scanner scanner) {
        System.out.print("Введите название улицы для поиска менее загруженного участка: ");
        String streetName = scanner.nextLine();
        service.findLessLoadedStationOnStreet(streetName);
        System.out.print("Наименее загруженный часток: " + service.findLessLoadedStationOnStreet(streetName));
    }

    private static void closePollingStation(ElectionTrafficLightService service, Scanner scanner) {
        System.out.print("Введите имя избирательного участка для закрытия: ");
        String stationName = scanner.nextLine();
        service.closePollingStation(stationName);
    }

    private static void viewIndStationInfo(ElectionTrafficLightService service, Scanner scanner){
        System.out.print("Введите имя избирательного участка для просмотра информации: ");
        String stationName = scanner.nextLine();
        service.viewIndividualStationInfo(stationName);
    }

    private static void displayTrafficLightStatusByStreet(ElectionTrafficLightService service, Scanner scanner) {
        System.out.print("Введите название улицы для просмотра светофора участков: ");
        String streetName = scanner.nextLine();
        service.displayTrafficLightStatusByStreet(streetName);
    }

    private static void viewDataBase() {
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

    private static void addStationFromBD(ElectionTrafficLightService service) {
        String fileInfo = "./src/barnastik/project/database/stations.csv";
        try {
            dropTable();
            List<List<String>> records = readCsv(fileInfo);
            connectDb();
            createElectionTable();
            fillDb(records);
            System.out.println("Данные об избирательных участках в базе данных:");
            addStation(service);
            closeDb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Данные считаны с базы данных.");
    }

    public static void viewAllStations(ElectionTrafficLightService service) {
        List<PollingStation> stations = service.getAllPollingStations();

        if (stations.isEmpty()) {
            System.out.println("Список избирательных участков пуст.");
        } else {
            System.out.println("Информация об избирательных участках:");
            for (PollingStation station : stations) {
                System.out.println("Название участка: " + station.getStationName());
                System.out.println("Адрес: " + station.getAddress());
                System.out.println("Вместимость: " + station.getCapacity());
                System.out.println("Количество голосующих: " + station.getCurrentLoad());
                System.out.println("Статус светофора: " + station.getTrafficLightStatus());
                System.out.println("-----------------------");
            }
        }
    }

    public static void AverageVotersCount(ElectionTrafficLightService service){
        double averageVotersCount = service.calculateAverageVotersCount();
        System.out.println("Среднее количество голосующих на участках: " + averageVotersCount);
    }
}


