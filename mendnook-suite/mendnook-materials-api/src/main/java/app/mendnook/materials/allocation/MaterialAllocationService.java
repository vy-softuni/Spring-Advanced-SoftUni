package app.mendnook.materials.allocation;

import app.mendnook.materials.shared.MaterialMissingException;
import app.mendnook.materials.shared.MaterialRuleException;
import app.mendnook.materials.stock.MaterialLot;
import app.mendnook.materials.stock.MaterialLotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaterialAllocationService {

    private static final Logger log = LoggerFactory.getLogger(MaterialAllocationService.class);

    private final MaterialAllocationRepository repository;
    private final MaterialLotService materialLotService;

    public MaterialAllocationService(MaterialAllocationRepository repository,
                                     MaterialLotService materialLotService) {
        this.repository = repository;
        this.materialLotService = materialLotService;
    }

    @Transactional(readOnly = true)
    public List<MaterialAllocationView> findForMendRequest(UUID mendRequestId) {
        return repository.findAllByMendRequestIdOrderByCreatedAtDesc(mendRequestId).stream()
                .map(MaterialAllocationView::from)
                .toList();
    }

    @Transactional
    public MaterialAllocationView create(AllocationCreateRequest request) {
        MaterialLot lot = materialLotService.hold(request.materialId(), request.units());
        MaterialAllocation allocation = repository.save(
                new MaterialAllocation(lot, request.mendRequestId(), request.units()));
        log.info("function=create-allocation allocationId={} mendRequestId={} units={}",
                allocation.getId(), request.mendRequestId(), request.units());
        return MaterialAllocationView.from(allocation);
    }

    @Transactional
    public MaterialAllocationView consume(UUID id) {
        MaterialAllocation allocation = findHeld(id);
        materialLotService.consume(allocation.getMaterialLot(), allocation.getUnits());
        allocation.consume();
        log.info("function=consume-allocation allocationId={} units={}", id, allocation.getUnits());
        return MaterialAllocationView.from(allocation);
    }

    @Transactional
    public void release(UUID id) {
        MaterialAllocation allocation = findHeld(id);
        materialLotService.release(allocation.getMaterialLot(), allocation.getUnits());
        allocation.release();
        log.info("function=release-allocation allocationId={} units={}", id, allocation.getUnits());
    }

    private MaterialAllocation findHeld(UUID id) {
        MaterialAllocation allocation = repository.findById(id)
                .orElseThrow(() -> new MaterialMissingException("Material allocation was not found"));
        if (allocation.getState() != AllocationState.HELD) {
            throw new MaterialRuleException("Only a held material allocation can be changed");
        }
        return allocation;
    }
}
