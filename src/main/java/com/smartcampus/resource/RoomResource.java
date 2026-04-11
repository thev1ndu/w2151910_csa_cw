package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.Level;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

    private static final Logger LOG = Logger.getLogger(RoomResource.class.getName());

    private DataStore store = DataStore.getInstance();

    @Context
    private UriInfo uriInfo;

    // GET all rooms
    @GET
    public Collection<Room> getAllRooms() {
        LOG.info("Fetching all rooms.");
        return new ArrayList<>(store.getRooms().values());
    }

    // POST a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        LOG.info("Attempting to create room: " + room.getId());
        store.addRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        LOG.info("Successfully created room: " + room.getId());
        return Response.created(location).entity(room).build();
    }

    // GET a single room by ID
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        LOG.info("Fetching room with ID: " + roomId);
        Room room = store.getRoom(roomId);
        if (room == null) {
            LOG.severe("Room not found: " + roomId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found: " + roomId + "\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE a room (cannot delete if sensors are still assigned)
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        LOG.info("Attempting to delete room with ID: " + roomId);
        Room room = store.getRoom(roomId);
        if (room == null) {
            LOG.info("Room not found for deletion, returning no content: " + roomId);
            return Response.noContent().build();
        }
        if (!room.getSensorIds().isEmpty()) {
            String errorMsg = "Room " + roomId + " still has " + room.getSensorIds().size() + " sensor(s) assigned. Remove all sensors before deleting the room.";
            LOG.severe(errorMsg);
            throw new RoomNotEmptyException(errorMsg);
        }
        store.removeRoom(roomId);
        LOG.info("Successfully deleted room: " + roomId);
        return Response.noContent().build();
    }
}