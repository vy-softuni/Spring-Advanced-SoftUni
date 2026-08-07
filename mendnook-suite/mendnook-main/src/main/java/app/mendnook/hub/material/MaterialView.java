package app.mendnook.hub.material;

import java.util.UUID;

public record MaterialView(
        UUID id,
        String label,
        String family,
        String unitName,
        int totalUnits,
        int heldUnits,
        int availableUnits
) {
}
