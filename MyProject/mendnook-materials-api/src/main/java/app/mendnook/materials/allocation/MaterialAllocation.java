package app.mendnook.materials.allocation;

import app.mendnook.materials.stock.MaterialLot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "material_allocations")
public class MaterialAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_lot_id", nullable = false)
    private MaterialLot materialLot;

    @NotNull
    @Column(nullable = false)
    private UUID mendRequestId;

    @Min(1)
    @Column(nullable = false)
    private int units;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AllocationState state;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant changedAt;

    protected MaterialAllocation() {
    }

    public MaterialAllocation(MaterialLot materialLot, UUID mendRequestId, int units) {
        this.materialLot = materialLot;
        this.mendRequestId = mendRequestId;
        this.units = units;
        this.state = AllocationState.HELD;
        this.createdAt = Instant.now();
        this.changedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public MaterialLot getMaterialLot() {
        return materialLot;
    }

    public UUID getMendRequestId() {
        return mendRequestId;
    }

    public int getUnits() {
        return units;
    }

    public AllocationState getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void consume() {
        state = AllocationState.CONSUMED;
        changedAt = Instant.now();
    }

    public void release() {
        state = AllocationState.RELEASED;
        changedAt = Instant.now();
    }
}
