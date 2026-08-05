package app.mendnook.materials.allocation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AllocationCreateRequest(
        @NotNull(message = "Material identifier is required") UUID materialId,
        @NotNull(message = "Repair request identifier is required") UUID mendRequestId,
        @Min(value = 1, message = "At least one unit must be reserved")
        @Max(value = 100, message = "No more than 100 units may be reserved at once") int units
) {
}
