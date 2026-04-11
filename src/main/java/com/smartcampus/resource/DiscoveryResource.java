package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author thevinduw
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Map<String, Object> discover() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", "Smart Campus API");
        info.put("version", "1.0");
        info.put("description", "Sensor and Room Management API for the university Smart Campus project");

        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("name", "Campus Admin");
        contact.put("email", "admin@smartcampus.ac.uk");
        info.put("contact", contact);

        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        info.put("links", links);

        return info;
    }
}