package app.mendnook.hub.material;

import java.time.Instant;
import java.util.UUID;

public record AllocationView(
        UUID id,
        UUID materialId,
        String materialLabel,
        UUID mendRequestId,
        int units,
        String state,
        Instant createdAt,
        Instant changedAt
) {
}
