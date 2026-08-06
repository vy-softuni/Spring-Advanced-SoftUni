package app.mendnook.materials.stock;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record StockLevelRequest(
        @Min(value = 0, message = "Stock cannot be negative")
        @Max(value = 100000, message = "Stock is too large")
        int totalUnits
) {
}
