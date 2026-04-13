package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext req) {
        String method = req.getMethod();
        String uri = req.getUriInfo().getRequestUri().toString();
//        LOGGER.info("--- Incoming Request ---");
        LOGGER.info(">>> [INCOMING REQUEST] " + method + " " + uri);
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) {
        String method = req.getMethod();
        String uri = req.getUriInfo().getRequestUri().toString();

        int status = res.getStatus();
        String statusText = (res.getStatusInfo() != null)
                ? res.getStatusInfo().getReasonPhrase()
                : "Unknown";
//        LOGGER.info("--- Outgoing Response ---");
        LOGGER.info("<<< [OUTGOING RESPONSE]" + method + " " + uri
                + " | " + status + " " + statusText);
    }
}