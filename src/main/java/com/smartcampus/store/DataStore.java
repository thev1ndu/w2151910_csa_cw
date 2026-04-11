package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {

    private static DataStore instance = new DataStore();

    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {
        seedData();
    }

    private void seedData() {
        // Add Seed Data
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

    public static DataStore getInstance() {
        return instance;
    }

    // Room methods
    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public Room removeRoom(String id) {
        return rooms.remove(id);
    }

    // Sensor methods
    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    public Sensor removeSensor(String id) {
        return sensors.remove(id);
    }

    // Reading methods
    public List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new CopyOnWriteArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> new CopyOnWriteArrayList<>()).add(reading);
    }
}