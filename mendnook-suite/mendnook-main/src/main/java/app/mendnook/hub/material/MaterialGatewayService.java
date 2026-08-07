package app.mendnook.hub.material;

import app.mendnook.hub.mend.MendRequest;
import app.mendnook.hub.mend.MendRequestService;
import app.mendnook.hub.mend.MendState;
import app.mendnook.hub.shared.DomainRuleException;
import app.mendnook.hub.shared.TrackedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MaterialGatewayService {

    private static final Logger log = LoggerFactory.getLogger(MaterialGatewayService.class);

    private final MaterialsApiClient client;
    private final MendRequestService mendRequestService;

    public MaterialGatewayService(MaterialsApiClient client,
                                  MendRequestService mendRequestService) {
        this.client = client;
        this.mendRequestService = mendRequestService;
    }

    @Cacheable(value = "materialCatalog", key = "#query == null ? '' : #query.toLowerCase()")
    public List<MaterialView> findMaterials(String query) {
        return client.findMaterials(query == null || query.isBlank() ? null : query.trim());
    }

    public List<AllocationView> findAllocations(UUID mendRequestId, String actorEmail, boolean privileged) {
        mendRequestService.findVisible(mendRequestId, actorEmail, privileged);
        return client.findAllocations(mendRequestId);
    }

    @TrackedAction("material-allocation")
    @CacheEvict(value = "materialCatalog", allEntries = true)
    public AllocationView reserve(UUID mendRequestId, MaterialHoldForm form, String ownerEmail) {
        MendRequest mendRequest = mendRequestService.findVisible(mendRequestId, ownerEmail, false);
        if (mendRequest.getState() != MendState.SUBMITTED && mendRequest.getState() != MendState.ASSIGNED) {
            throw new DomainRuleException("Materials cannot be reserved for a closed request");
        }
        AllocationView allocation = client.createAllocation(
                new AllocationRequest(form.getMaterialId(), mendRequestId, form.getUnits()));
        log.info("function=reserve-material allocationId={} mendRequestId={}", allocation.id(), mendRequestId);
        return allocation;
    }

    @TrackedAction("material-release")
    @CacheEvict(value = "materialCatalog", allEntries = true)
    public void release(UUID allocationId, UUID mendRequestId, String actorEmail, boolean privileged) {
        mendRequestService.findVisible(mendRequestId, actorEmail, privileged);
        client.releaseAllocation(allocationId);
        log.info("function=release-material allocationId={} mendRequestId={}", allocationId, mendRequestId);
    }

    @TrackedAction("material-consumption")
    @CacheEvict(value = "materialCatalog", allEntries = true)
    @PreAuthorize("hasAuthority('MANAGE_MATERIALS')")
    public AllocationView consume(UUID allocationId) {
        AllocationView allocation = client.consumeAllocation(allocationId);
        log.info("function=consume-material allocationId={}", allocationId);
        return allocation;
    }

    @TrackedAction("material-restock")
    @CacheEvict(value = "materialCatalog", allEntries = true)
    @PreAuthorize("hasAuthority('MANAGE_MATERIALS')")
    public MaterialView replaceStock(UUID materialId, StockLevelForm form) {
        MaterialView material = client.replaceStock(materialId, new StockLevelRequest(form.getTotalUnits()));
        log.info("function=replace-material-stock materialId={} totalUnits={}", materialId, form.getTotalUnits());
        return material;
    }
}
