package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private static final Logger LOG = Logger.getLogger(SensorResource.class.getName());

    @GET
    public List<Sensor> getAll(@QueryParam("type") String type) {
        LOG.info("getAll sensors called, type=" + type);

        List<Sensor> all = new ArrayList<>(DataStore.sensors().values());
        if (type == null || type.isEmpty()) {
            LOG.info("Returning " + all.size() + " sensors without filtering");
            return all;
        }

        List<Sensor> filtered = all.stream()
                .filter(s -> type.equalsIgnoreCase(s.getType()))
                .collect(Collectors.toList());
        LOG.info("Returning " + filtered.size() + " sensors after filtering by type");
        return filtered;
    }

    @POST
    public Response create(Sensor sensor) {
        LOG.info("create sensor called for room " + sensor.getRoomId());

        Room room = DataStore.rooms().get(sensor.getRoomId());
        if (room == null) {
            LOG.warning("Room " + sensor.getRoomId() + " does not exist, cannot create sensor");
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist");
        }
        DataStore.sensors().put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());
        LOG.info("Sensor created with id " + sensor.getId());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource readingsSubResource(@PathParam("sensorId") String sensorId) {
        LOG.info("Creating readings sub-resource for sensorId=" + sensorId);
        return new SensorReadingResource(sensorId);
    }
}
