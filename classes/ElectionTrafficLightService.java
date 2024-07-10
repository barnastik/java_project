package barnastik.project.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ElectionTrafficLightService {
    private final List<PollingStation> pollingStations;

    public ElectionTrafficLightService() {
        this.pollingStations = new ArrayList<>();
    }

    public void addPollingStation(String stationName, String address, int capacity, int votersCount) {
        PollingStation newStation = new PollingStation(stationName, address, capacity, votersCount);

        if (!pollingStations.contains(newStation)) {
            pollingStations.add(newStation);
            pollingStations.sort(Collections.reverseOrder());
            System.out.println("Станция '" + stationName + "' успешно добавлена.");
        } else {
            System.out.println("Станция с именем '" + stationName + "' уже существует. Дубликат не добавлен.");
        }
    }

    public synchronized void updateVotersCount(String stationName, int newVotersCount) {
        PollingStation station = findStationByName(stationName);
        if (station != null) {
            station.setVotersCount(newVotersCount);
            pollingStations.sort(Collections.reverseOrder());
        } else {
            System.out.println("Избирательный участок с именем " + stationName + " не найден.");
        }
    }

    private PollingStation findStationByName(String stationName) {
        for (PollingStation station : pollingStations) {
            if (station.getStationName().equals(stationName)) {
                return station;
            }
        }
        return null;
    }

    public void displayTrafficLightStatusReverseOrder() {
        pollingStations.sort(Comparator.comparingDouble(station ->
                (double) station.getCurrentLoad() / station.getCapacity()));
        System.out.println("Статус загруженности избирательных участков:");

        for (int i = 0; i <= pollingStations.size() - 1; i++) {
            PollingStation station = pollingStations.get(i);
            Light stationStatus = station.getTrafficLightStatus();
            String colorCode = switch (stationStatus) {
                case GREEN -> "\u001B[32m"; // Зеленый цвет
                case YELLOW -> "\u001B[33m"; // Желтый цвет
                case RED -> "\u001B[31m"; // Красный цвет
                // Сброс цвета
            };

            System.out.println(colorCode + station.getStationName() + ": " + stationStatus + "\u001B[0m"); // Сброс цвета после вывода
        }
    }

    public void closePollingStation(String stationName) {
        PollingStation station = findStationByName(stationName);
        if (station != null) {
            pollingStations.remove(station);
            System.out.println("Избирательный участок '" + stationName + "' закрыт.");
        } else {
            System.out.println("Избирательный участок с именем " + stationName + " не найден.");
        }
    }

    public void readStationsFromFile(String fileName) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(", ");

                if (parts.length >= 4) {
                    String stationName = parts[0];
                    int capacity = Integer.parseInt(parts[1]);
                    int currentLoad = Integer.parseInt(parts[2]);
                    String address = parts[3];

                    addPollingStation(stationName, address, capacity, currentLoad);
                } else {
                    System.out.println("Некорректный формат строки в файле. Пропуск строки: " + line);
                }
            }

            scanner.close();
            System.out.println("Данные успешно считаны из файла.");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        }
    }

    public String findLessLoadedStationOnStreet(String streetName) {
        PollingStation lessLoadedStation = null;
        int minLoad = Integer.MAX_VALUE;

        for (PollingStation station : pollingStations) {
            if (station.getAddress().contains(streetName)) {
                if (station.getCurrentLoad() < minLoad) {
                    minLoad = station.getCurrentLoad();
                    lessLoadedStation = station;
                }
            }
        }

        assert lessLoadedStation != null;
        return lessLoadedStation.getStationName();
    }

    public void viewIndividualStationInfo(String stationName) {
        PollingStation station = findStationByName(stationName);

        if (station != null) {
            System.out.println("Информация об избирательном участке '" + stationName + "':");
            System.out.println("Название участка: " + station.getStationName());
            System.out.println("Адрес: " + station.getAddress());
            System.out.println("Вместимость: " + station.getCapacity());
            System.out.println("Количество голосующих: " + station.getCurrentLoad());
            System.out.println("Статус светофора: " + station.getTrafficLightStatus());
        } else {
            System.out.println("Избирательный участок с именем " + stationName + " не найден.");
        }
    }

    public void displayTrafficLightStatusByStreet(String streetName) {
        List<PollingStation> stationsOnStreet = new ArrayList<>();

        for (PollingStation station : pollingStations) {
            if (station.getAddress().contains(streetName)) {
                stationsOnStreet.add(station);
            }
        }

        pollingStations.sort(Comparator.comparingDouble(station ->
                (double) station.getCurrentLoad() / station.getCapacity()));

        if (stationsOnStreet.isEmpty()) {
            System.out.println("На улице " + streetName + " нет избирательных участков.");
        } else {
            System.out.println("Статус светофора для избирательных участков на улице " + streetName + ":");

            for (PollingStation station : stationsOnStreet) {
                Light stationStatus = station.getTrafficLightStatus();
                String colorCode = switch (stationStatus) {
                    case GREEN -> "\u001B[32m"; // Зеленый цвет
                    case YELLOW -> "\u001B[33m"; // Желтый цвет
                    case RED -> "\u001B[31m"; // Красный цвет
                };

                System.out.println(station.getStationName() + ": " + colorCode + station.getTrafficLightStatus() + "\u001B[0m");
            }
        }
    }

    public List<PollingStation> getAllPollingStations() {
        return pollingStations;
    }

    public double calculateAverageVotersCount() {
        if (pollingStations.isEmpty()) {
            return 0;
        }

        double totalVotersCount = 0;
        for (PollingStation station : pollingStations) {
            totalVotersCount += station.getCurrentLoad();
        }

        return totalVotersCount / pollingStations.size();
    }

}

