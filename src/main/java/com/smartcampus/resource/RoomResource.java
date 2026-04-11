package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private static final Logger LOG = Logger.getLogger(RoomResource.class.getName());

    @GET
    public List<Room> getAll() {
        LOG.info("getAll rooms called");
        List<Room> rooms = new ArrayList<>(DataStore.rooms().values());
        LOG.info("Returning " + rooms.size() + " rooms");
        return rooms;
    }

    @POST
    public Response create(Room room) {
        LOG.info("create room called with id=" + room.getId());
        DataStore.rooms().put(room.getId(), room);
        LOG.info("Room created with id=" + room.getId());
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response get(@PathParam("roomId") String roomId) {
        LOG.info("get room called with id=" + roomId);
        Room room = DataStore.rooms().get(roomId);
        if (room == null) {
            LOG.warning("Room " + roomId + " not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        LOG.info("Room " + roomId + " found, returning details");
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response delete(@PathParam("roomId") String roomId) {
        LOG.info("delete room called with id=" + roomId);
        Room room = DataStore.rooms().get(roomId);
        if (room == null) {
            LOG.warning("Room " + roomId + " not found, cannot delete");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!room.getSensorIds().isEmpty()) {
            LOG.warning("Room " + roomId + " still has sensors, cannot delete");
            throw new RoomNotEmptyException("Room " + roomId + " still has active sensors");
        }
        DataStore.rooms().remove(roomId);
        LOG.info("Room " + roomId + " deleted successfully");
        return Response.noContent().build();
    }
}
