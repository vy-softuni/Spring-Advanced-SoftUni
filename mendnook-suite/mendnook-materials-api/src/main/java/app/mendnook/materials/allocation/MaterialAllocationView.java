package app.mendnook.materials.allocation;

import java.time.Instant;
import java.util.UUID;

public record MaterialAllocationView(
        UUID id,
        UUID materialId,
        String materialLabel,
        UUID mendRequestId,
        int units,
        String state,
        Instant createdAt,
        Instant changedAt
) {

    public static MaterialAllocationView from(MaterialAllocation allocation) {
        return new MaterialAllocationView(
                allocation.getId(),
                allocation.getMaterialLot().getId(),
                allocation.getMaterialLot().getLabel(),
                allocation.getMendRequestId(),
                allocation.getUnits(),
                allocation.getState().name(),
                allocation.getCreatedAt(),
                allocation.getChangedAt()
        );
    }
}
