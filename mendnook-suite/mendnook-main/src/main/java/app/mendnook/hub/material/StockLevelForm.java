package app.mendnook.hub.material;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class StockLevelForm {

    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 100000, message = "Stock is too large")
    private int totalUnits;

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }
}
