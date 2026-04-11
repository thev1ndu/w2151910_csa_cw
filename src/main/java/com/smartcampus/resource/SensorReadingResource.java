package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    private static final Logger LOG = Logger.getLogger(SensorReadingResource.class.getName());

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getHistory() {
        LOG.info("getHistory called for sensorId=" + sensorId);
        List<SensorReading> history = DataStore.readingsFor(sensorId);
        LOG.info("Returning " + history.size() + " readings for sensorId=" + sensorId);
        return history;
    }

    @POST
    public Response addReading(SensorReading reading) {
        LOG.info("addReading called for sensorId=" + sensorId);
        Sensor sensor = DataStore.sensors().get(sensorId);
        if (sensor == null) {
            LOG.warning("Sensor " + sensorId + " not found when trying to add reading");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            LOG.warning("Sensor " + sensorId + " is under maintenance, cannot add reading");
            throw new SensorUnavailableException("Sensor " + sensorId + " is under maintenance");
        }

        if (reading.getId() == null) {
            reading.setId(UUID.randomUUID().toString());
        }
        DataStore.readingsFor(sensorId).add(reading);

        // side effect: update current value on parent sensor
        sensor.setCurrentValue(reading.getValue());

        LOG.info("Reading created with id " + reading.getId() + " for sensorId=" + sensorId);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
