package app.mendnook.hub.material;

import java.util.UUID;

record AllocationRequest(UUID materialId, UUID mendRequestId, int units) {
}
