package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author thevinduw
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {

    private static final Logger LOG = Logger.getLogger(SensorResource.class.getName());

    private DataStore store = DataStore.getInstance();

    @Context
    private UriInfo uriInfo;

    // GET all sensors, with optional type filter
    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        LOG.info("Getting all sensors...");
        List<Sensor> all = new ArrayList<>(store.getSensors().values());
        if (type != null && !type.isBlank()) {
            LOG.info("Filtering sensors by type: " + type);
            return all.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }
        return all;
    }

    // POST a new sensor (roomId must reference an existing room)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        LOG.info("Attempting to register new sensor: " + sensor.getId());
        Room room = store.getRoom(sensor.getRoomId());
        if (room == null) {
            String errorMsg = "Room '" + sensor.getRoomId()
                    + "' does not exist. Cannot register a sensor to a non-existent room.";
            LOG.severe(errorMsg);
            throw new LinkedResourceNotFoundException(errorMsg);
        }
        store.addSensor(sensor);
        room.getSensorIds().add(sensor.getId());
        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        LOG.info("Successfully registered sensor: " + sensor.getId());
        return Response.created(location).entity(sensor).build();
    }

    // GET a single sensor by ID
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        LOG.info("Fetching sensor with ID: " + sensorId);
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            LOG.severe("Sensor not found: " + sensorId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        return Response.ok(sensor).build();
    }

    // Sub-resource locator for sensor readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsSubResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}