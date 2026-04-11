package com.smartcampus;

/**
 * Example curl commands for the Smart Campus API.
 *
 * NOTE: This class is for documentation only and is not used at runtime.
 * Adjust HOST, PORT and CONTEXT if your server differs.
 *
 * Base URL (as configured):
 *   http://localhost:8080/YOUR_CONTEXT/api/v1
 */
public final class CurlExamples {

    private CurlExamples() {
        // no-op
    }

    /*
     * =============================
     * 1. Discovery Endpoint
     * =============================
     *
     * GET /api/v1
     *
     * curl -i http://localhost:8080/YOUR_CONTEXT/api/v1
     */

    /*
     * =============================
     * 2. Room Management
     * =============================
     *
     * 2.1 Create a Room (POST /rooms)
     *
     * curl -i -X POST -H "Content-Type: application/json" -d '{
              "id": "LIB-301",
              "name": "Library Quiet Study",
              "capacity": 40
            }' http://localhost:8080/YOUR_CONTEXT/api/v1/rooms
     *
     * 2.2 List All Rooms (GET /rooms)
     *
     * curl -i \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/rooms
     *
     * 2.3 Get Single Room (GET /rooms/{roomId})
     *
     * curl -i \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/rooms/LIB-301
     *
     * 2.4 Delete Empty Room (DELETE /rooms/{roomId})
     *
     * curl -i -X DELETE \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/rooms/LIB-301
     *
     * 2.5 Attempt to Delete Room with Sensors (expect 409)
     *
     * curl -i -X DELETE \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/rooms/LAB-101
     */

    /*
     * =============================
     * 3. Sensor Management & Filtering
     * =============================
     *
     * 3.1 Register a Sensor (POST /sensors)
     *
     * Precondition: Room with id "LIB-301" exists.
     *
     * curl -i -X POST \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *         "id": "TEMP-001",
     *         "type": "Temperature",
     *         "status": "ACTIVE",
     *         "currentValue": 21.5,
     *         "roomId": "LIB-301"
     *       }' \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/sensors
     *
     * 3.2 Register a Sensor with Missing Room (expect 422)
     *
     * curl -i -X POST \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *         "id": "TEMP-999",
     *         "type": "Temperature",
     *         "status": "ACTIVE",
     *         "currentValue": 20.0,
     *         "roomId": "NON_EXISTENT_ROOM"
     *       }' \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/sensors
     *
     * 3.3 List All Sensors (GET /sensors)
     *
     * curl -i \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/sensors
     *
     * 3.4 Filter Sensors by Type (GET /sensors?type=CO2)
     *
     * curl -i \
     *   "http://localhost:8080/YOUR_CONTEXT/api/v1/sensors?type=CO2"
     */

    /*
     * =============================
     * 4. Sensor Readings (Sub-Resource)
     * =============================
     *
     * 4.1 List Readings for a Sensor (GET /sensors/{id}/readings)
     *
     * curl -i \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/sensors/TEMP-001/readings
     *
     * 4.2 Add Reading for ACTIVE Sensor (POST /sensors/{id}/readings)
     *
     * curl -i -X POST \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *         "timestamp": 1713868800000,
     *         "value": 22.3
     *       }' \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/sensors/TEMP-001/readings
     *
     * 4.3 Add Reading for Sensor in MAINTENANCE (expect 403)
     *
     * Precondition: Sensor TEMP-002 exists with status "MAINTENANCE".
     *
     * curl -i -X POST \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *         "timestamp": 1713868800000,
     *         "value": 19.0
     *       }' \
     *   http://localhost:8080/YOUR_CONTEXT/api/v1/sensors/TEMP-002/readings
     */

    /*
     * =============================
     * 5. Global Error Handling
     * =============================
     *
     * Generic 500 examples depend on triggering an unexpected server-side
     * error (e.g. by temporarily inserting a bug). The key point is that
     * the API returns a JSON body like:
     *
     *   { "message": "Internal server error", "status": 500 }
     *
     * and never exposes a raw Java stack trace to the client.
     */
}
