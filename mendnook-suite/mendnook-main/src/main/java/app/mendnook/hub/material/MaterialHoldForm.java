package app.mendnook.hub.material;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class MaterialHoldForm {

    @NotNull(message = "Choose a material")
    private UUID materialId;

    @Min(value = 1, message = "Reserve at least one unit")
    @Max(value = 100, message = "Reserve no more than 100 units at once")
    private int units = 1;

    public UUID getMaterialId() {
        return materialId;
    }

    public void setMaterialId(UUID materialId) {
        this.materialId = materialId;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }
}
