package app.mendnook.hub.material;

import app.mendnook.hub.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "mendnook-materials-api",
        url = "${mendnook.materials-api-url:http://localhost:8081}",
        configuration = FeignSecurityConfig.class
)
interface MaterialsApiClient {

    @GetMapping("/api/material-lots")
    List<MaterialView> findMaterials(@RequestParam(required = false) String query);

    @GetMapping("/api/material-allocations")
    List<AllocationView> findAllocations(@RequestParam UUID mendRequestId);

    @PostMapping("/api/material-allocations")
    AllocationView createAllocation(@RequestBody AllocationRequest request);

    @PutMapping("/api/material-allocations/{id}/consume")
    AllocationView consumeAllocation(@PathVariable UUID id);

    @DeleteMapping("/api/material-allocations/{id}")
    void releaseAllocation(@PathVariable UUID id);

    @PutMapping("/api/material-lots/{id}/stock")
    MaterialView replaceStock(@PathVariable UUID id, @RequestBody StockLevelRequest request);
}
