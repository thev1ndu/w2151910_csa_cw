package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Centralised API logging filter.
 * Logs method, URI, headers on the way in and method, URI, status, duration on
 * the way out.
 *
 * @author thevinduw
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    private static final String START_TIME = "request-start-time";

    @Override
    public void filter(ContainerRequestContext req) {
        // Store the start time so we can calculate duration in the response filter
        req.setProperty(START_TIME, System.currentTimeMillis());

        String method = req.getMethod();
        String uri = req.getUriInfo().getRequestUri().toString();
        String contentType = req.getHeaderString("Content-Type");
        String accept = req.getHeaderString("Accept");

        LOGGER.info(">>> REQUEST  " + method + " " + uri
                + " | Content-Type: " + (contentType != null ? contentType : "N/A")
                + " | Accept: " + (accept != null ? accept : "*/*"));
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) {
        String method = req.getMethod();
        String uri = req.getUriInfo().getRequestUri().toString();
        int status = res.getStatus();
        String statusInfo = res.getStatusInfo() != null
                ? res.getStatusInfo().getReasonPhrase()
                : "Unknown";

        // Calculate elapsed time
        long duration = 0;
        Object startTime = req.getProperty(START_TIME);
        if (startTime != null) {
            duration = System.currentTimeMillis() - (long) startTime;
        }

        LOGGER.info("<<< RESPONSE " + method + " " + uri
                + " | Status: " + status + " " + statusInfo
                + " | Duration: " + duration + "ms");
    }
}