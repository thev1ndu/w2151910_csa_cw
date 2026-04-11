package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    private static final Logger LOG = Logger.getLogger(DiscoveryResource.class.getName());

    @GET
    public Response discovery() {
        LOG.info("discovery endpoint called");
        Map<String, Object> root = new HashMap<>();
        root.put("version", "v1");
        root.put("contact", "smartcampus-api@university.example");

        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        root.put("links", links);

        LOG.info("discovery endpoint returning links: " + root.keySet());
        return Response.ok(root).build();
    }
}
