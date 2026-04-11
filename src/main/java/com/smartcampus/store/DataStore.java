package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.*;

/**
 * Simple in-memory data store using regular maps.
 */
public class DataStore {

    private static final Map<String, Room> ROOMS = new HashMap<>();
    private static final Map<String, Sensor> SENSORS = new HashMap<>();
    private static final Map<String, List<SensorReading>> READINGS = new HashMap<>();

    public static Map<String, Room> rooms() {
        return ROOMS;
    }

    public static Map<String, Sensor> sensors() {
        return SENSORS;
    }

    public static List<SensorReading> readingsFor(String sensorId) {
        return READINGS.computeIfAbsent(sensorId, id -> Collections.synchronizedList(new ArrayList<>()));
    }
}
