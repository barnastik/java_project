package barnastik.project.classes;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

public class ElectionSimulation {
    private final Set<PollingStation> pollingStations;

    public ElectionSimulation() {
        this.pollingStations = new CopyOnWriteArraySet<>();
    }

    public void addPollingStation(String stationName, String address, int capacity, int currentLoad) {
        PollingStation station = new PollingStation(stationName, address, capacity, currentLoad);
        pollingStations.add(station);
    }

    public void updateVotersCount(String stationName, int newVotersCount) {
        for (PollingStation station : pollingStations) {
            if (station.getStationName().equals(stationName)) {
                synchronized (station) {
                    station.setVotersCount(newVotersCount);
                }
                break;
            }
        }
    }

    public Set<PollingStation> getPollingStations() {
        return Collections.unmodifiableSet(pollingStations);
    }

    public static void main(String[] args) {
        ElectionSimulation service = new ElectionSimulation();

        generatePollingStations(service);
        scheduleUpdateTasks(service);
    }

    private static void generatePollingStations(ElectionSimulation service) {
        service.addPollingStation("Участок1", "Улица 1", 200, 100);
        service.addPollingStation("Участок2", "Улица 2", 150, 80);
        service.addPollingStation("Участок3", "Улица 3", 180, 120);
    }

    private static void scheduleUpdateTasks(ElectionSimulation service) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
            System.out.println("\nГенерация запроса на обновление данных об участках...");
            generateUpdateRequests(service);
        }, 0, 1, TimeUnit.SECONDS);

        executor.schedule(() -> {
            System.out.println("\nМоделирование завершено.");
            executor.shutdown();
        }, 5, TimeUnit.SECONDS);
    }

    private static void generateUpdateRequests(ElectionSimulation service) {
        for (PollingStation station : service.getPollingStations()) {
            int newVotersCount = station.getCurrentLoad() + (int) (Math.random() * 10);
            service.updateVotersCount(station.getStationName(), newVotersCount);
            System.out.println("Данные обновлены для " + station.getStationName() +
                    ". Новое количество избирателей: " + newVotersCount);
        }
    }

}
