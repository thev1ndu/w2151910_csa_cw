package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    private static DataStore instance = new DataStore();

    private Map<String, Room> rooms = new HashMap<>();
    private Map<String, Sensor> sensors = new HashMap<>();
    private Map<String, List<SensorReading>> readings = new HashMap<>();

    private DataStore() {
        seedData();
    }

    public static DataStore getInstance() {
        return instance;
    }

    // SEED DATA
    private synchronized void seedData() {

        Room r1 = new Room("R101", "Lecture Hall A", 100);
        rooms.put(r1.getId(), r1);

        Room r2 = new Room("R102", "Lab 1", 30);
        rooms.put(r2.getId(), r2);

        Sensor s1 = new Sensor("S001", "Temperature", "ACTIVE", "R101");
        s1.setCurrentValue(22.5);
        sensors.put(s1.getId(), s1);
        r1.getSensorIds().add(s1.getId());

        Sensor s2 = new Sensor("S002", "Humidity", "ACTIVE", "R101");
        s2.setCurrentValue(45.0);
        sensors.put(s2.getId(), s2);
        r1.getSensorIds().add(s2.getId());

        Sensor s3 = new Sensor("S003", "Temperature", "MAINTENANCE", "R102");
        s3.setCurrentValue(18.0);
        sensors.put(s3.getId(), s3);
        r2.getSensorIds().add(s3.getId());

        SensorReading sr1 = new SensorReading("R-001", System.currentTimeMillis() - 3600000, 22.0);
        addReading(s1.getId(), sr1);

        SensorReading sr2 = new SensorReading("R-002", System.currentTimeMillis(), 22.5);
        addReading(s1.getId(), sr2);
    }

    // ROOM METHODS
    public synchronized Map<String, Room> getRooms() {
        return new HashMap<>(rooms); // defensive copy
    }

    public synchronized Room getRoom(String id) {
        return rooms.get(id);
    }

    public synchronized void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public synchronized Room removeRoom(String id) {
        return rooms.remove(id);
    }

    // SENSOR METHODS
    public synchronized Map<String, Sensor> getSensors() {
        return new HashMap<>(sensors);
    }

    public synchronized Sensor getSensor(String id) {
        return sensors.get(id);
    }

    // IMPORTANT: atomic operation across structures
    public synchronized void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);

        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().add(sensor.getId());
        }
    }

    public synchronized Sensor removeSensor(String id) {
        Sensor removed = sensors.remove(id);

        if (removed != null) {
            Room room = rooms.get(removed.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(id);
            }
        }

        return removed;
    }

    // READING METHODS
    public synchronized List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }

    public synchronized void addReading(String sensorId, SensorReading reading) {
        List<SensorReading> list = readings.get(sensorId);

        if (list == null) {
            list = new ArrayList<>();
            readings.put(sensorId, list);
        }

        list.add(reading);
    }
}
