package com.smartcampus;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * JAX-RS application configuration for the Smart Campus API.
 *
 * Exposes all resources under the versioned base path /api/v1 and
 * configures Jersey to scan the com.smartcampus package for resource
 * classes, exception mappers, and filters.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        // Scan this base package for JAX-RS components
        packages("com.smartcampus");
    }
}
