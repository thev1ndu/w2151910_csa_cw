package com.smartcampus;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author thevinduw
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static HttpServer startServer() {
        ResourceConfig config = new ResourceConfig().packages("com.smartcampus");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            LOG.info("");
            LOG.info("  Smart Campus - Sensor & Room Management API started successfully!");
            LOG.info("---------------------------------------------------------");
            LOG.info("  Discovery : http://localhost:8080/api/v1/");
            LOG.info("  Rooms     : http://localhost:8080/api/v1/rooms");
            LOG.info("  Sensors   : http://localhost:8080/api/v1/sensors");
            LOG.info("---------------------------------------------------------");
            LOG.info("  Press Enter to stop the server...");
            LOG.info("");

            // Wait for user input to terminate the server
            System.in.read();

            LOG.info("Shutting down the server...");
            server.shutdownNow();
            LOG.info("Server shut down successfully.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error starting or running the server", ex);
        }
    }
}