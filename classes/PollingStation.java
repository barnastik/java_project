package barnastik.project.classes;

import java.util.Objects;

import static barnastik.project.classes.Light.*;

public class PollingStation implements Comparable<PollingStation> {
    private String stationName;
    private int votersCount;
    private String address;
    private int capacity;
    private Light light;

    public PollingStation(String stationName, String address, int capacity, int votersCount) {
        setStationName(stationName);
        setAddress(address);
        setCapacity(capacity);
        setVotersCount(votersCount);
        appDateLight();
    }

    public void setStationName(String stationName) {
        if (stationName == null) {
            throw new IllegalArgumentException("Отсутствует название избирательного пункта");
        }
        this.stationName = stationName;
    }
    public void setAddress(String address) {
        if (address == null) {
            throw new IllegalArgumentException("Отсутствует адрес избирательного пункта");
        }
        this.address = address;
    }
    public void setCapacity(int capacity) {
        if (capacity <= 0 || capacity > 10000) {
            throw new IllegalArgumentException("Значение вместимости должно быть в [0,10000]");
        }
        this.capacity = capacity;
    }
    public void setVotersCount(int votersCount) {
        if (votersCount <= 0 || votersCount > 10000) {
            throw new IllegalArgumentException("Количество голосующих должно быть в [0,10000]");
        }
        if (votersCount > this.capacity) {
            throw new IllegalArgumentException("Количество голосующих не может быть больше вместимости");
        }
        this.votersCount = votersCount;
    }

    private static final double HIGH_LOAD_THRESHOLD = 0.8;
    private static final double MEDIUM_LOAD_THRESHOLD = 0.6;
    public void appDateLight() {
        double loadRatio = (double) votersCount / capacity;

        if (loadRatio >= HIGH_LOAD_THRESHOLD) {
            this.light = RED;
        } else if (loadRatio >= MEDIUM_LOAD_THRESHOLD) {
            this.light = YELLOW;
        } else {
            this.light = GREEN;
        }
    }

    public String getStationName() { return stationName; }
    public int getCapacity() { return capacity; }
    public int getCurrentLoad() { return votersCount; }
    public String getAddress() { return address; }
    public Light getLight() { return light; }
    public Light getTrafficLightStatus() {
        return light;
    }

    @Override
    public int compareTo(PollingStation other) {
        return Integer.compare(this.votersCount, other.votersCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollingStation that = (PollingStation) o;
        return votersCount == that.votersCount &&
                Objects.equals(stationName, that.stationName) &&
                Objects.equals(address, that.address) &&
                capacity == that.capacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationName, address, capacity, votersCount);
    }

}