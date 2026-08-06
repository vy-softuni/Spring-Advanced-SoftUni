package app.mendnook.materials.stock;

import app.mendnook.materials.shared.MaterialRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "material_lots", uniqueConstraints = @UniqueConstraint(name = "uk_material_label", columnNames = "label"))
public class MaterialLot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String label;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MaterialFamily family;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String unitName;

    @Min(0)
    @Column(nullable = false)
    private int totalUnits;

    @Min(0)
    @Column(nullable = false)
    private int heldUnits;

    @Version
    private long version;

    protected MaterialLot() {
    }

    public MaterialLot(String label, MaterialFamily family, String unitName, int totalUnits) {
        this.label = label.trim();
        this.family = family;
        this.unitName = unitName.trim();
        this.totalUnits = totalUnits;
        this.heldUnits = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public MaterialFamily getFamily() {
        return family;
    }

    public String getUnitName() {
        return unitName;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public int getHeldUnits() {
        return heldUnits;
    }

    public int getAvailableUnits() {
        return totalUnits - heldUnits;
    }

    public void hold(int units) {
        if (units <= 0 || units > getAvailableUnits()) {
            throw new MaterialRuleException("The requested material quantity is not available");
        }
        heldUnits += units;
    }

    public void release(int units) {
        if (units <= 0 || units > heldUnits) {
            throw new MaterialRuleException("The reserved material quantity cannot be released");
        }
        heldUnits -= units;
    }

    public void consume(int units) {
        if (units <= 0 || units > heldUnits || units > totalUnits) {
            throw new MaterialRuleException("The reserved material quantity cannot be consumed");
        }
        heldUnits -= units;
        totalUnits -= units;
    }

    public void replaceTotalUnits(int totalUnits) {
        if (totalUnits < heldUnits) {
            throw new MaterialRuleException("Total stock cannot be lower than currently reserved stock");
        }
        this.totalUnits = totalUnits;
    }
}
