## Demo

This section is the script for the video demonstration. Follow each step in order — run the `curl` command and verify the expected result.

---

### Step 1: API Discovery

Check the API is running. The discovery endpoint returns metadata (name, version, contact) and HATEOAS links to the main collections. The links are **built dynamically** using `UriInfo` so they adapt to any deployment.

**Expected:** `200 OK` with JSON containing `name`, `version`, `contact`, and `links`.

```bash
curl -i "http://localhost:8080/api/v1"
```

---

### Step 2: List Existing Rooms

Fetch all rooms. The API comes pre-loaded with seed data (`R101`, `R102`).

**Expected:** `200 OK` with a JSON array of room objects.

```bash
curl -i "http://localhost:8080/api/v1/rooms"
```

---

### Step 3: Create a New Room

Create room `LIB-301`. POST returns `201 Created` with a `Location` header built via `UriInfo`.

**Expected:** `201 Created`, `Location: .../api/v1/rooms/LIB-301`, body contains the new room JSON.

```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":40}' \
  "http://localhost:8080/api/v1/rooms"
```

---

### Step 4: Verify the New Room

Fetch `LIB-301` by ID to confirm it was persisted in the in-memory `DataStore`.

**Expected:** `200 OK` with the `LIB-301` room JSON.

```bash
curl -i "http://localhost:8080/api/v1/rooms/LIB-301"
```

---

### Step 5: Error — Sensor in a Non-Existent Room (422)

Try to register a sensor into room `NO-SUCH-ROOM`. This triggers `LinkedResourceNotFoundException` → mapped to **422** via `LinkedResourceNotFoundExceptionMapper`. The response uses the `ErrorMessage` POJO.

**Expected:** `422 Unprocessable Entity` with structured JSON error.

```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"id":"X-1","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"NO-SUCH-ROOM"}' \
  "http://localhost:8080/api/v1/sensors"
```

---

### Step 6: Register a Sensor Successfully

Register temperature sensor `TEMP-001` into `LIB-301`. The room must exist or a 422 is returned (as shown above).

**Expected:** `201 Created`, `Location` header, sensor JSON in body, `LIB-301.sensorIds` now includes `TEMP-001`.

```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":21.5,"roomId":"LIB-301"}' \
  "http://localhost:8080/api/v1/sensors"
```

---

### Step 7: Filter Sensors by Type

Use `@QueryParam("type")` to filter. Only sensors with type `Temperature` are returned.

**Expected:** `200 OK` with a filtered JSON array (includes seed sensor `S001` and newly created `TEMP-001`).

```bash
curl -i "http://localhost:8080/api/v1/sensors?type=Temperature"
```

---

### Step 8: Add a Sensor Reading

POST a reading to the sub-resource `/sensors/TEMP-001/readings`. The server auto-generates `id` if missing and updates `Sensor.currentValue` to `22.3`. This uses the **sub-resource locator** pattern — `SensorResource` delegates to `SensorReadingResource`.

**Expected:** `201 Created` with reading JSON (auto-generated `id`).

```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"timestamp":1713868800000,"value":22.3}' \
  "http://localhost:8080/api/v1/sensors/TEMP-001/readings"
```

---

### Step 9: Get Sensor Readings History

Retrieve all readings for `TEMP-001`. This returns the full history list.

**Expected:** `200 OK` with JSON array of readings.

```bash
curl -i "http://localhost:8080/api/v1/sensors/TEMP-001/readings"
```

---

### Step 10: Error — Reading on a MAINTENANCE Sensor (403)

Try to add a reading to seed sensor `S003` which is in `MAINTENANCE` mode. This triggers `SensorUnavailableException` → mapped to **403 Forbidden** via `SensorUnavailableExceptionMapper`.

**Expected:** `403 Forbidden` with `ErrorMessage` JSON.

```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"timestamp":1713868800000,"value":19.0}' \
  "http://localhost:8080/api/v1/sensors/S003/readings"
```

---

### Step 11: Error — Deleting a Room in Use (409)

Try to delete `LIB-301` while `TEMP-001` is still assigned. This triggers `RoomNotEmptyException` → mapped to **409 Conflict** via `RoomNotEmptyExceptionMapper`. Data integrity is enforced.

**Expected:** `409 Conflict` with `ErrorMessage` JSON.

```bash
curl -i -X DELETE "http://localhost:8080/api/v1/rooms/LIB-301"
```

---

### Step 12: Error — Unsupported Media Type (415)

Send `Content-Type: text/plain` instead of `application/json`. Jersey's `@Consumes(APPLICATION_JSON)` rejects the request before the method body runs.

**Expected:** `415 Unsupported Media Type`.

```bash
curl -i -X POST \
  -H "Content-Type: text/plain" \
  -d 'This is not valid JSON' \
  "http://localhost:8080/api/v1/sensors"
```

---

### Step 13: Delete the Sensor

Delete sensor `TEMP-001`. The `DELETE` endpoint unlinks the sensor from room `LIB-301` (removes from `sensorIds`) and removes it from the `DataStore`. The room's `sensorIds` list is now empty, which is required before the room can be deleted.

**Expected:** `204 No Content`.

```bash
curl -i -X DELETE "http://localhost:8080/api/v1/sensors/TEMP-001"
```

---

### Step 14: Delete the Room (Success)

Now that `LIB-301` has no sensors assigned, the DELETE succeeds. The room is removed from the `DataStore`.

**Expected:** `204 No Content`.

```bash
curl -i -X DELETE "http://localhost:8080/api/v1/rooms/LIB-301"
```

---

### Step 15: DELETE Idempotency Demo

Delete `LIB-301` again. The room no longer exists, so the server returns `404 Not Found`. The server state (room absent) is identical after both calls, proving the operation is **idempotent in terms of state**.

**Expected:** `404 Not Found` with `ErrorMessage` JSON. Server state is the same as after Step 14.

```bash
curl -i -X DELETE "http://localhost:8080/api/v1/rooms/LIB-301"
```

---

**Tools used:** Postman and/or `curl` in the terminal.

The full video was recorded separately and submitted via **Blackboard** as required by the module brief.
