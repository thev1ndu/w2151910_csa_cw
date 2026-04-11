package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorMessage;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author thevinduw
 */
@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private static final Logger LOG = Logger.getLogger(SensorReadingResource.class.getName());

    private String sensorId;
    private DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET all readings for this sensor
    @GET
    public Response getReadings() {
        LOG.info("Fetching readings for sensor ID: " + sensorId);
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            LOG.severe("Sensor not found when fetching readings: " + sensorId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Not Found", 404, "Sensor not found: " + sensorId))
                    .build();
        }
        List<SensorReading> readings = store.getReadings(sensorId);
        LOG.info("Successfully fetched " + readings.size() + " readings for sensor: " + sensorId);
        return Response.ok(readings).build();
    }

    // POST a new reading for this sensor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        LOG.info("Attempting to add reading for sensor ID: " + sensorId);
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            LOG.severe("Sensor not found when adding reading: " + sensorId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Not Found", 404, "Sensor not found: " + sensorId))
                    .build();
        }

        // Cannot post readings to a sensor in MAINTENANCE mode
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            String errorMsg = "Sensor '" + sensorId + "' is in MAINTENANCE mode and cannot accept readings.";
            LOG.severe(errorMsg);
            throw new SensorUnavailableException(errorMsg);
        }

        // Auto-generate ID and timestamp if missing
        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        store.addReading(sensorId, reading);

        // Update the sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        LOG.info("Successfully added reading (value: " + reading.getValue() + ") to sensor: " + sensorId);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}