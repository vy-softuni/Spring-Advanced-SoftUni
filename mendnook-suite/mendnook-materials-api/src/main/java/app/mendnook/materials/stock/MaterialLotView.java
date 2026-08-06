package app.mendnook.materials.stock;

import java.util.UUID;

public record MaterialLotView(
        UUID id,
        String label,
        String family,
        String unitName,
        int totalUnits,
        int heldUnits,
        int availableUnits
) {

    public static MaterialLotView from(MaterialLot materialLot) {
        return new MaterialLotView(
                materialLot.getId(),
                materialLot.getLabel(),
                materialLot.getFamily().name(),
                materialLot.getUnitName(),
                materialLot.getTotalUnits(),
                materialLot.getHeldUnits(),
                materialLot.getAvailableUnits()
        );
    }
}
